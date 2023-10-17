package com.roomies.api.service;

import com.google.gson.Gson;
import com.roomies.api.enums.ServiceResponse;
import com.roomies.api.model.AccountValidation;
import com.roomies.api.model.request.RegistrationRequest;
import com.roomies.api.model.roommate.Demographic;
import com.roomies.api.model.roommate.Location;
import com.roomies.api.model.roommate.Preference;
import com.roomies.api.model.roommate.Roommate;
import com.roomies.api.repository.mongo.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class RegistrationService {

    private static final String EMAIL_VERIFICATION_TOPIC = "email-verification";
    private static final Long VALIDATION_PERIOD = 10L;

    @Autowired
    RoommateRepository roommateRepository;
    @Autowired
    DemographicRepository demographicRepository;
    @Autowired
    LocationRepository locationRepository;
    @Autowired
    PreferenceRepository preferenceRepository;
    @Autowired
    AccountValidationRepository accountValidationRepository;
    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;
    @Autowired
    BCryptPasswordEncoder hashEncoder;

    public static class Message {
        private final String token;
        private final Long validationPeriod;

        public Message(String token,Long validation_period){
            this.token = token;
            this.validationPeriod = validation_period;
        }
    }

    public ServiceResponse registerNewUser(RegistrationRequest request) {
        String uniqueIdHash = hashEncoder.encode(request.getGovernmentIdentification());
        Optional<Roommate> optionalRoommate = roommateRepository.findByEmail(request.getEmail());

        if(optionalRoommate.isPresent()){
            return ServiceResponse.FAULTY_IDENTIFIERS;
        }

        Roommate roommate = new Roommate();
        roommate.setEmail(request.getEmail());
        roommate.setPassword(hashEncoder.encode(request.getPassword()));
        roommate.setSocialSecurityHash(uniqueIdHash);
        roommate.setPhoneNumber(request.getPhoneNumber());
        roommate.setDateOfBirth(request.getDateOfBirth());
        roommate.setStudent(request.isAStudent());
        roommate.setAuthorized(false);
        roommate.setMfaActive(false);


        generateVerificationTokenAndSendEmail(roommate);
        return ServiceResponse.SUCCESSFUL;
    }

    private void generateVerificationTokenAndSendEmail(Roommate roommate) {
        String token = UUID.randomUUID().toString();
        AccountValidation validation = new AccountValidation();
        Gson gson = new Gson();

        validation.setRoommate(roommate);
        validation.setVerificationToken(token);
        validation.setValidFor(LocalDateTime.now().plusMinutes(VALIDATION_PERIOD).toEpochSecond(ZoneOffset.UTC));

        demographicRepository.save(roommate.getDemographics());
        locationRepository.save(roommate.getLocation());

        roommateRepository.save(roommate);
        accountValidationRepository.save(validation);

        Demographic demographic = roommate.getDemographics();
        Location location = roommate.getLocation();

        demographic.setRoommate(roommate);
        location.setRoommate(roommate);
        demographicRepository.save(roommate.getDemographics());
        locationRepository.save(roommate.getLocation());

        log.info("Saved New User {} and generated token {} to database... Sending token to be emailed for verification",roommate.getEmail(),token);
        kafkaTemplate.send(EMAIL_VERIFICATION_TOPIC,gson.toJson(new Message(token,VALIDATION_PERIOD)));
        log.info("Successfully sent token to {} channel to for registration",EMAIL_VERIFICATION_TOPIC);

    }

    public ServiceResponse verification(String verificationToken) {
        Optional<AccountValidation> accountValidation = accountValidationRepository.findVerificationToken(verificationToken);
        if(accountValidation.isEmpty()){
            log.warn("Token: {} was not present in the database",verificationToken);
            return ServiceResponse.FAULTY_TOKEN;
        }
        AccountValidation validation = accountValidation.get();
        Roommate roommate = validation.getRoommate();
        Long currentTimeStamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        if(currentTimeStamp >= validation.getValidFor()){
            return ServiceResponse.UNSUCCESSFUL;
        }
        validation.setVerifiedAt(currentTimeStamp);
        roommate.setAuthorized(true);
        accountValidationRepository.save(validation);
        roommateRepository.save(roommate);
        log.info("Successfully authenticated user and token. Saving to database....");
        return ServiceResponse.SUCCESSFUL;
    }
}
