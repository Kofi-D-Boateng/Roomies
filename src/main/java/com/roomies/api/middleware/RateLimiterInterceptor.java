package com.roomies.api.middleware;

import com.roomies.api.enums.RateLimitStatus;
import com.roomies.api.service.RateLimiterService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class RateLimiterInterceptor extends OncePerRequestFilter {

    @Autowired
    RateLimiterService rateLimiter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        RateLimitStatus status = rateLimiter.checkForAcceptableRequest(request);
        if(status != RateLimitStatus.ACCEPTABLE){
            switch (status){
                case EXCEEDED_THRESHOLD -> response.sendError(HttpStatus.TOO_MANY_REQUESTS.value());
                case BLOCKED -> response.sendError(HttpStatus.FORBIDDEN.value());
                case NOT_FOUND -> response.sendError(HttpStatus.NO_CONTENT.value());
            }
        }
        filterChain.doFilter(request,response);
    }
}
