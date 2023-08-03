package com.roomies.api.middleware;

import com.google.gson.Gson;
import com.roomies.api.model.geolocation.GeolocationRequest;
import com.roomies.api.model.geolocation.IPAddressInfo;
import com.roomies.api.repository.mongo.IPAddressInfoRepository;
import com.roomies.api.util.Utils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
@Slf4j
public class RoommateLocationInterceptor extends OncePerRequestFilter {

    private static final String GEOLOCATION_DOMAIN = "https://api.ip2location.io/?";
    @Value("com.roomies.geolocation.key")
    private String apiKey;
    @Autowired
    IPAddressInfoRepository ipAddressInfoRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(apiKey == null){
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            log.error("Api Key is not present in geolocation filter.... ");
            return;
        }
        String ip = Utils.getRealIp(request);
        if(ip != null){
            IPAddressInfo ipAddressInfo = reverseLookup(ip);
            if(ipAddressInfo != null) ipAddressInfoRepository.save(ipAddressInfo);
            else{
                log.warn("There was an issue when performing reverse dns look up via external API...");
            }
        }else{
            log.error("IP was not generated for the following request.... ");
        }
        filterChain.doFilter(request,response);
    }

    /**
     *
     * @param ip - The ip of the user making the request
     * @return Object - The object returned will be a map that should be cast to a map that can hold multiple types.
     * @throws IOException - throws an IOException if http call is not resolved.
     */
    private IPAddressInfo reverseLookup(String ip) throws IOException {
        GeolocationRequest.Builder builder = new GeolocationRequest.Builder();
        builder.withIp(ip).withFormat("json");
        GeolocationRequest geolocationRequest = builder.build();
        String query = String.format("key=%s&ip=%s&format=%s",apiKey,geolocationRequest.getIp(),geolocationRequest.getFormat());
        String url = GEOLOCATION_DOMAIN + query;
        OkHttpClient client = new OkHttpClient();
        Request httpRequest = new Request.Builder().url(url).get().build();

        try(Response response = client.newCall(httpRequest).execute()){
            if(response.isSuccessful() && response.body() != null){
                return new Gson().fromJson(response.body().toString(), IPAddressInfo.class);
            }else{
                return null;
            }
        }catch (IOException e){
            log.trace("Error was catch when performing API call.... ");
            return null;
        }
    }
}
