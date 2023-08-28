package com.roomies.api.service;

import com.google.gson.Gson;
import com.roomies.api.enums.ServiceResponse;
import com.roomies.api.model.AccountValidation;
import com.roomies.api.model.request.RegistrationRequest;
import com.roomies.api.model.roommate.Roommate;
import com.roomies.api.repository.mongo.AccountValidationRepository;
import com.roomies.api.repository.mongo.RoommateRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
class RegistrationServiceTest {
    private static final String EMAIL_VERIFICATION_TOPIC = "email_verification";
    private static final Long VALIDATION_PERIOD = 10L;

    static RegistrationRequest request;
    static Roommate roommate;

    static String hash;

    @Mock
    RoommateRepository roommateRepository;

    @Mock
    AccountValidationRepository accountValidationRepository;

    @Mock
    KafkaTemplate<String,String> kafkaTemplate;

    @Mock
    static
    BCryptPasswordEncoder encoder;

    @InjectMocks
    RegistrationService registrationService;

    public static class Message {
        private final String token;
        private final Long validationPeriod;

        public Message(String token,Long validation_period){
            this.token = token;
            this.validationPeriod = validation_period;
        }
    }

    @BeforeAll
    public static void setUp(){
        request = new RegistrationRequest();
        roommate = new Roommate();
        hash = encoder.encode(request.getGovernmentIdentification());
        request.setEmail("someEmail@email.com");
        roommate.setEmail(request.getEmail());
        request.setPassword("password214!");
        roommate.setPassword(encoder.encode(request.getPassword()));
        request.setGovernmentIdentification("82310234");
        roommate.setSocialSecurityHash(hash);
        request.setPhoneNumber(8888888888L);
        roommate.setPhoneNumber(request.getPhoneNumber());
    }


    @Test
    void registerNewUser() {
        List<ServiceResponse> responses = new ArrayList<>();

        when(encoder.encode(request.getGovernmentIdentification())).thenReturn(hash);
        when(roommateRepository.findRoommateByEmailOrSocialSecurityHash(request.getEmail(),hash)).thenReturn(Optional.empty());
        lenient().when(roommateRepository.save(roommate)).thenReturn(roommate);
        lenient().when(kafkaTemplate.send(EMAIL_VERIFICATION_TOPIC,new Gson().toJson(new Message("",VALIDATION_PERIOD)))).thenReturn(null);

        responses.add(registrationService.registerNewUser(request));

        when(encoder.encode(request.getGovernmentIdentification())).thenReturn(hash);
        when(roommateRepository.findRoommateByEmailOrSocialSecurityHash(request.getEmail(),hash)).thenReturn(Optional.of(roommate));
        lenient().when(roommateRepository.save(roommate)).thenReturn(roommate);
        lenient().when(kafkaTemplate.send(EMAIL_VERIFICATION_TOPIC,new Gson().toJson(new Message("",VALIDATION_PERIOD)))).thenReturn(null);

        responses.add(registrationService.registerNewUser(request));

        for(ServiceResponse response:responses) {
            if(response.equals(ServiceResponse.SUCCESSFUL) || response.equals(ServiceResponse.FAULTY_IDENTIFIERS)) assertTrue(true);
        }


    }

    @Test
    void verification() {
        String token = UUID.randomUUID().toString();
        AccountValidation accountValidation = new AccountValidation();
        accountValidation.setVerificationToken(token);
        accountValidation.setRoommate(roommate);
        accountValidation.setValidFor(LocalDateTime.now().plusMinutes(10).toEpochSecond(ZoneOffset.UTC));
        when(accountValidationRepository.findVerificationToken(token)).thenReturn(Optional.of(accountValidation));
        lenient().when(accountValidationRepository.save(accountValidation)).thenReturn(accountValidation);
        lenient().when(roommateRepository.save(roommate)).thenReturn(roommate);

        ServiceResponse response = registrationService.verification(token);

        assertEquals(response, ServiceResponse.SUCCESSFUL);
    }
}