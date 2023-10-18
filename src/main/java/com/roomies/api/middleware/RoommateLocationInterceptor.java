package com.roomies.api.middleware;

import com.google.gson.Gson;
import com.roomies.api.model.geolocation.GeolocationRequest;
import com.roomies.api.model.geolocation.IPAddressInfo;
import com.roomies.api.repository.mongo.IPAddressInfoRepository;
import com.roomies.api.util.Utils;
import com.roomies.api.util.external.ip2location.Ip2Location;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Component
public class RoommateLocationInterceptor extends OncePerRequestFilter {
    @Autowired
    IPAddressInfoRepository ipAddressInfoRepository;
    @Autowired
    Ip2Location ip2Location;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Conducting reverse IP lookup for current session....");
        if(!ip2Location.apiKeyIsSet()){
//            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            log.error("Api Key is not present in geolocation filter.... ");
            return;
        }
        String ip = Utils.getRealIp(request);
        if(ip != null){
            IPAddressInfo ipAddressInfo = ip2Location.reverseLookup(ip);
            if(ipAddressInfo != null) ipAddressInfoRepository.save(ipAddressInfo);
            else{
                log.warn("There was an issue when performing reverse dns look up via external API...");
            }
        }else{
            log.error("IP was not generated for the following request.... ");
        }
        filterChain.doFilter(request,response);
    }
}
