package com.roomies.api.service;

import com.roomies.api.enums.RateLimitStatus;
import com.roomies.api.interfaces.Limiter;
import com.roomies.api.model.geolocation.IPAddressInfo;
import com.roomies.api.model.session.RequestContext;
import com.roomies.api.repository.mongo.IPAddressInfoRepository;
import com.roomies.api.util.Utils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
@Slf4j
public class RateLimiterService implements Limiter {
    private static final ConcurrentHashMap<String, RequestContext> rateLimitHashMap = new ConcurrentHashMap<>();
    private Set<String> blockedIpSet;
    protected static final Long MAX_ATTEMPTS = 15L;
    protected static final Long EXTENDED_BY = 5L;
    protected static final Long MAX_ATTEMPTS_BEFORE_BAN = 100L;
    @Autowired
    private final IPAddressInfoRepository ipAddressInfoRepository;


    void setup() {
        blockedIpSet = new HashSet<>();
        List<IPAddressInfo> blockedEntityList = ipAddressInfoRepository.findAll();
        if(blockedEntityList.size() == 0) return;
        blockedEntityList.stream().parallel().map(IPAddressInfo::getIp).forEach(blockedIpSet::add);
        log.info("Set up for rate-limiting services has finished....");
    }

    public static ConcurrentHashMap<String, RequestContext> getRateLimitHashMap(){return rateLimitHashMap;}

    public Set<String> getBlockedIpSet(){return blockedIpSet;}

    @Override
    public void resetSessionLimit(HttpServletRequest request) {
        String ip = Utils.getRealIp(request);
        if(!rateLimitHashMap.containsKey(ip)){
            RequestContext requestContext = new RequestContext(LocalDateTime.now().plusSeconds(EXTENDED_BY).toEpochSecond(ZoneOffset.UTC),1,0,0,0L);
            rateLimitHashMap.putIfAbsent(ip,requestContext);
            return;
        }
        rateLimitHashMap.get(ip).resetContext(LocalDateTime.now().plusSeconds(EXTENDED_BY).toEpochSecond(ZoneOffset.UTC));
    }

    @Override
    public RateLimitStatus checkForAcceptableRequest(HttpServletRequest request) {
        if(blockedIpSet == null) setup();
        String ip = Utils.getRealIp(request);
        log.info("Checking request rate for {}",ip);
        if(!rateLimitHashMap.containsKey(ip)) createRequestContextEntry(ip);
        if(checkForBlockedIp(ip)) return RateLimitStatus.BLOCKED;
        RequestContext requestContext = rateLimitHashMap.get(ip);
        if(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) >= requestContext.getTimestamp()){
//            log.info("Resetting request context: {}",requestContext);
            resetSessionLimit(request);
//            System.out.println("requestContext = " + requestContext);
            return RateLimitStatus.ACCEPTABLE;
        }
        if(requestContext.getAttempts() < MAX_ATTEMPTS){
            requestContext.incrementAttempts();
//            System.out.println("requestContext = " + requestContext);
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
        Optional<IPAddressInfo> ipAddressInfoOptional = ipAddressInfoRepository.findByIp(ip);
        IPAddressInfo entity;
        if(ipAddressInfoOptional.isEmpty()){
            entity = new IPAddressInfo();
            entity.setIp(ip);
        }else{
            entity = ipAddressInfoOptional.get();
        }
        entity.getUserAgents().add(userAgent);
        entity.setBlockedDate(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        entity.setReason("Exceed too many request when being rate limited.");
        blockedIpSet.add(ip);
        ipAddressInfoRepository.save(entity);
    }

    @Override
    public void clearRequestContext(String ip) {
        rateLimitHashMap.remove(ip);
    }

    @Override
    public void clearAllRequestContext() {
        rateLimitHashMap.clear();
    }

    private void createRequestContextEntry(String ip){rateLimitHashMap.putIfAbsent(ip,new RequestContext(LocalDateTime.now().plusSeconds(EXTENDED_BY).toEpochSecond(ZoneOffset.UTC),0,0,0,0L));}
}
