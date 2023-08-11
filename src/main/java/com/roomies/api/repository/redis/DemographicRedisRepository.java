package com.roomies.api.repository.redis;

import com.roomies.api.model.Demographic;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemographicRedisRepository extends KeyValueRepository<Demographic,String> {
}
