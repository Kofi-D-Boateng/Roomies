package com.roomies.api.middleware;

import com.roomies.api.service.ApiKeyManagementService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


public class TokenValidatorInterceptor extends OncePerRequestFilter {

    @Autowired
    ApiKeyManagementService apiKeyManagementService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
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
