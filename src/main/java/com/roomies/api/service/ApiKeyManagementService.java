package com.roomies.api.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class ApiKeyManagementService {

    public static Map<String,Object> keyCache = new HashMap<>();

    private static final long DEFAULT_EXPIRATION_TIME = 60 * 10; // 10 minutes
    private static final long DEFAULT_REFRESH_TOKEN_EXPIRATION = 60 * 60 * 24 * 90; // 90 days

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    /**
     *
     * @return String[2] where index 0 is the access token and index 1 is the refresh token
     */
    public String[] generateAccessAndRefreshToken(){
        String accessToken = UUID.randomUUID().toString();
        String refreshToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(accessToken,accessToken,DEFAULT_EXPIRATION_TIME, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(refreshToken,refreshToken,DEFAULT_REFRESH_TOKEN_EXPIRATION, TimeUnit.SECONDS);
        return new String[]{accessToken,refreshToken};
    }

    /**
     *
     * @param key String - the apikey generated when logging in
     * @return boolean - whether the key is still present or not.
     */
    public boolean validateApiKey(String key){
        return redisTemplate.opsForValue().get(key) != null;
    }

    /**
     *
     * @param key - the access token to be removed from the cache
     */
    public void removeApiKey(String key){
        redisTemplate.delete(key);
    }

    /**
     *
     * @return String key - a new access token to use with the api
     */
    public String generateNewAccessKey(){
        String accessToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(accessToken,accessToken,DEFAULT_EXPIRATION_TIME, TimeUnit.SECONDS);
        return accessToken;
    }

}
