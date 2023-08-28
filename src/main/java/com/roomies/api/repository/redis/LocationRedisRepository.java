package com.roomies.api.repository.redis;

import com.roomies.api.model.roommate.Location;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRedisRepository extends KeyValueRepository<Location,String> {
}
