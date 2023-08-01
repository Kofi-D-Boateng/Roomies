package com.roomies.api.repository.redis;

import com.roomies.api.model.RoommateRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoommateRequestRedisRepository extends MongoRepository<RoommateRequest,String> {
}
