package com.roomies.api.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.roomies.api.enums.MFARequest;
import com.roomies.api.enums.OAuth;
import com.roomies.api.enums.ServiceResponse;
import com.roomies.api.model.LoginRequest;
import com.roomies.api.model.Roommate;
import com.roomies.api.model.DTO.RoommateDTO;
import com.roomies.api.repository.redis.RoommateRedisRepository;
import com.roomies.api.service.AuthenticationService;
import com.roomies.api.service.RoommateService;
import com.roomies.api.util.custom.ResponseTuple;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/auth")
@Slf4j
public class AuthenticationController {
    @Autowired
    RoommateService roommateService;
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    RoommateRedisRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    protected final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Getter
    @Setter
    private static class ReturnData {
        RoommateDTO roommate;
        String[][] tokens;
        boolean multiFactorAuth = false;

        public ReturnData(RoommateDTO roommate, String[] tokens){
            this.roommate = roommate;
            distributeTokens(tokens);

        }

        public ReturnData(RoommateDTO roommate, String[] tokens, boolean multiFactorAuth){
            this.roommate = roommate;
            distributeTokens(tokens);
            this.multiFactorAuth = multiFactorAuth;
        }

        private void distributeTokens(String[] tokens){
            if(tokens.length < 2){
                log.error("Tokens are not present when going to distribute.... Token: {}",Arrays.toString(tokens));
            }
            this.tokens = new String[2][2];
            this.tokens[0][0] = "accessToken";
            this.tokens[0][1] = tokens[0];
            this.tokens[1][0] = "refreshToken";
            this.tokens[1][1] = tokens[1];
        }

        @Override
        public String toString() {
            return "ReturnData{" +
                    "roommate=" + roommate +
                    ", tokens=" + Arrays.toString(tokens) +
                    ", multiFactorAuth=" + multiFactorAuth +
                    '}';
        }
    }

    @PostMapping("/user/login")
    public ResponseEntity<ReturnData> loginRoommate(@RequestBody LoginRequest request){
        log.info("Attempting login for request: {} @ {}",request,LocalDateTime.now());
        if(request.getEmail() == null || request.getPassword() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        ResponseTuple<ServiceResponse, Optional<Roommate>,String[]> response =  roommateService.loginUser(request);

        ServiceResponse serviceResponse = response.getVal1();


        if(serviceResponse.equals(ServiceResponse.FAULTY_EMAIL_OR_PASSWORD) || serviceResponse.equals(ServiceResponse.UNSUCCESSFUL)){
            String stamp = LocalDateTime.now(ZoneOffset.UTC).format(formatter);
            log.warn("Unauthorized attempt to log in using request {} @ {}",request, stamp);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }else if(response.getVal2().isEmpty()){
            String stamp = LocalDateTime.now(ZoneOffset.UTC).format(formatter);
            log.error("Error within the server when requesting login attempt for request {} @ {}",request,stamp);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        Roommate r = response.getVal2().get();

        if(r.isMfaActive()){
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ReturnData(null,response.getVal3()));
        }

        RoommateDTO roommateDTO = objectMapper.convertValue(r,RoommateDTO.class);
        System.out.println("roommateDTO = " + roommateDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ReturnData(roommateDTO, response.getVal3(),r.isAuthorized()));
    }

    @GetMapping("/google-oauth")
    public ResponseEntity<ServiceResponse> googleRegistration(){
        ServiceResponse response = authenticationService.authenticateUserWithOAuth(OAuth.GOOGLE,"");
        if(!response.equals(ServiceResponse.SUCCESSFUL)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/facebook-oauth")
    public ResponseEntity<ServiceResponse> facebookRegistration(){
        ServiceResponse response =  authenticationService.authenticateUserWithOAuth(OAuth.FACEBOOK,"");
        if(!response.equals(ServiceResponse.SUCCESSFUL)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/github-oauth")
    public ResponseEntity<ServiceResponse> githubRegistration(){
        ServiceResponse response =  authenticationService.authenticateUserWithOAuth(OAuth.GITHUB,"");
        if(!response.equals(ServiceResponse.SUCCESSFUL)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/generate-mfa-request")
    public ResponseEntity<Object> generateMultiFactorAuthenticationRequest(@RequestParam("id") String id, @RequestParam("method") MFARequest request){
        if(request == null || id == null){
            log.error("Missing either roommate info from body or proper request. Roommate: {}, Request: {}",id,request);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        boolean result = authenticationService.sendMultiFactorAuthenticationCode(id,request);
        if(!result){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/multiFactor")
    public ResponseEntity<Object> multiFactorAuthentication(@RequestParam("id") String id, @RequestParam("token") String token){
        boolean result = authenticationService.checkTimestampOfMultiFactorAuthentication(token);
        if(!result){
            log.warn("User id: {} failed to verify token: {} in time",id,token);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        Optional<Roommate> roommateOptional = repository.findById(id);
        if(roommateOptional.isEmpty()){
            log.error("Could not locate user from cache using id: {} ... Please check cache implementation or id implementation",id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        RoommateDTO roommateDTO = objectMapper.convertValue(roommateOptional.get(),RoommateDTO.class);
        return ResponseEntity.status(HttpStatus.OK).body(new ReturnData(roommateDTO,new String[]{"",""}));
    }
}
