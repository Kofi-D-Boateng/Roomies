package com.roomies.api.service;

import com.roomies.api.enums.ServiceResponse;
import com.roomies.api.model.LoginRequest;
import com.roomies.api.model.Roommate;
import com.roomies.api.model.RoommateRequest;
import com.roomies.api.repository.mongo.RoommateRepository;
import com.roomies.api.repository.mongo.RoommateRequestRepository;
import com.roomies.api.repository.redis.RoommateRedisRepository;
import com.roomies.api.util.custom.ResponseTuple;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.util.function.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
public class RoommateService {

    @Autowired
    RoommateRepository roommateRepository;
    @Autowired
    RoommateRedisRepository redisRepository;
    @Autowired
    RoommateRequestRepository roommateRequestRepository;
    @Autowired
    ApiKeyManagementService apiKeyManagementService;
    @Autowired
    BCryptPasswordEncoder encoder;
    public ResponseTuple<ServiceResponse, Optional<Roommate>, String[]> fetchUserInfo(LoginRequest request) {

        Optional<Roommate> optionalRoommate = roommateRepository.findRoommateBy(request.getEmail(), request.getPhoneNumber(), null);

        if(optionalRoommate.isEmpty()){
            Tuple2<ServiceResponse, Optional<Roommate>> Tuple2;
            return new ResponseTuple<>(ServiceResponse.FAULTY_EMAIL,optionalRoommate,null);
        }

        if(!encoder.matches(request.getPassword(), optionalRoommate.get().getPassword())){
            return new ResponseTuple<>(ServiceResponse.FAULTY_PASSWORD,optionalRoommate,null);
        }

        String[] keys = apiKeyManagementService.generateAccessAndRefreshToken();

        return new ResponseTuple<>(ServiceResponse.SUCCESSFUL,optionalRoommate,keys);
    }

    @Cacheable("roommates")
    public ServiceResponse requestRoommate(String userId, String requestId, String message) {
        Optional<Roommate> requestingRoommate = redisRepository.findById(userId);
        Optional<Roommate> requestedRoommate = redisRepository.findById(requestId);

        if(requestingRoommate.isEmpty()){
            requestingRoommate = roommateRepository.findById(userId);
            if(requestingRoommate.isEmpty()) return ServiceResponse.FAULTY_IDENTIFIERS;
        }

        if(requestedRoommate.isEmpty()){
            requestedRoommate = roommateRepository.findById(requestId);
            if(requestedRoommate.isEmpty()) return  ServiceResponse.FAULTY_IDENTIFIERS;
        }

        RoommateRequest request = new RoommateRequest();
        request.setRequestingRoommate(requestingRoommate.get());
        request.setRequestedRoommate(requestedRoommate.get());
        if(message != null) request.setMessage(message.trim());

        requestingRoommate.get().getRoommateRequests().add(request);
        requestedRoommate.get().getRoommateRequests().add(request);

        roommateRequestRepository.save(request);
        roommateRepository.save(requestingRoommate.get());
        roommateRepository.save(requestingRoommate.get());
        redisRepository.save(requestedRoommate.get());
        redisRepository.save(requestingRoommate.get());
        return ServiceResponse.SUCCESSFUL;
    }

    public ServiceResponse removeRequest(String userId, String requestId) {
        Optional<Roommate> roommateOptional = redisRepository.findById(userId);
        Optional<RoommateRequest> roommateRequest = roommateRequestRepository.findById(requestId);

        if(roommateOptional.isEmpty()){
            roommateOptional = roommateRepository.findById(userId);
            if(roommateOptional.isEmpty()) return ServiceResponse.FAULTY_IDENTIFIERS;
        }

        if(roommateRequest.isEmpty()) return ServiceResponse.FAULTY_IDENTIFIERS;

        RoommateRequest request = roommateRequest.get();
        Roommate requestingRoommate = request.getRequestingRoommate();
        Roommate roommate = roommateOptional.get();

        roommate.getRoommateRequests().remove(request);
        requestingRoommate.getRoommateRequests().remove(request);

        roommateRequestRepository.delete(request);
        roommateRepository.saveAll(List.of(roommate,requestingRoommate));

        return ServiceResponse.SUCCESSFUL;
    }
}
