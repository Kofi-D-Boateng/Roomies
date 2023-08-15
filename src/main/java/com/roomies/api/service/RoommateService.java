package com.roomies.api.service;

import com.roomies.api.enums.Rating;
import com.roomies.api.enums.ServiceResponse;
import com.roomies.api.model.*;
import com.roomies.api.repository.mongo.RoommateRepository;
import com.roomies.api.repository.mongo.RoommateRequestRepository;
import com.roomies.api.util.custom.ResponseTuple;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.util.function.Tuple2;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class RoommateService {
    @Autowired
    RoommateRepository roommateRepository;
    @Autowired
    RoommateRequestRepository roommateRequestRepository;
    @Autowired
    RedisService redisService;
    @Autowired
    ApiKeyManagementService apiKeyManagementService;
    @Autowired
    BCryptPasswordEncoder encoder;

    public ServiceResponse acceptRequest(String requestId) {
        Optional<RoommateRequest> roommateRequest = roommateRequestRepository.findById(requestId);

        if(roommateRequest.isEmpty()) return ServiceResponse.FAULTY_IDENTIFIERS;

        RoommateRequest request = roommateRequest.get();

        roommateRequest.get().setAcceptedRequest(0);
        roommateRequest.get().setAcceptedTimestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        roommateRequestRepository.save(request);

        return ServiceResponse.SUCCESSFUL;
    }

    public ServiceResponse adjustRating(String personToRateId, String personWhoIsRatingId, Rating rating){
        Roommate ratingRoommate;
        Roommate raterRoommate;
//        String key = redisService.generateCacheKey(Roommate.class.getName(),personToRateId);
//        String key1 = redisService.generateCacheKey(Roommate.class.getName(),personWhoIsRatingId);
        Optional<Object> ratingRoommateOptional =  redisService.retrieveFromCache(redisService.generateCacheKey(Roommate.class.getName(),personToRateId));
        Optional<Object> raterRoommateOptional =  redisService.retrieveFromCache(redisService.generateCacheKey(Roommate.class.getName(),personWhoIsRatingId));

        if(ratingRoommateOptional.isEmpty()){
            Optional<Roommate> optionalRoommate = roommateRepository.findById(personToRateId);
            if(optionalRoommate.isEmpty()) return ServiceResponse.FAULTY_IDENTIFIERS;
            ratingRoommate = optionalRoommate.get();
        }else{
            ratingRoommate = (Roommate) ratingRoommateOptional.get();
        }

        if(raterRoommateOptional.isEmpty()){
            Optional<Roommate> roommateOptional = roommateRepository.findById(personWhoIsRatingId);
            if(roommateOptional.isEmpty()) return ServiceResponse.FAULTY_IDENTIFIERS;
            raterRoommate = roommateOptional.get();
        }else{
            raterRoommate = (Roommate) raterRoommateOptional.get();
        }

        ratingRoommate.adjustRating(raterRoommate,rating);
        roommateRepository.save(ratingRoommate);
        return ServiceResponse.SUCCESSFUL;
    }

    public ServiceResponse blockRoommate(String userId, String blockingUserId, String reasonForBlocking) {
        Roommate roommate;
        Roommate roommateToBeBlocked;
        Optional<Object> roommateOptional = redisService.retrieveFromCache(redisService.generateCacheKey(Roommate.class.getName(),userId));
        Optional<Object> blockingRoommateOptional = redisService.retrieveFromCache(redisService.generateCacheKey(Roommate.class.getName(),blockingUserId));

        if(roommateOptional.isEmpty()){
            Optional<Roommate> optional = roommateRepository.findById(userId);
            if(optional.isEmpty()) return ServiceResponse.FAULTY_IDENTIFIERS;
            roommate = optional.get();
        }else{
            roommate = (Roommate) roommateOptional.get();
        }

        if(blockingRoommateOptional.isEmpty()){
            Optional<Roommate> optional = roommateRepository.findById(blockingUserId);
            if(optional.isEmpty()) return ServiceResponse.FAULTY_IDENTIFIERS;
            roommateToBeBlocked = optional.get();
        }else{
            roommateToBeBlocked = (Roommate) blockingRoommateOptional.get();
        }

        roommate.blockRoommate(roommateToBeBlocked,reasonForBlocking);
        List<Roommate> roommates = List.of(roommate,roommateToBeBlocked);
        Map<String,Object> roommateMap = roommates.stream().collect(Collectors.toMap(r -> redisService.generateCacheKey(Roommate.class.getName(),r.getId()),r -> r));
        redisService.saveAllToCache(roommateMap);
        return ServiceResponse.SUCCESSFUL;
    }

    public ResponseTuple<ServiceResponse,Roommate,Object> getUserProfile(String id) {
        Optional<Object> optionalRoommate  = redisService.retrieveFromCache(id);
        if(optionalRoommate.isEmpty()){
            Optional<Roommate> user = roommateRepository.findById(id);
            if(user.isEmpty()) return new ResponseTuple<>(ServiceResponse.UNSUCCESSFUL,null,null);
            redisService.saveToCache(user.get().getId(),user);
            return new ResponseTuple<>(ServiceResponse.SUCCESSFUL,user.get(),null);
        }

        return new ResponseTuple<>(ServiceResponse.SUCCESSFUL,(Roommate) optionalRoommate.get(),null);
    }

    public ServiceResponse incrementViewership(String userId, String viewedUserId) {
        Roommate roommate;
        Roommate viewedRoommate;

        Optional<Object> roommateOptional = redisService.retrieveFromCache(Roommate.class.getName()+userId);
        Optional<Object> viewedRoommateOptional = redisService.retrieveFromCache(Roommate.class.getName()+viewedUserId);

        if(roommateOptional.isEmpty()){
            Optional<Roommate> optionalRoommateMongo = roommateRepository.findById(userId);
            if(optionalRoommateMongo.isEmpty()) return ServiceResponse.FAULTY_IDENTIFIERS;
            roommate = optionalRoommateMongo.get();

        }else{
            roommate = (Roommate) roommateOptional.get();
        }

        if(viewedRoommateOptional.isEmpty()){
            Optional<Roommate> viewedRoommateMongo = roommateRepository.findById(viewedUserId);
            if(viewedRoommateMongo.isEmpty()) return  ServiceResponse.FAULTY_IDENTIFIERS;
            viewedRoommate = viewedRoommateMongo.get();
        }else{
            viewedRoommate = (Roommate) viewedRoommateOptional.get();
        }

        viewedRoommate.getViewersSet().add(roommate);
        redisService.saveToCache(viewedRoommate.getId(),viewedRoommate);
        return ServiceResponse.SUCCESSFUL;
    }

    public ResponseTuple<ServiceResponse, Optional<Roommate>, String[]> loginUser(LoginRequest request) {

        Optional<Roommate> optionalRoommate = roommateRepository.findByEmail(request.getEmail());

        if(optionalRoommate.isEmpty() || !encoder.matches(request.getPassword(), optionalRoommate.get().getPassword())){
            Tuple2<ServiceResponse, Optional<Roommate>> Tuple2;
            return new ResponseTuple<>(ServiceResponse.FAULTY_EMAIL_OR_PASSWORD,optionalRoommate,null);
        }

        redisService.saveToCache(optionalRoommate.get().getId(),optionalRoommate.get());

        return new ResponseTuple<>(ServiceResponse.SUCCESSFUL,optionalRoommate,apiKeyManagementService.generateAccessAndRefreshToken());
    }

    public ServiceResponse requestRoommate(String userId, String requestId, String message) {
        Roommate requestingRoommate;
        Roommate requestedRoommate;
        Optional<Object> requestingRoommateOptional = redisService.retrieveFromCache(userId);
        Optional<Object> requestedRoommateOptional = redisService.retrieveFromCache(requestId);

        if(requestingRoommateOptional.isEmpty()){
            Optional<Roommate> optional = roommateRepository.findById(userId);
            if(optional.isEmpty()) return ServiceResponse.FAULTY_IDENTIFIERS;
            requestingRoommate = optional.get();
        }else{
            requestingRoommate = (Roommate) requestingRoommateOptional.get();
        }

        if(requestedRoommateOptional.isEmpty()){
            Optional<Roommate> optional = roommateRepository.findById(requestId);
            if(optional.isEmpty()) return  ServiceResponse.FAULTY_IDENTIFIERS;
            requestedRoommate = optional.get();
        }else{
            requestedRoommate = (Roommate) requestedRoommateOptional.get();
        }

        if(!requestedRoommate.isAcceptingCoed()){
            Demographic demographicOfRequestingRoommate = requestingRoommate.getDemographics();
            Demographic demographicOfRequestedRoommate = requestedRoommate.getDemographics();
            if(demographicOfRequestedRoommate.getGender() != demographicOfRequestingRoommate.getGender()) return ServiceResponse.GENDER_MISMATCH;
        }

        RoommateRequest request = new RoommateRequest();
        request.setRequestingRoommate(requestingRoommate);
        request.setRequestedRoommate(requestedRoommate);
        if(message != null) request.setMessage(message.trim());
        request.setCreationTimestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));

        requestingRoommate.getRoommateRequests().add(request);
        requestedRoommate.getRoommateRequests().add(request);

        roommateRequestRepository.save(request);
        roommateRepository.saveAll(List.of(requestedRoommate,requestingRoommate));
        List<Roommate> roommates = List.of(requestedRoommate,requestingRoommate);
        Map<String,Object> roommateMap = roommates.stream().collect(Collectors.toMap(Roommate::getId, roommate -> roommate));
        redisService.saveAllToCache(roommateMap);
        return ServiceResponse.SUCCESSFUL;
    }

    public ServiceResponse removeRequest(String requestId) {
        Optional<RoommateRequest> roommateRequest = roommateRequestRepository.findById(requestId);

        if(roommateRequest.isEmpty()) return ServiceResponse.FAULTY_IDENTIFIERS;

        RoommateRequest request = roommateRequest.get();
        Roommate requestingRoommate = request.getRequestingRoommate();
        Roommate roommate = request.getRequestedRoommate();

        roommate.getRoommateRequests().remove(request);
        requestingRoommate.getRoommateRequests().remove(request);

        roommateRequest.get().setAcceptedRequest(1);
        roommateRequest.get().setRejectionTimestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        roommateRequestRepository.save(request);
        roommateRepository.saveAll(List.of(roommate,requestingRoommate));

        redisService.flushFromCache(roommate.getId());
        redisService.flushFromCache(requestingRoommate.getId());

        return ServiceResponse.SUCCESSFUL;
    }


}
