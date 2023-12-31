package com.roomies.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roomies.api.enums.Rating;
import com.roomies.api.enums.RequestStatus;
import com.roomies.api.enums.ServiceResponse;
import com.roomies.api.enums.Update;
import com.roomies.api.model.request.LoginRequest;
import com.roomies.api.model.roommate.Demographic;
import com.roomies.api.model.roommate.Roommate;
import com.roomies.api.model.roommate.RoommateRequest;
import com.roomies.api.repository.mongo.*;
import com.roomies.api.util.custom.ResponseTuple;
import com.roomies.api.util.external.enums.GoogleStatus;
import com.roomies.api.util.external.google.GoogleMaps;
import com.roomies.api.util.external.google.results.GeocodingResult;
import com.roomies.api.util.external.google.results.Location;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.util.function.Tuple2;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class RoommateService {
    private static final long DURATION = 60L;
    private static final String ROOM_GENERATION_TOPIC = "generate-room";
    private static final String ROOM_DESTRUCTION_TOPIC = "delete-room";
    @Autowired
    RoommateRepository roommateRepository;
    @Autowired
    DemographicRepository demographicRepository;
    @Autowired
    LocationRepository locationRepository;
    @Autowired
    PreferenceRepository preferenceRepository;
    @Autowired
    RoommateRequestRepository roommateRequestRepository;
    @Autowired
    RedisService redisService;
    @Autowired
    ApiKeyManagementService apiKeyManagementService;
    @Autowired
    BCryptPasswordEncoder encoder;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;
    @Autowired
    GoogleMaps googleMaps;

    public ServiceResponse acceptRequest(@NonNull String requestId) {
        Optional<RoommateRequest> roommateRequest = roommateRequestRepository.findById(requestId);

        if(roommateRequest.isEmpty()) return ServiceResponse.FAULTY_IDENTIFIERS;

        RoommateRequest request = roommateRequest.get();

        roommateRequest.get().setRequestStatus(RequestStatus.ACCEPTED);
        roommateRequest.get().setAcceptedTimestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        roommateRequestRepository.save(request);
        kafkaTemplate.send(ROOM_GENERATION_TOPIC,requestId);
        return ServiceResponse.SUCCESSFUL;
    }

    // ADD INPUT VALIDATION BELOW

    public ServiceResponse adjustRating(@NonNull String personToRateId, @NonNull String personWhoIsRatingId, @NonNull Rating rating){
        Roommate ratingRoommate;
        Roommate raterRoommate;
//        String key = redisService.generateCacheKey(Roommate.class.getName(),personToRateId);
//        String key1 = redisService.generateCacheKey(Roommate.class.getName(),personWhoIsRatingId);
        Optional<Object> ratingRoommateOptional =  redisService.retrieveFromCache(personToRateId);
        Optional<Object> raterRoommateOptional =  redisService.retrieveFromCache(personWhoIsRatingId);

        if(ratingRoommateOptional.isEmpty()){
            Optional<Roommate> optionalRoommate = roommateRepository.findById(personToRateId);
            if(optionalRoommate.isEmpty()) return ServiceResponse.FAULTY_IDENTIFIERS;
            ratingRoommate = optionalRoommate.get();
        }else{
            ratingRoommate = (Roommate) ratingRoommateOptional.get();
        }

        if(raterRoommateOptional.isEmpty()){
            Optional<Roommate> roommateOptional = roommateRepository.findById(personWhoIsRatingId);
            if(roommateOptional.isEmpty()) return ServiceResponse.FAULTY_IDENTIFIERS;
            raterRoommate = roommateOptional.get();
        }else{
            raterRoommate = (Roommate) raterRoommateOptional.get();
        }

        ratingRoommate.adjustRating(raterRoommate,rating);
        roommateRepository.save(ratingRoommate);
        return ServiceResponse.SUCCESSFUL;
    }

    // ADD INPUT VALIDATION BELOW

    public ServiceResponse blockRoommate(@NonNull String userId, @NonNull String blockingUserId, String reasonForBlocking) {
        Roommate roommate;
        Roommate roommateToBeBlocked;
        Optional<Object> roommateOptional = redisService.retrieveFromCache(userId);
        Optional<Object> blockingRoommateOptional = redisService.retrieveFromCache(blockingUserId);

        if(roommateOptional.isEmpty()){
            Optional<Roommate> optional = roommateRepository.findById(userId);
            if(optional.isEmpty()){
                log.warn("Could not find user with id: {} in redis or mongo...",userId);
                return ServiceResponse.FAULTY_IDENTIFIERS;
            }
            roommate = optional.get();
        }else{
            roommate = (Roommate) roommateOptional.get();
        }

        if(blockingRoommateOptional.isEmpty()){
            Optional<Roommate> optional = roommateRepository.findById(blockingUserId);
            if(optional.isEmpty()){
                log.warn("Could not find user with id: {} in redis or mongo...",userId);
                return ServiceResponse.FAULTY_IDENTIFIERS;
            }
            roommateToBeBlocked = optional.get();
        }else{
            roommateToBeBlocked = (Roommate) blockingRoommateOptional.get();
        }

        roommate.blockRoommate(roommateToBeBlocked,reasonForBlocking);
        List<Roommate> roommates = List.of(roommate,roommateToBeBlocked);
        Map<String,Object> roommateMap = roommates.stream().collect(Collectors.toMap(Roommate::getId, r -> r));
        redisService.saveAllToCache(roommateMap,DURATION);
        return ServiceResponse.SUCCESSFUL;
    }

    public ResponseTuple<ServiceResponse,Roommate,Object> getUserProfile(String id) {
        Optional<Object> optionalRoommate  = redisService.retrieveFromCache(id);
        if(optionalRoommate.isEmpty()){
            Optional<Roommate> user = roommateRepository.findById(id);
            if(user.isEmpty()){
                log.warn("Could not find user with id: {} in redis or mongo...",id);
                return new ResponseTuple<>(ServiceResponse.UNSUCCESSFUL,null,null);
            }
            redisService.saveToCache(user.get().getId(),user,DURATION);
            return new ResponseTuple<>(ServiceResponse.SUCCESSFUL,user.get(),null);
        }

        return new ResponseTuple<>(ServiceResponse.SUCCESSFUL,(Roommate) optionalRoommate.get(),null);
    }

    public ServiceResponse incrementViewership(@NonNull String userId, @NonNull String viewedUserId) {
        Roommate roommate;
        Roommate viewedRoommate;

        Optional<Object> roommateOptional = redisService.retrieveFromCache(userId);
        Optional<Object> viewedRoommateOptional = redisService.retrieveFromCache(viewedUserId);

        if(roommateOptional.isEmpty()){
            Optional<Roommate> optionalRoommateMongo = roommateRepository.findById(userId);
            if(optionalRoommateMongo.isEmpty()){
                log.warn("Could not find user with id: {} in redis or mongo...",userId);
                return ServiceResponse.FAULTY_IDENTIFIERS;
            }
            roommate = optionalRoommateMongo.get();

        }else{
            roommate = (Roommate) roommateOptional.get();
        }

        if(viewedRoommateOptional.isEmpty()){
            Optional<Roommate> viewedRoommateMongo = roommateRepository.findById(viewedUserId);
            if(viewedRoommateMongo.isEmpty()){
                log.warn("Could not find viewerUserId with id: {} in redis or mongo...",viewedUserId);
                return  ServiceResponse.FAULTY_IDENTIFIERS;
            }
            viewedRoommate = viewedRoommateMongo.get();
        }else{
            viewedRoommate = (Roommate) viewedRoommateOptional.get();
        }

        viewedRoommate.getViewersSet().add(roommate);
        redisService.saveToCache(viewedRoommate.getId(),viewedRoommate,DURATION);
        return ServiceResponse.SUCCESSFUL;
    }

    public ResponseTuple<ServiceResponse, Optional<Roommate>, String[]> loginUser(@NonNull LoginRequest request) {

        Optional<Roommate> optionalRoommate = roommateRepository.findByEmail(request.getEmail());

        if(optionalRoommate.isEmpty() || !encoder.matches(request.getPassword(), optionalRoommate.get().getPassword())){
            return new ResponseTuple<>(ServiceResponse.FAULTY_EMAIL_OR_PASSWORD,optionalRoommate,null);
        }
//        System.out.println("optionalRoommate.get() = " + optionalRoommate.get());
        redisService.saveToCache(optionalRoommate.get().getId(),optionalRoommate.get(),DURATION);

        return new ResponseTuple<>(ServiceResponse.SUCCESSFUL,optionalRoommate,apiKeyManagementService.generateAccessAndRefreshToken());
    }

    public ServiceResponse requestRoommate(@NonNull String userId, @NonNull String requestId, String message) {
        Roommate requestingRoommate;
        Roommate requestedRoommate;
        Optional<Object> requestingRoommateOptional = redisService.retrieveFromCache(userId);
        Optional<Object> requestedRoommateOptional = redisService.retrieveFromCache(requestId);

        if(requestingRoommateOptional.isEmpty()){
            Optional<Roommate> optional = roommateRepository.findById(userId);
            if(optional.isEmpty()) {
                log.trace("Could not find user with id: {} in redis or mongo...",userId);
                return ServiceResponse.FAULTY_IDENTIFIERS;
            }
            requestingRoommate = optional.get();
        }else{
            requestingRoommate = (Roommate) requestingRoommateOptional.get();
        }

        if(requestedRoommateOptional.isEmpty()){
            Optional<Roommate> optional = roommateRepository.findById(requestId);
            if(optional.isEmpty()){
                log.trace("Could not find user with id: {} in redis or mongo...",requestId);
                return  ServiceResponse.FAULTY_IDENTIFIERS;
            }
            requestedRoommate = optional.get();
        }else{
            requestedRoommate = (Roommate) requestedRoommateOptional.get();
        }

        if(!requestedRoommate.isAcceptingCoed()){
            Demographic demographicOfRequestingRoommate = requestingRoommate.getDemographics();
            Demographic demographicOfRequestedRoommate = requestedRoommate.getDemographics();
            if(demographicOfRequestedRoommate.getGender() != demographicOfRequestingRoommate.getGender()) return ServiceResponse.GENDER_MISMATCH;
        }

        RoommateRequest request = new RoommateRequest();
        request.setRequestingRoommate(requestingRoommate);
        request.setRequestedRoommate(requestedRoommate);
        request.setRequestStatus(RequestStatus.PENDING);
        if(message != null) request.setMessage(message.trim());
        request.setCreationTimestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));

        requestingRoommate.getRoommateRequests().add(request);
        requestedRoommate.getRoommateRequests().add(request);

        RoommateRequest result = roommateRequestRepository.save(request);
        roommateRepository.saveAll(List.of(requestedRoommate,requestingRoommate));
        List<Roommate> roommates = List.of(requestedRoommate,requestingRoommate);
        Map<String,Object> roommateMap = roommates.stream().collect(Collectors.toMap(Roommate::getId, roommate -> roommate));
        redisService.saveAllToCache(roommateMap,DURATION);
        return ServiceResponse.SUCCESSFUL;
    }

    public ServiceResponse removeRequest(@NonNull String requestId) {
        Optional<RoommateRequest> roommateRequest = roommateRequestRepository.findById(requestId);

        if(roommateRequest.isEmpty()){
            log.trace("Could not find roommate request with id: {} in redis or mongo...",requestId);
            return ServiceResponse.FAULTY_IDENTIFIERS;
        }

        RoommateRequest request = roommateRequest.get();
        Roommate requestingRoommate = request.getRequestingRoommate();
        Roommate roommate = request.getRequestedRoommate();

        roommate.getRoommateRequests().remove(request);
        requestingRoommate.getRoommateRequests().remove(request);

        roommateRequest.get().setRequestStatus(RequestStatus.REJECTED);
        roommateRequest.get().setRejectionTimestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        roommateRequestRepository.save(request);
        roommateRepository.saveAll(List.of(roommate,requestingRoommate));

        redisService.flushFromCache(roommate.getId());
        redisService.flushFromCache(requestingRoommate.getId());
        kafkaTemplate.send(ROOM_GENERATION_TOPIC,requestId);
        return ServiceResponse.SUCCESSFUL;
    }

    // ADD INPUT VALIDATION BELOW

    public ServiceResponse updateUserProfile(@NonNull String id, @NonNull Map<Update,Object> updateObjectMap){
        Roommate roommate;
        Optional<Object> redisOptional = redisService.retrieveFromCache(id);
        if(redisOptional.isEmpty()){
            Optional<Roommate> mongoOptional = roommateRepository.findById(id);
            if(mongoOptional.isEmpty()){
                log.trace("Could not find user with id: {} in redis or mongo...",id);
                return  ServiceResponse.FAULTY_IDENTIFIERS;
            }
            roommate = mongoOptional.get();
        }else{
            roommate = (Roommate) redisOptional.get();
        }


        StringBuilder newAddress = new StringBuilder();
        if(updateObjectMap.containsKey(Update.ADDRESS)) newAddress.append(updateObjectMap.get(Update.ADDRESS)).append(",");
        if(updateObjectMap.containsKey(Update.AREA)) newAddress.append(updateObjectMap.get(Update.AREA)).append(",");
        if(updateObjectMap.containsKey(Update.AREA_CODE)) newAddress.append(updateObjectMap.get(Update.AREA_CODE)).append(",");
        if(updateObjectMap.containsKey(Update.COUNTRY)) newAddress.append(updateObjectMap.get(Update.COUNTRY)).append(",");

        GeocodingResult result = googleMaps.lookupAddressByName(newAddress.toString());
        if(!result.getStatus().equals(GoogleStatus.OK.getValue())){
            log.warn("Google could not find the address to update for user with id: {}.... Will still save address",id);
        }else{
            Location coords = result.getResults().get(0).getGeometry().getLocation();
            roommate.getLocation().setLongitude(coords.getLongitude());
            roommate.getLocation().setLatitude(coords.getLatitude());
        }

        roommate.updateRoommate(updateObjectMap,objectMapper);
        roommateRepository.save(roommate);
        demographicRepository.save(roommate.getDemographics());
        locationRepository.save(roommate.getLocation());
        redisService.saveToCache(id,roommate,DURATION);
        return ServiceResponse.SUCCESSFUL;
    }
}
