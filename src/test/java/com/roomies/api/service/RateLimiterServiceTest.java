package com.roomies.api.service;

import com.roomies.api.enums.RateLimitStatus;
import com.roomies.api.model.geolocation.IPAddressInfo;
import com.roomies.api.model.session.RequestContext;
import com.roomies.api.repository.mongo.IPAddressInfoRepository;
import com.roomies.api.util.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
class RateLimiterServiceTest {
    IPAddressInfo ipAddressInfo;
    @Mock
    IPAddressInfoRepository repository;
    @Mock
    Utils utils;

    @InjectMocks
    RateLimiterService rateLimiterService;

    @BeforeEach
    public void setup() {
        List<IPAddressInfo> blockedEntities = new ArrayList<>();
         ipAddressInfo = new IPAddressInfo();
        ipAddressInfo.setId(UUID.randomUUID().toString());
        ipAddressInfo.setIp("1.2.3.4");
        ipAddressInfo.setBlockedDate(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        ipAddressInfo.setReason( "Exceeded requests");
        ipAddressInfo.setRoommate(null);
        ipAddressInfo.setUserAgents(List.of("UserAgent1"));
        blockedEntities.add(ipAddressInfo);
        lenient().when(repository.findAll()).thenReturn(blockedEntities);
        rateLimiterService.setup();
    }

    @Test
    void resetSessionLimit() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(Utils.getRealIp(request)).thenReturn("1.2.3.5");

        rateLimiterService.resetSessionLimit(request);

        assertEquals(2, RateLimiterService.getRateLimitHashMap().size());
        RequestContext context = RateLimiterService.getRateLimitHashMap().get("1.2.3.5");
        assertEquals(1, context.getAttempts());
        System.out.println(RateLimiterService.getBlockedIpSet());
    }

    @Test
    void checkForAcceptableRequest() throws InterruptedException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(Utils.getRealIp(request)).thenReturn("1.2.3.5");

        RateLimitStatus status;
        RequestContext context = null;
        ConcurrentHashMap<String,RequestContext> r = RateLimiterService.getRateLimitHashMap();

        for (int i = 0 ; i < RateLimiterService.MAX_ATTEMPTS; i++) {
            status = rateLimiterService.checkForAcceptableRequest(request);
            assertEquals(RateLimitStatus.ACCEPTABLE, status);
        }

        status = rateLimiterService.checkForAcceptableRequest(request);
        assertEquals(RateLimitStatus.EXCEEDED_THRESHOLD, status);
        status = rateLimiterService.checkForAcceptableRequest(request);
        int attempts = r.get("1.2.3.5").getExceededAttempts();
        for(int i = attempts;i <= RateLimiterService.MAX_ATTEMPTS_BEFORE_BAN;i++) status = rateLimiterService.checkForAcceptableRequest(request);

        assertEquals(RateLimitStatus.BLOCKED, status);
        when(Utils.getRealIp(request)).thenReturn("1.2.3.3");
        for(int i = 0; i < 4;i++){
            status = rateLimiterService.checkForAcceptableRequest(request);
            assertEquals(RateLimitStatus.ACCEPTABLE, status);
            if(i == 2) while(r.get("1.2.3.3").getTimestamp() > LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        }
        assertEquals(1, (int) r.get("1.2.3.3").getAttempts());
    }

    @Test
    void checkForBlockedIp() {
        lenient().when(repository.findByIp("1.2.3.5")).thenReturn(Optional.of(ipAddressInfo));
        System.out.println(RateLimiterService.getBlockedIpSet());
        assertTrue(rateLimiterService.checkForBlockedIp("1.2.3.5"));
        assertFalse(rateLimiterService.checkForBlockedIp("1.2.3.4"));
    }
}