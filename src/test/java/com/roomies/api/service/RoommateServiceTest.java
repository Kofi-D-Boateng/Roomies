package com.roomies.api.service;

import com.roomies.api.enums.Gender;
import com.roomies.api.enums.Rating;
import com.roomies.api.enums.ServiceResponse;
import com.roomies.api.model.Demographic;
import com.roomies.api.model.LoginRequest;
import com.roomies.api.model.Roommate;
import com.roomies.api.model.RoommateRequest;
import com.roomies.api.repository.mongo.RoommateRepository;
import com.roomies.api.repository.mongo.RoommateRequestRepository;
import com.roomies.api.repository.redis.RoommateRedisRepository;
import com.roomies.api.util.custom.ResponseTuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


import static org.mockito.Mockito.when;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
class RoommateServiceTest {
     Roommate roommate;
     Roommate roommate2;
    @Mock
    RoommateRepository roommateRepository;
    @Mock
    RoommateRedisRepository roommateRedisRepository;
    @Mock
    RoommateRequestRepository roommateRequestRepository;
    @Mock
    ApiKeyManagementService apiKeyManagementService;
    @Mock
    static BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    RoommateService roommateService;

    @BeforeEach
    public void setUp(){
        roommate = new Roommate();
        roommate2 = new Roommate();
        roommate.setEmail("email@email.com");
        roommate.setPassword("password123!");
        roommate.setAcceptingCoed(false);
        roommate2.setEmail("email2@email.com");
        roommate2.setPassword("password345!");
        roommate2.setAcceptingCoed(false);

        Demographic demographic1 = new Demographic(), demographic2 = new Demographic();
        demographic1.setRoommate(roommate);
        demographic2.setRoommate(roommate2);
        demographic1.setGender(Gender.MALE);
        demographic2.setGender(Gender.MALE);
        demographic1.setId(UUID.randomUUID().toString());
        demographic2.setId(UUID.randomUUID().toString());

        roommate.setDemographics(demographic1);
        roommate2.setDemographics(demographic2);
    }

    @Test
    void loginUser() {
        LoginRequest request = new LoginRequest();
        request.setEmail("email@email.com");
        request.setPassword("password123!");
        when(roommateRepository.findRoommateBy(request.getEmail(),request.getPhoneNumber(),null)).thenReturn(Optional.of(roommate));
        when(bCryptPasswordEncoder.matches(request.getPassword(),roommate.getPassword())).thenReturn(Objects.equals(request.getPassword(), roommate.getPassword()));
        when(apiKeyManagementService.generateAccessAndRefreshToken()).thenReturn(new String[]{UUID.randomUUID().toString(),UUID.randomUUID().toString()});

        ResponseTuple<ServiceResponse,Optional<Roommate>,String[]> responseTuple = roommateService.loginUser(request);
        assertNotNull(responseTuple.getVal1());
        assertNotNull(responseTuple.getVal2());
        assertNotNull(responseTuple.getVal3());
    }

    @Test
    void getUserProfile() {
        String id = UUID.randomUUID().toString();
        roommate.setId(id);
        when(roommateRedisRepository.findById(id)).thenReturn(Optional.of(roommate));
        Roommate r = roommateService.getUserProfile(id).getVal2();
        assertNotNull(r);
    }

    @Test
    void requestRoommate() {
        String id1 = UUID.randomUUID().toString(), id2 = UUID.randomUUID().toString();
        when(roommateRedisRepository.findById(id1)).thenReturn(Optional.of(roommate));
        when(roommateRedisRepository.findById(id2)).thenReturn(Optional.of(roommate2));

        lenient().when(roommateRepository.saveAll(List.of(roommate,roommate2))).thenReturn(null);
        lenient().when(roommateRedisRepository.saveAll(List.of(roommate,roommate2))).thenReturn(null);

        ServiceResponse response = roommateService.requestRoommate(id1,id2,null);

        assertEquals(ServiceResponse.SUCCESSFUL,response);
        assertTrue(roommate.getRoommateRequests().size() >=1);
        assertTrue(roommate2.getRoommateRequests().size() >=1);
    }

    @Test
    void removeRequest() {
        String id = UUID.randomUUID().toString();
        RoommateRequest request = new RoommateRequest(id,roommate,roommate2,null
        ,2, LocalDateTime.now().minusDays(3).toEpochSecond(ZoneOffset.UTC),null,null);

        roommate.getRoommateRequests().add(request);
        roommate2.getRoommateRequests().add(request);

        assertEquals(1,roommate.getRoommateRequests().size());
        assertEquals(1,roommate2.getRoommateRequests().size());

        when(roommateRequestRepository.findById(id)).thenReturn(Optional.of(request));
        when(roommateRequestRepository.save(request)).thenReturn(request);
        lenient().when(roommateRepository.saveAll(List.of(roommate,roommate2))).thenReturn(List.of(roommate,roommate2));

        ServiceResponse response = roommateService.removeRequest(id);

        assertEquals(0,roommate.getRoommateRequests().size());
        assertEquals(0,roommate2.getRoommateRequests().size());
        assertEquals(ServiceResponse.SUCCESSFUL,response);
        assertEquals(1,request.getAcceptedRequest());
    }

    @Test
    void acceptRequest() {
        String id = UUID.randomUUID().toString();
        RoommateRequest request = new RoommateRequest(id,roommate,roommate2,null
                ,2, LocalDateTime.now().minusDays(3).toEpochSecond(ZoneOffset.UTC),null,null);

        roommate.getRoommateRequests().add(request);
        roommate2.getRoommateRequests().add(request);

        assertEquals(1,roommate.getRoommateRequests().size());
        assertEquals(1,roommate2.getRoommateRequests().size());

        when(roommateRequestRepository.findById(id)).thenReturn(Optional.of(request));
        when(roommateRequestRepository.save(request)).thenReturn(request);
        lenient().when(roommateRepository.saveAll(List.of(roommate,roommate2))).thenReturn(List.of(roommate,roommate2));

        ServiceResponse response = roommateService.acceptRequest(id);

        assertEquals(1,roommate.getRoommateRequests().size());
        assertEquals(1,roommate2.getRoommateRequests().size());
        assertEquals(ServiceResponse.SUCCESSFUL,response);
        assertEquals(0,request.getAcceptedRequest());
    }

    @Test
    void incrementViewership() {
        String id1 = UUID.randomUUID().toString(), id2 = UUID.randomUUID().toString();
        when(roommateRedisRepository.findById(id1)).thenReturn(Optional.of(roommate));
        when(roommateRedisRepository.findById(id2)).thenReturn(Optional.of(roommate2));

        ServiceResponse response = roommateService.incrementViewership(id1,id2);
        assertEquals(ServiceResponse.SUCCESSFUL, response);
        assertEquals(0,roommate.getViewersSet().size());
        assertEquals(1,roommate2.getViewersSet().size());
        assertTrue(roommate2.getViewersSet().contains(roommate));
    }

    @Test
    void adjustRating() {
        String id1 = UUID.randomUUID().toString(), id2 = UUID.randomUUID().toString();
        Rating rating = Rating.UP;
        Long currRating = roommate2.getTotalRating(), currPositiveRating = roommate2.getPositiveRating();
        when(roommateRedisRepository.findById(id1)).thenReturn(Optional.of(roommate));
        when(roommateRedisRepository.findById(id2)).thenReturn(Optional.of(roommate2));

        ServiceResponse response = roommateService.adjustRating(id2,id1,rating);
        assertEquals(ServiceResponse.SUCCESSFUL, response);
        assertEquals(0,roommate.getRaterSet().size());
        assertEquals(1,roommate2.getRaterSet().size());
        assertTrue(roommate2.getRaterSet().contains(roommate));
        assertEquals(currRating + 1, roommate2.getTotalRating());
        assertEquals(currPositiveRating+1, roommate2.getPositiveRating());

    }

    @Test
    void blockRoommate() {
        String id1 = UUID.randomUUID().toString(), id2 = UUID.randomUUID().toString();
        when(roommateRedisRepository.findById(id1)).thenReturn(Optional.of(roommate));
        when(roommateRedisRepository.findById(id2)).thenReturn(Optional.of(roommate2));

        ServiceResponse response = roommateService.blockRoommate(id1,id2,null);
        assertEquals(ServiceResponse.SUCCESSFUL, response);
        assertEquals(1,roommate.getBlockedRoommates().size());
        assertEquals(0,roommate2.getBlockedRoommates().size());
        assertTrue(roommate.getBlockedRoommates().containsKey(roommate2));
    }
}