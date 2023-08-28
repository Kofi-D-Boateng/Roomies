package com.roomies.api.repository.redis;

import com.roomies.api.model.roommate.Roommate;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoommateRedisRepository extends KeyValueRepository<Roommate, String> {
}
