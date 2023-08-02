package com.roomies.api.service;

import com.roomies.api.enums.RateLimitStatus;
import com.roomies.api.model.session.BlockedEntity;
import com.roomies.api.model.session.RequestContext;
import com.roomies.api.repository.mongo.BlockedEntityRepository;
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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
class RateLimiterServiceTest {
    @Mock
    BlockedEntityRepository repository;
    @Mock
    Utils utils;

    @InjectMocks
    RateLimiterService rateLimiterService;

    @BeforeEach
    public void setup() {
        List<BlockedEntity> blockedEntities = new ArrayList<>();
        blockedEntities.add(new BlockedEntity(UUID.randomUUID().toString(),null,"1.2.3.4", List.of("UserAgent1"), LocalDateTime.now().toEpochSecond(ZoneOffset.UTC), "Exceeded requests"));
        lenient().when(repository.findAll()).thenReturn(blockedEntities);
        rateLimiterService.setup();
    }

    @Test
    void resetSessionLimit() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(Utils.getRealIp(request)).thenReturn("1.2.3.5");

        rateLimiterService.resetSessionLimit(request);

        assertEquals(1, RateLimiterService.getRateLimitHashMap().size());
        RequestContext context = RateLimiterService.getRateLimitHashMap().get("1.2.3.5");
        assertEquals(1, context.getAttempts());
    }

    @Test
    void checkForAcceptableRequest() {
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
    }

    @Test
    void checkForBlockedIp() {
        assertTrue(rateLimiterService.checkForBlockedIp("1.2.3.5"));
        assertFalse(rateLimiterService.checkForBlockedIp("1.2.3.4"));
    }
}