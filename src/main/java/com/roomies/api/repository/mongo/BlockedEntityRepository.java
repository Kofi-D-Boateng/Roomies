package com.roomies.api.repository.mongo;

import com.roomies.api.model.session.BlockedEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockedEntityRepository extends MongoRepository<BlockedEntity,String> {
}
