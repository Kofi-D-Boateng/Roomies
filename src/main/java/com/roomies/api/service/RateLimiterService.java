package com.roomies.api.service;

import com.roomies.api.enums.RateLimitStatus;
import com.roomies.api.interfaces.Limiter;
import com.roomies.api.model.session.BlockedEntity;
import com.roomies.api.model.session.RequestContext;
import com.roomies.api.repository.mongo.BlockedEntityRepository;
import com.roomies.api.util.Utils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
public class RateLimiterService implements Limiter {
    private static final ConcurrentHashMap<String, RequestContext> rateLimitHashMap = new ConcurrentHashMap<>();
    private static final Set<String> blockedIpSet = new HashSet<>();
    protected static final Long MAX_ATTEMPTS = 20L;
    protected static final Long EXTENDED_BY = 5L;
    protected static final Long MAX_ATTEMPTS_BEFORE_BAN = 100L;

    @Autowired
    private final BlockedEntityRepository blockedEntityRepository;


    public void setup() {
        List<BlockedEntity> blockedEntityList = blockedEntityRepository.findAll();
        if(blockedEntityList.size() == 0) return;
        blockedEntityList.stream().parallel().map(BlockedEntity::getIp).forEach(blockedIpSet::add);
    }

    public static ConcurrentHashMap<String, RequestContext> getRateLimitHashMap(){return rateLimitHashMap;}

    public static Set<String> getBlockedIpSet(){return blockedIpSet;}

    @Override
    public void resetSessionLimit(HttpServletRequest request) {
        String ip = Utils.getRealIp(request);
        if(!rateLimitHashMap.containsKey(ip)){
            RequestContext requestContext = new RequestContext(LocalDateTime.now().plusSeconds(EXTENDED_BY).toEpochSecond(ZoneOffset.UTC),0,0);
            rateLimitHashMap.putIfAbsent(ip,requestContext);
            return;
        }
        rateLimitHashMap.get(ip).resetContext(LocalDateTime.now().plusSeconds(EXTENDED_BY).toEpochSecond(ZoneOffset.UTC));
    }

    @Override
    public RateLimitStatus checkForAcceptableRequest(HttpServletRequest request) {
        String ip = Utils.getRealIp(request);
        if(!rateLimitHashMap.containsKey(ip)) createRequestContextEntry(ip);
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
            blockIp(ip,request.getHeader("User-Agent"));
            return RateLimitStatus.BLOCKED;
        }
        return RateLimitStatus.EXCEEDED_THRESHOLD;
    }

    @Override
    public boolean checkForBlockedIp(String ip){
        return blockedIpSet.contains(ip);
    }

    @Override
    public void blockIp(String ip, String userAgent) {
        BlockedEntity entity = new BlockedEntity();
        entity.setIp(ip);
        entity.getUserAgents().add(userAgent);
        entity.setBlockedDate(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        entity.setReason("Exceed too many request when being rate limited.");
        blockedIpSet.add(ip);
        blockedEntityRepository.save(entity);
    }

    @Override
    public void clearRequestContext(String ip) {
        rateLimitHashMap.remove(ip);
    }

    @Override
    public void clearAllRequestContext() {
        rateLimitHashMap.clear();
    }

    private void createRequestContextEntry(String ip){rateLimitHashMap.putIfAbsent(ip,new RequestContext(LocalDateTime.now().plusSeconds(EXTENDED_BY).toEpochSecond(ZoneOffset.UTC),0,0));}
}
