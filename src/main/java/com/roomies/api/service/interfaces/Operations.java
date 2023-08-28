package com.roomies.api.service.interfaces;

import java.util.Map;
import java.util.Optional;

public interface Operations {

    void saveToCache(String key, Object roommate, Long expiration);
    void saveAllToCache(Map<String,Object> cacheableItemMap, Long expiration);
    boolean valueIsInCache(String key);
    Optional<Object> retrieveFromCache(String key);
    String generateCacheKey(Object object,String key);
    void flushFromCache(String key);

}
