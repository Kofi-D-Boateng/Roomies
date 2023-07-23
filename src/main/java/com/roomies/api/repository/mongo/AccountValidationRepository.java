package com.roomies.api.repository.mongo;

import com.roomies.api.model.AccountValidation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountValidationRepository extends MongoRepository<AccountValidation,String> {

    @Query("{verification_token: ?0}")
    Optional<AccountValidation> findVerificationToken(String token);
}
