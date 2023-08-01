package com.roomies.api.repository.redis;

import com.roomies.api.model.Location;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRedisRepository extends MongoRepository<Location,String> {
}
