package com.roomies.api.repository.redis;

import com.roomies.api.model.roommate.Preference;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreferenceRedisRepository extends KeyValueRepository<Preference,String> {
}
