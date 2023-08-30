package com.roomies.api.repository.mongo;

import com.roomies.api.model.roommate.Location;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends MongoRepository<Location,String> {

    @Query("{ $and: [ { 'latitude': { $gte: ?0, $lte: ?1 } }, { 'longitude': { $gte: ?2, $lte: ?3 } } ] }")
    Optional<List<Location>> findLocationsWithinRange(double minLat, double maxLat, double minLon, double maxLon);
}
