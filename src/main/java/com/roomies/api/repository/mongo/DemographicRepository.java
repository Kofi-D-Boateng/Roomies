package com.roomies.api.repository.mongo;

import com.roomies.api.model.Demographic;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemographicRepository extends MongoRepository<Demographic,String> {
}
