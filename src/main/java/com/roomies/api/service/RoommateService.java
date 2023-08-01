package com.roomies.api.service;

import com.roomies.api.enums.Rating;
import com.roomies.api.enums.ServiceResponse;
import com.roomies.api.model.*;
import com.roomies.api.repository.mongo.RoommateRepository;
import com.roomies.api.repository.mongo.RoommateRequestRepository;
import com.roomies.api.repository.redis.*;
import com.roomies.api.util.custom.ResponseTuple;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.util.function.Tuple2;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
public class RoommateService {
    @Autowired
    RoommateRepository roommateRepository;
    @Autowired
    RoommateRequestRepository roommateRequestRepository;
    @Autowired
    RoommateRedisRepository redisRepository;
    @Autowired
    RoommateRequestRedisRepository roommateRequestRedisRepository;
    @Autowired
    DemographicRedisRepository demographicRedisRepository;
    @Autowired
    LocationRedisRepository locationRedisRepository;
    @Autowired
    PreferenceRedisRepository preferenceRedisRepository;
    @Autowired
    ApiKeyManagementService apiKeyManagementService;
    @Autowired
    BCryptPasswordEncoder encoder;
    public ResponseTuple<ServiceResponse, Optional<Roommate>, String[]> loginUser(LoginRequest request) {

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

    public ResponseTuple<ServiceResponse,Roommate,Object> getUserProfile(String id) {
        Optional<Roommate> user = redisRepository.findById(id);
        Optional<Demographic> demographicOptional = Optional.empty();
        Optional<Preference> preferenceOptional = Optional.empty();
        Optional<Location> locationOptional = Optional.empty();
        Optional<RoommateRequest> roommateRequestOptional = Optional.empty();
        if(user.isEmpty()){
            user = roommateRepository.findById(id);
            if(user.isEmpty()) return new ResponseTuple<>(ServiceResponse.UNSUCCESSFUL,null,null);
            redisRepository.save(user.get());
        }

        return new ResponseTuple<>(ServiceResponse.SUCCESSFUL,user.get(),null);
    }

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

        if(!requestedRoommate.get().isAcceptingCoed()){
            Demographic demographicOfRequestingRoommate = requestingRoommate.get().getDemographics();
            Demographic demographicOfRequestedRoommate = requestedRoommate.get().getDemographics();
            if(demographicOfRequestedRoommate.getGender() != demographicOfRequestingRoommate.getGender()) return ServiceResponse.GENDER_MISMATCH;
        }

        RoommateRequest request = new RoommateRequest();
        request.setRequestingRoommate(requestingRoommate.get());
        request.setRequestedRoommate(requestedRoommate.get());
        if(message != null) request.setMessage(message.trim());
        request.setCreationTimestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));

        requestingRoommate.get().getRoommateRequests().add(request);
        requestedRoommate.get().getRoommateRequests().add(request);

        roommateRequestRepository.save(request);
        roommateRepository.saveAll(List.of(requestedRoommate.get(),requestingRoommate.get()));
        redisRepository.saveAll(List.of(requestedRoommate.get(),requestingRoommate.get()));
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

        return ServiceResponse.SUCCESSFUL;
    }

    public ServiceResponse acceptRequest(String requestId) {
        Optional<RoommateRequest> roommateRequest = roommateRequestRepository.findById(requestId);

        if(roommateRequest.isEmpty()) return ServiceResponse.FAULTY_IDENTIFIERS;

        RoommateRequest request = roommateRequest.get();

        roommateRequest.get().setAcceptedRequest(0);
        roommateRequest.get().setAcceptedTimestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        roommateRequestRepository.save(request);

        return ServiceResponse.SUCCESSFUL;
    }

    public ServiceResponse incrementViewership(String userId, String viewedUserId) {
        Optional<Roommate> roommate = redisRepository.findById(userId);
        Optional<Roommate> viewedRoommate = redisRepository.findById(viewedUserId);

        if(roommate.isEmpty()){
            roommate = roommateRepository.findById(userId);
            if(roommate.isEmpty()) return ServiceResponse.FAULTY_IDENTIFIERS;
        }

        if(viewedRoommate.isEmpty()){
            viewedRoommate = roommateRepository.findById(viewedUserId);
            if(viewedRoommate.isEmpty()) return  ServiceResponse.FAULTY_IDENTIFIERS;
        }

        viewedRoommate.get().getViewersSet().add(roommate.get());
        redisRepository.save(viewedRoommate.get());
        return ServiceResponse.SUCCESSFUL;
    }

    public ServiceResponse adjustRating(String personToRateId, String personWhoIsRatingId, Rating rating){
        Optional<Roommate> ratingRoommateOptional = redisRepository.findById(personToRateId);
        Optional<Roommate> raterRoommateOptional = redisRepository.findById(personWhoIsRatingId);
        if(ratingRoommateOptional.isEmpty()){
            ratingRoommateOptional = roommateRepository.findById(personToRateId);
            if(ratingRoommateOptional.isEmpty()) return ServiceResponse.FAULTY_IDENTIFIERS;
        }
        if(raterRoommateOptional.isEmpty()){
            raterRoommateOptional = roommateRepository.findById(personWhoIsRatingId);
            if(raterRoommateOptional.isEmpty()) return ServiceResponse.FAULTY_IDENTIFIERS;
        }
        ratingRoommateOptional.get().adjustRating(raterRoommateOptional.get(),rating);
        roommateRepository.save(ratingRoommateOptional.get());
        return ServiceResponse.SUCCESSFUL;
    }

    public ServiceResponse blockRoommate(String userId, String blockingUserId, String reasonForBlocking) {
        Optional<Roommate> roommateOptional = redisRepository.findById(userId);
        Optional<Roommate> blockingRoommateOptional = redisRepository.findById(blockingUserId);

        if(roommateOptional.isEmpty()){
            roommateOptional = roommateRepository.findById(userId);
            if(roommateOptional.isEmpty()) return ServiceResponse.FAULTY_IDENTIFIERS;
        }

        if(blockingRoommateOptional.isEmpty()){
            blockingRoommateOptional = roommateRepository.findById(blockingUserId);
            if(blockingRoommateOptional.isEmpty()) return ServiceResponse.FAULTY_IDENTIFIERS;
        }

        roommateOptional.get().blockRoommate(blockingRoommateOptional.get(),reasonForBlocking);
        return ServiceResponse.SUCCESSFUL;
    }
}
