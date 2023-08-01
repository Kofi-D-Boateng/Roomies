package com.roomies.api.repository.redis;

import com.roomies.api.model.Preference;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreferenceRedisRepository extends MongoRepository<Preference,String> {
}
