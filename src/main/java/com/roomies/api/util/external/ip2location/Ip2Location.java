package com.roomies.api.util.external.ip2location;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.roomies.api.util.httpClient.HttpClientSingleton;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@AllArgsConstructor
@NoArgsConstructor
public class Ip2Location implements IPLocator {

    private static final String GEOLOCATION_DOMAIN = "https://api.ip2location.io/?";
    @Value("${com.roomies.ip2location.key}")
    private String apiKey;
    @Autowired
    ObjectMapper objectMapper;

    /**
     *
     * @param ip - The ip of the user making the request
     * @return Object - The object returned will be a map that should be cast to a map that can hold multiple types.
     * @throws IOException - throws an IOException if http call is not resolved.
     */
    public IPAddressInfo reverseLookup(@NonNull String ip) throws IOException {
        GeolocationRequest.Builder builder = new GeolocationRequest.Builder();
        builder.withIp(ip).withFormat("json");
        GeolocationRequest geolocationRequest = builder.build();
        String query = String.format("key=%s&ip=%s&format=%s", apiKey, geolocationRequest.getIp(), geolocationRequest.getFormat());
        String url = GEOLOCATION_DOMAIN + query;
        OkHttpClient client = HttpClientSingleton.getClient();
        Request httpRequest = new Request.Builder().url(url).get().build();

        try (Response response = client.newCall(httpRequest).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return objectMapper.readValue(response.body().byteStream(), IPAddressInfo.class);
            } else {
                return null;
            }
        } catch (IOException e) {
            log.trace("Error was catch when performing API call.... ");
            return null;
        }
    }

    public boolean apiKeyIsSet(){return apiKey != null && apiKey.trim().length() >0;}
}
