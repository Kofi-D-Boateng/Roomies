package com.roomies.api.repository.mongo;

import com.roomies.api.model.geolocation.IPAddressInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPAddressInfoRepository extends MongoRepository<IPAddressInfo,String> {

    @Query("{ip:?0}")
    Optional<IPAddressInfo> findByIp(String ip);
}
