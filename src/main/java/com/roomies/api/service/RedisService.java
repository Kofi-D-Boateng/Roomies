package com.roomies.api.service;

import com.roomies.api.interfaces.Operations;
import com.roomies.api.model.Roommate;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Service
public class RedisService implements Operations {

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    public void saveToCache(String key, Object object){
        log.info("Saving object: {} to the redis",object.getClass().getName());
        redisTemplate.opsForValue().set(key,object);
        log.info("Object saved to cache");
    }
    public void saveAllToCache(@NonNull Map<String, Object> cacheableItemMap) {
        cacheableItemMap.forEach((key,object) ->{
            log.info("Saving object: {} to the redis",object.getClass().getName());
            redisTemplate.opsForValue().set(key,object);
            log.info("Object saved to cache");
        });
    }

    public Optional<Object> retrieveFromCache(String key){
        log.info("Retrieving roommate class for key: {}",key);
        Object redisObject = redisTemplate.opsForValue().get(key);
        if(redisObject != null){
            log.info("Object retrieved: {}",redisObject);
            return Optional.of((Roommate) redisObject);
        }
        log.warn("Roommate not found for key: {}",key);
        return Optional.empty();
    }
    public void flushFromCache(String key) {
        log.info("Removing object with key: {} from redis",key);
        redisTemplate.delete(key);
        log.info("Removed object from cache");
    }

    public String generateCacheKey(Object object, String keyPart) {
        String className = object.getClass().getName();
        if(!className.contains(".")){
            log.warn("Class name was not passed to ");
            return keyPart;
        }
        String[] nameArr = className.split("\\.");
        String key = nameArr[nameArr.length-1]  + ":" + keyPart;
        log.info("Generated Key {} for cache...",key);
        return key;
    }
}
