package com.roomies.api.util.external.google;

import com.google.gson.Gson;
import com.roomies.api.util.external.google.interfaces.GoogleMap;
import com.roomies.api.util.external.google.results.GeocodingResult;
import com.roomies.api.util.httpClient.HttpClientSingleton;
import com.roomies.api.util.parsing.GoogleParser;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Component
public class GoogleMaps implements GoogleMap {
    private static final String BASE_URL = "https://maps.googleapis.com/";
    private static final String ADDRESS_VALIDATION_BASE_URL = "https://addressvalidation.googleapis.com/";
    private static final Map<String,String> streetAbbreviations = getStreetAbbreviations();

    @Value("${com.roomies.google.key}")
    private String GOOGLE_API_KEY;
    private static final Gson gson = new Gson();
    @Autowired
    GoogleParser googleParser;

    @Override
    public AddressResponse validateAddress(@NonNull ValidationRequest validationRequest) {
        if(GOOGLE_API_KEY == null){
            log.error("API Key for external request to google is missing... terminating request");
            return null;
        }
        OkHttpClient client = HttpClientSingleton.getClient();
        HttpUrl.Builder builder = baseUrl(ADDRESS_VALIDATION_BASE_URL,"v1:validateAddress?");
        if(builder == null){
            log.trace("HTTP Builder could be created.... ");
            return null;
        }
        HttpUrl url = builder.addQueryParameter("key",GOOGLE_API_KEY).build();
        String stringedJson = gson.toJson(validationRequest);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),stringedJson);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        try(Response response = client.newCall(request).execute()){
            if(!response.isSuccessful() || response.body() == null){
                String advice = "CHECK ERROR HANDLING ON GOOGLE MAPS PAGE";
            }
            return googleParser.parseAddressResponse(response.body().byteStream());
        }catch (IOException e){
            log.trace("There was an error with the request to google's api...");
            return null;
        }
    }

    @Override
    public GeocodingResult lookupAddressByName(@NonNull String address) {
        if(GOOGLE_API_KEY == null){
            log.error("API Key for external request to google is missing... terminating request");
            return null;
        }
        OkHttpClient client = HttpClientSingleton.getClient();
        HttpUrl.Builder builder = baseUrl(BASE_URL,"maps","api","geocode","json");
        if(builder == null){
            log.trace("HTTP Builder could be created.... ");
            return null;
        }
        HttpUrl url = builder.addQueryParameter("key",GOOGLE_API_KEY)
                .addQueryParameter("address",address)
                .build();

        Request request = new Request.Builder().url(url).build();
        try(Response response = client.newCall(request).execute()){
            if(response.isSuccessful() && response.body() != null){
                return googleParser.parseGeocodeResults(response.body().byteStream());
            }
            return null;
        }catch (IOException e){
            log.trace("There was an error with the request to google's api...");
            return null;
        }
    }

    @Override
    public GeocodingResult lookupAddressByCoords(@NonNull Double longitude, @NonNull Double latitude) {
        if(GOOGLE_API_KEY == null){
            log.error("API Key for external request to google is missing... terminating request");
            return null;
        }
        OkHttpClient client = HttpClientSingleton.getClient();
        HttpUrl.Builder builder = baseUrl(BASE_URL,"maps","api","geocode","json");
        if(builder == null){
            log.trace("HTTP Builder could be created.... ");
            return null;
        }
        HttpUrl url = builder.addQueryParameter("key",GOOGLE_API_KEY)
                .addQueryParameter("latlng",latitude + "," + longitude)
                .build();

        Request request = new Request.Builder().url(url).get().build();
        try(Response response = client.newCall(request).execute()){
            if(response.isSuccessful() && response.body() != null){
                return googleParser.parseGeocodeResults(response.body().byteStream());
            }
            return null;
        }catch (IOException e){
            log.trace("There was an error with the request to google's api...");
            return null;
        }
    }

    @Override
    public List<Prediction> addressAutocompletion(String address) {
        log.info("Starting address completion for {} with google...",address);
        if(GOOGLE_API_KEY == null){
            log.error("API Key for external request to google is missing... terminating request");
            return null;
        }
        OkHttpClient client = HttpClientSingleton.getClient();
        HttpUrl.Builder builder = baseUrl(BASE_URL,"maps","api","place","autocomplete","json");
        HttpUrl url = builder
                .addQueryParameter("input",address)
                .addQueryParameter("types","geocode")
                .addQueryParameter("key",GOOGLE_API_KEY.trim())
                .build();
        Request request = new Request.Builder().url(url).get().build();
        try(Response response = client.newCall(request).execute()) {
            if(response.isSuccessful() && response.body() != null){
                return googleParser.parsePredictions(response.body().byteStream());
            }
        } catch (IOException e) {
            log.trace("There was an error with the request to google's api...");
        }
        return null;
    }

    protected HttpUrl.Builder baseUrl(String base,String ... path){
        if(base == null) return null;
        HttpUrl url = HttpUrl.parse(base);
        if(url == null) return null;
        HttpUrl.Builder builder = url.newBuilder();
        for(String segment:path) builder.addPathSegment(segment);
        return builder;
    }

    @Override
    public String buildSearchableString(String[] stringParts) {
        log.info("Building String from parts {}", Arrays.toString(stringParts));
        return Arrays.stream(stringParts)
                .map(part -> part.contains(",") ? part.substring(0,part.trim().length()-1) :part.trim())
                .map(part2 -> part2.split(" "))
                .flatMap(Arrays::stream)
                .map(p -> streetAbbreviations.getOrDefault(p,p))
                .filter(p -> !(p.trim().length() == 3 && p.contains("USA")) && !(p.trim().length() == 5 && p.matches("\\d+")))
                .collect(Collectors.joining(" "));
    }

    private static Map<String, String> getStreetAbbreviations() {
        Map<String, String> abbreviations = new HashMap<>();

        // Add street suffixes and their abbreviations to the map
        abbreviations.put("Avenue", "Ave");
        abbreviations.put("Boulevard", "Blvd");
        abbreviations.put("Circle", "Cir");
        abbreviations.put("Court", "Ct");
        abbreviations.put("Drive", "Dr");
        abbreviations.put("Lane", "Ln");
        abbreviations.put("Road", "Rd");
        abbreviations.put("Street", "St");
        abbreviations.put("Terrace", "Ter");
        abbreviations.put("Way", "Way");
        abbreviations.put("Parkway", "Pkwy");
        abbreviations.put("Place", "Pl");
        abbreviations.put("Square", "Sq");
        abbreviations.put("Alley", "Aly");
        abbreviations.put("Loop", "Loop");
        abbreviations.put("Crescent", "Cres");
        abbreviations.put("Cove", "Cove");
        abbreviations.put("Heights", "Hts");
        abbreviations.put("Expressway", "Expy");
        abbreviations.put("Mews", "Mews");
        abbreviations.put("Promenade", "Prom");
        abbreviations.put("Crossing", "Xing");
        abbreviations.put("Trail", "Trl");
        abbreviations.put("Green", "Green");
        abbreviations.put("Plaza", "Plz");
        abbreviations.put("Ridge", "Rdg");
        abbreviations.put("Landing", "Lndg");
        abbreviations.put("Point", "Pt");
        abbreviations.put("Pass", "Pass");
        abbreviations.put("Walk", "Walk");

        return abbreviations;
    }
}
