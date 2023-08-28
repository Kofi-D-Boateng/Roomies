package com.roomies.api.repository.redis;

import com.roomies.api.model.roommate.RoommateRequest;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoommateRequestRedisRepository extends KeyValueRepository<RoommateRequest,String> {
}
