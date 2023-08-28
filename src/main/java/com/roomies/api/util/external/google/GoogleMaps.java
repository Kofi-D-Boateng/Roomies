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
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Component
public class GoogleMaps implements GoogleMap {
    private static final String BASE_URL = "https://maps.googleapis.com/";
    private static final String ADDRESS_VALIDATION_BASE_URL = "https://addressvalidation.googleapis.com/";

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
            if(!response.isSuccessful()){
                String advice = "CHECK ERROR HANDLING ON GOOGLE MAPS PAGE";
            }
            return googleParser.parseGeocodeResults(response.body().byteStream());
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
            if(!response.isSuccessful()){
                String advice = "CHECK ERROR HANDLING ON GOOGLE MAPS PAGE";
            }
            return googleParser.parseGeocodeResults(response.body().byteStream());
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
//        log.info("Google endpoint: {}",url.toString());
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
}
