package com.roomies.api.repository.redis;

import com.roomies.api.model.Demographic;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemographicRedisRepository extends MongoRepository<Demographic,String> {
}
