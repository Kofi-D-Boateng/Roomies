package com.roomies.api.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roomies.api.enums.ServiceResponse;
import com.roomies.api.enums.Unit;
import com.roomies.api.model.DTO.MaskedRoommateDTO;
import com.roomies.api.model.request.SearchRequest;
import com.roomies.api.model.roommate.Location;
import com.roomies.api.model.roommate.Roommate;
import com.roomies.api.repository.mongo.DemographicRepository;
import com.roomies.api.repository.mongo.LocationRepository;
import com.roomies.api.repository.mongo.RoommateRepository;
import com.roomies.api.util.custom.AddressNode;
import com.roomies.api.util.custom.AddressTrie;
import com.roomies.api.util.custom.ResponseTuple;
import com.roomies.api.util.external.enums.GoogleStatus;
import com.roomies.api.util.external.google.GoogleMaps;
import com.roomies.api.util.external.google.Prediction;
import com.roomies.api.util.external.google.results.AddressComponent;
import com.roomies.api.util.external.google.results.GeocodingResult;
import com.roomies.api.util.external.google.results.Result;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Service
@Slf4j
public class SearchService {

    private static final long MAXIMUM_QUERY_LIFETIME = 10L; // Each query will be stored in the cache for the value of this in minutes;
    private static final double EARTH_RADIUS_MILES = 3958.8;
    private static final double EARTH_RADIUS_KILOMETERS = 6371.0;
    private static final AddressTrie addressTrie = new AddressTrie();
    @Autowired
    RedisService redisService;
    @Autowired
    LocationRepository locationRepository;
    @Autowired
    RoommateRepository roommateRepository;
    @Autowired
    DemographicRepository demographicRepository;
    @Autowired
    AnalyticService analyticService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    GoogleMaps googleMaps;

    public Set<String> retrieveMatchedPatterns(@NonNull String value){
        String[] addressParts = value.contains(",") ? value.split(",") : value.split(" ");
        String address = googleMaps.buildSearchableString(addressParts);
        log.info("Beginning Search for address string {}",address);
        Set<String> addresses = addressTrie.getMatches(address);
        log.info("Matches for address: {}",addresses);
        if(addresses == null || addresses.size() == 0){
            log.warn("No matches where found in address trie.... sourcing external api....");
            List<Prediction> predictions = googleMaps.addressAutocompletion(value);
            if(predictions == null) return new HashSet<>();
            addresses = predictions.stream().map(Prediction::getDescription).collect(Collectors.toSet());
            for(String word:addresses){
                String insertableAddress = googleMaps.buildSearchableString(word.split(","));
                String[] stringParts = insertableAddress.split(" ");
                if(addressTrie.containsPrefix(insertableAddress)){
                    log.info("Address '{}' is already in Trie... continuing to next word",insertableAddress);
                }else{
                    addressTrie.insert(insertableAddress,0.0,0.0,stringParts[stringParts.length-1]);
                }
            }
        }
        return addresses;
    }

    /**
     *
     * @param request A Search Request generated by the user
     * @return ResponseTuple[ServiceResponse, Set[MaskedRoommateDTO] || null , null] - The response tuple will return contain the gather result for the search of roommates based on what was requested.
     */
    public ResponseTuple<ServiceResponse,Set<MaskedRoommateDTO>,Object> findRoommates(@NonNull String id,@NonNull SearchRequest request){
        request.setAddress(googleMaps.buildSearchableString(request.getAddress().split(" ")));
        String key = request.generateHashKey();
        Set<MaskedRoommateDTO> maskedRoommateDTOSet;
        log.info("Initial Cache check for computed query results for address: {}",request.getAddress());
        Optional<Object> optional = redisService.retrieveFromCache(key);
        if(optional.isPresent()){
            Set<MaskedRoommateDTO> list = objectMapper.convertValue(optional.get(), new TypeReference<Set<MaskedRoommateDTO>>() {});
            return new ResponseTuple<>(ServiceResponse.SUCCESSFUL,list,null);
        }else{
            log.info("Cache Miss.... Beginning query...");
            /**
             * STEP ONE: CHECK ADDRESS TRIE FOR COORDS
             * STEP TWO: IF COORDS ARE NOT THERE USE GOOGLE API TO GET FORMATTED ADDRESS AND COORDINATES THEN ADD TO TRIE
             * STEP TWO: INSERT THESE COORDINATES INTO THE TREE
             * STEP THREE: GENERATE THE MASKED ROOMMATES BY QUERYING FROM MONGODB
             * STEP FOUR: INSERT THE SET WITH THE REQUEST HASHKEY FOR QUICK RETRIEVAL
             * STEP FIVE: RETURN MASKED SET
             */
            Roommate user;
            double lat;
            double lon;
            String locale;
            AddressNode addressNode = addressTrie.getNode(request.getAddress());
            log.info("ADDRESS NODE:{}",addressNode);
            if(addressNode == null || (addressNode.getLatitude() <= 0 && addressNode.getLongitude() <= 0)){
                GeocodingResult result = googleMaps.lookupAddressByName(request.getAddress());
                List<Result> results = result.getResults();
                if(!result.getStatus().equals(GoogleStatus.OK.getValue())) return new ResponseTuple<>(ServiceResponse.UNSUCCESSFUL,null,null);
                com.roomies.api.util.external.google.results.Location locationCoord = results.get(0).getGeometry().getLocation();
                List<AddressComponent> addressComponents = results.get(0).getAddressComponent();
                String parsedAddress = googleMaps.buildSearchableString(results.get(0).getFormattedAddress().split(","));
                locale = addressComponents.get(addressComponents.size()-3).getShortName();
                lat = locationCoord.getLatitude();
                lon = locationCoord.getLongitude();
                if(addressTrie.containsPrefix(parsedAddress)) addressTrie.updateNode(parsedAddress,lat,lon,locale);
                else addressTrie.insert(parsedAddress,lat,lon,locale);
            }else{
                lat = addressNode.getLatitude();
                lon = addressNode.getLongitude();
                locale = addressNode.getLocale();
            }

            double[] latLngCoords = generateBoxBoundaries(lat, lon, request.getDistance(), Objects.equals(locale, "US") ? Unit.IMPERIAL:Unit.METRIC);
            log.info("Querying database for potential roommates....");
            Optional<List<Location>> locationOptional = locationRepository.findLocationsWithinRange(latLngCoords[0],latLngCoords[1],latLngCoords[2],latLngCoords[3]);
            Optional<Object> potentialUser = redisService.retrieveFromCache(id);
            if(potentialUser.isEmpty()){
                Optional<Roommate> potentialUserMongo = roommateRepository.findById(id);
                if(potentialUserMongo.isEmpty()){
                    log.warn("Could not find id: {} when querying mongo or cache....",id);
                    return new ResponseTuple<>(ServiceResponse.UNSUCCESSFUL,null,null);
                }
                else user = potentialUserMongo.get();
            }else{
                user = (Roommate) potentialUser.get();
            }

            if(locationOptional.isEmpty()) return new ResponseTuple<>(ServiceResponse.UNSUCCESSFUL,null,null);

            List<Location> locations = locationOptional.get();
            maskedRoommateDTOSet = locations.stream()
                    .filter(location -> location.getRoommate().isAuthorized()
                            && !Objects.equals(location.getRoommate().getId(), id)
                            && (!location.getRoommate().getBlockedRoommates().containsKey(user) && !user.getBlockedRoommates().containsKey(location.getRoommate()))
                            && calculateDistance(location.getLatitude(),location.getLongitude(),lat,lon,Objects.equals(locale, "US") ? Unit.IMPERIAL:Unit.METRIC) <= request.getDistance())
                    .map(location -> objectMapper.convertValue(location.getRoommate(), MaskedRoommateDTO.class))
                    .collect(Collectors.toSet());
        }


        ResponseTuple<ServiceResponse,Set<MaskedRoommateDTO>,Object> responseTuple = new ResponseTuple<>(ServiceResponse.SUCCESSFUL,maskedRoommateDTOSet,null);
        redisService.saveToCache(key,maskedRoommateDTOSet,MAXIMUM_QUERY_LIFETIME);
        analyticService.processSearch(id,request);
        return responseTuple;
    }

    /**
     * @param latitude Latitude of location
     * @param longitude  Longitude of location
     * @param distance  The maximum distance the user can be within the box
     * @return double[] - Returns an array of boundaries that will create a box around our coordinates represented as [minLat,maxLat,minLon,maxLon]
     */
    private double[] generateBoxBoundaries(double latitude, double longitude, double distance, Unit unit) {
        double earthRadius = unit == Unit.IMPERIAL ? EARTH_RADIUS_MILES : EARTH_RADIUS_KILOMETERS;

        double angularDistance = distance / earthRadius;

        double radianLat = Math.toRadians(latitude);
        double radianLon = Math.toRadians(longitude);

        double minLat = radianLat - angularDistance;
        double maxLat = radianLat + angularDistance;

        double deltaLon = Math.asin(Math.sin(angularDistance) / Math.cos(radianLat));
        double minLon = radianLon - deltaLon;
        double maxLon = radianLon + deltaLon;

        // Convert back to degrees
        minLat = Math.toDegrees(minLat);
        maxLat = Math.toDegrees(maxLat);
        minLon = Math.toDegrees(minLon);
        maxLon = Math.toDegrees(maxLon);

        return new double[]{minLat, maxLat, minLon, maxLon};
    }

    /**
     * @param latitude Latitude of a user's location
     * @param longitude Longitude of a user's location
     * @param latitude1 Latitude of google-searched location
     * @param longitude1 Longitude of google-searched location
     * @param unit Unit to measure in
     * @return distance - The calculated distance from the
     */
    private double calculateDistance(double latitude,double longitude,double latitude1,double longitude1,Unit unit){
        double earthRadius = unit == Unit.IMPERIAL ? EARTH_RADIUS_MILES : EARTH_RADIUS_KILOMETERS;
        double latRad = Math.toRadians(latitude);
        double lonRad = Math.toRadians(longitude);
        double lat1Rad = Math.toRadians(latitude1);
        double lon1Rad = Math.toRadians(longitude1);

        double distanceLongitude = lon1Rad - lonRad;
        double distanceLatitude = lat1Rad - latRad;

        double a = Math.pow(Math.sin(distanceLatitude/2),2) + Math.cos(latRad) * Math.cos(lat1Rad) * Math.pow(Math.sin(distanceLongitude/2),2);
        double c = 2 * Math.atan2(Math.sqrt(a),Math.sqrt(1-a));

        return earthRadius * c;
    }
}
