package com.roomies.api.repository.redis;

import com.roomies.api.model.Roommate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoommateRedisRepository extends CrudRepository<Roommate, String> {
}
