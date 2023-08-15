package com.roomies.api.middleware;

import com.roomies.api.service.ApiKeyManagementService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Component
public class TokenValidatorInterceptor extends OncePerRequestFilter {

    @Autowired
    ApiKeyManagementService apiKeyManagementService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Logging from Token Interceptor....");
        String authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if(!apiKeyManagementService.validateApiKey(authorizationHeader.substring("Bearer ".length()))){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        filterChain.doFilter(request,response);
    }
}
