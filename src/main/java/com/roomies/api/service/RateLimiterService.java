package com.roomies.api.service;

import com.roomies.api.enums.RateLimitStatus;
import com.roomies.api.interfaces.Limiter;
import com.roomies.api.model.session.RequestContext;
import com.roomies.api.util.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService implements Limiter {
    private static final ConcurrentHashMap<String, RequestContext> rateLimitHashMap = new ConcurrentHashMap<>();
    private static final Long MAX_ATTEMPTS = 20L;
    private static final Long EXTENDED_BY = 5L;
    private static final Long MAX_ATTEMPTS_BEFORE_BAN = 100L;

    @Override
    public void resetSessionLimit(HttpServletRequest request) {
        String ip = Utils.getRealIp(request);
        if(!rateLimitHashMap.containsKey(ip)){
            RequestContext requestContext = new RequestContext(LocalDateTime.now().plusSeconds(EXTENDED_BY).toEpochSecond(ZoneOffset.UTC),1,0);
            rateLimitHashMap.putIfAbsent(ip,requestContext);
        }
        rateLimitHashMap.get(ip).resetContext(LocalDateTime.now().plusSeconds(EXTENDED_BY).toEpochSecond(ZoneOffset.UTC));
    }

    @Override
    public RateLimitStatus checkForAcceptableRequest(HttpServletRequest request) {
        String ip = Utils.getRealIp(request);
        if(!rateLimitHashMap.containsKey(ip)) return RateLimitStatus.NOT_FOUND;
        if(checkForBlockedIp(ip)) return RateLimitStatus.BLOCKED;
        RequestContext requestContext = rateLimitHashMap.get(ip);
        if(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) >= requestContext.getTimestamp()){
            requestContext.resetContext(LocalDateTime.now().plusSeconds(EXTENDED_BY).toEpochSecond(ZoneOffset.UTC));
            return RateLimitStatus.ACCEPTABLE;
        }
        if(requestContext.getAttempts() < MAX_ATTEMPTS){
            requestContext.incrementAttempts();
            return RateLimitStatus.ACCEPTABLE;
        }

        if(requestContext.getExceededAttempts() < MAX_ATTEMPTS_BEFORE_BAN){
            requestContext.incrementExceededAttempts();
        }else{
            blockIp(ip,null);
        }
        return RateLimitStatus.EXCEEDED_THRESHOLD;
    }

    @Override
    public boolean checkForBlockedIp(String ip) {
        return false;
    }

    @Override
    public void blockIp(String ip, String userAgent) {

    }

    @Override
    public void clearRequestContext(String ip) {
        rateLimitHashMap.remove(ip);
    }

    @Override
    public void clearAllRequestContext() {
        rateLimitHashMap.clear();
    }
}
