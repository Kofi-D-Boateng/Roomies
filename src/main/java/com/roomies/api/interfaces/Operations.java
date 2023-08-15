package com.roomies.api.interfaces;

import java.util.Map;
import java.util.Optional;

public interface Operations {

    void saveToCache(String key, Object roommate);
    void saveAllToCache(Map<String,Object> cacheableItemMap);
    Optional<Object> retrieveFromCache(String key);
    String generateCacheKey(Object object,String key);
    void flushFromCache(String key);

}
