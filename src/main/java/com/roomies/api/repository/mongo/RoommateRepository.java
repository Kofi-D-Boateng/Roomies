package com.roomies.api.repository.mongo;

import com.roomies.api.model.roommate.Roommate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoommateRepository extends MongoRepository<Roommate, String> {
    Optional<Roommate> findByEmail(String email);
    Optional<Roommate> findRoommateByEmailOrSocialSecurityHash(String email,String uniqueIdHash);
    @Query("{ $or: [{ 'email': ?0 }, { 'phone_number': ?1 } ,{ 'social_security': ?2 } ] }")
    Optional<Roommate> findRoommateBy(String email,Long phoneNumber,String uniqueIdHash);
}
