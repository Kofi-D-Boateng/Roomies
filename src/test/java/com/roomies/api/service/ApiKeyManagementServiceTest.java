package com.roomies.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ApiKeyManagementServiceTest {

    public static String[] keyList = new String[]{UUID.randomUUID().toString(),UUID.randomUUID().toString()};
    private static final long DEFAULT_EXPIRATION_TIME = 60 * 10; // 10 minutes
    private static final long DEFAULT_REFRESH_TOKEN_EXPIRATION = 60 * 60 * 24 * 90; // 90 days

    @Mock
    private RedisTemplate<String,Object> redisService;
    @Mock
    private ValueOperations<String,Object> operations;
    @InjectMocks
    private ApiKeyManagementService apiKeyManagementService;

    @Test
    void generateAccessAndRefreshToken() {
        String k = "Hello";
        when(redisService.opsForValue()).thenReturn(operations);
        lenient().doNothing().when(operations).set(keyList[0],keyList[0] ,DEFAULT_EXPIRATION_TIME,TimeUnit.SECONDS);
        lenient().doNothing().when(operations).set(keyList[1],keyList[1] ,DEFAULT_REFRESH_TOKEN_EXPIRATION,TimeUnit.SECONDS);
        String[] keys = apiKeyManagementService.generateAccessAndRefreshToken();
        int count = 0;
        for(String key: keys){
            assertNotEquals(0,key.trim().length());
            assertNotEquals(keyList[count++].trim(),key.trim());
        }
    }

    @Test
    void validateApiKey() {
        for(String key:keyList){
            when(redisService.opsForValue()).thenReturn(operations);
            when(operations.get(key)).thenReturn(key);
            assertTrue(apiKeyManagementService.validateApiKey(key));
        }
    }

    @Test
    void generateNewAccessKey() {
        when(redisService.opsForValue()).thenReturn(operations);
        lenient().doNothing().when(operations).set(keyList[0],keyList[0] ,DEFAULT_EXPIRATION_TIME,TimeUnit.SECONDS);
        assertFalse(apiKeyManagementService.generateNewAccessKey().isEmpty());
    }
}