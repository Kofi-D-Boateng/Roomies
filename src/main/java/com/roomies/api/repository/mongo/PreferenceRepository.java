package com.roomies.api.repository.mongo;

import com.roomies.api.model.roommate.Preference;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreferenceRepository extends MongoRepository<Preference,String> {
}
