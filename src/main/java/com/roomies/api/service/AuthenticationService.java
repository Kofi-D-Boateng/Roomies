package com.roomies.api.service;

import com.roomies.api.enums.MFARequest;
import com.roomies.api.enums.OAuth;
import com.roomies.api.enums.ServiceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class AuthenticationService {
    public static Map<String,Long> mfaCacheMap = new ConcurrentHashMap<>();
    public static String EMAIL_ROUTE_KEY = "email_multiFactor";
    public static String SMS_ROUTE_KEY = "sms_multiFactor";
    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    public boolean sendMultiFactorAuthenticationCode(String id, MFARequest request){
        String code = UUID.randomUUID().toString();
        if(mfaCacheMap.containsKey(code)) code = UUID.randomUUID().toString();
        else mfaCacheMap.putIfAbsent(code, LocalDateTime.now().plusMinutes(10L).toEpochSecond(ZoneOffset.UTC));
        if(request.equals(MFARequest.SMS)){
            log.info("Sending MFA request for roommate: {} @ {}",id,LocalDateTime.now());
            kafkaTemplate.send(SMS_ROUTE_KEY,id);
            log.info("Sent notification request @ {}",LocalDateTime.now());
            return true;
        }else if(request.equals(MFARequest.EMAIL)){
            log.info("Sending MFA request for roommate: {} @ {}",id,LocalDateTime.now());
            kafkaTemplate.send(EMAIL_ROUTE_KEY,id);
            log.info("Sent notification request @ {}",LocalDateTime.now());
            return true;
        }
        return false;
    }

    public boolean checkTimestampOfMultiFactorAuthentication(String token){
        if(!mfaCacheMap.containsKey(token)) return false;
        Long tokenTimestamp = mfaCacheMap.get(token);
        long currentTimestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        mfaCacheMap.remove(token);
        return currentTimestamp <= tokenTimestamp;
    }

    public ServiceResponse authenticateUserWithOAuth(OAuth google, String s) {
        switch (google){
            case GITHUB -> {
                return githubOAuthRegistrationFlow(s);
            }
            case GOOGLE -> {
                return googleOAuthRegistrationFlow(s);
            }
            case FACEBOOK -> {
                return facebookOAuthRegistrationFlow(s);
            }
            default -> {
                return null;
            }
        }
    }

    private ServiceResponse googleOAuthRegistrationFlow(Object obj){
        return null;
    }

    private ServiceResponse facebookOAuthRegistrationFlow(Object obj){
        return null;
    }

    private ServiceResponse githubOAuthRegistrationFlow(Object obj){
        return null;
    }


}
