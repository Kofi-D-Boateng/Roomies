package com.roomies.api.repository.mongo;

import com.roomies.api.model.roommate.RoommateRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoommateRequestRepository extends MongoRepository<RoommateRequest,String> {
}
