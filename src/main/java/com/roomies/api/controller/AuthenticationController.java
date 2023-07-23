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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
    @Autowired
    ObjectMapper objectMapper;

    private static class ReturnData {
        RoommateDTO roommate;
        Map<String,String> tokens;

        boolean multiFactorAuth = false;

        public ReturnData(RoommateDTO roommate, String[] tokens){
            this.roommate = roommate;
            this.tokens = this.distributeTokens(tokens);
        }

        public ReturnData(RoommateDTO roommate, String[] tokens, boolean multiFactorAuth){
            this.roommate = roommate;
            this.tokens = this.distributeTokens(tokens);
            this.multiFactorAuth = multiFactorAuth;
        }

        private Map<String,String> distributeTokens(String[] tokens){
            Map<String,String> map = new HashMap<>();
            map.putIfAbsent("accessToken",tokens[0]);
            map.putIfAbsent("refreshToken",tokens[1]);
            return map;
        }
    }

    @PostMapping("/user/login")
    public ResponseEntity<Object> loginRoommate(@RequestBody LoginRequest request){
        log.info("Login Request: {}",request);
        if(request.getEmail() == null || request.getPassword() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        ResponseTuple<ServiceResponse, Optional<Roommate>,String[]> response =  roommateService.fetchUserInfo(request);

        ServiceResponse serviceResponse = response.getVal1();
        Roommate r = response.getVal2().get();

        if(serviceResponse.equals(ServiceResponse.FAULTY_PASSWORD) || serviceResponse.equals(ServiceResponse.FAULTY_EMAIL)){
            log.warn("Unauthorized attempt to log in for roommate: {}",r.getId());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }else if(response.getVal2().isEmpty()){
            log.error("Error within the server when requesting login attempt for roommate: {}",r.getId());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        if(r.isMfaActive()){
            return ResponseEntity.status(HttpStatus.OK).body(new ReturnData(null,response.getVal3()));
        }

        RoommateDTO roommateDTO = objectMapper.convertValue(r,RoommateDTO.class);

        return ResponseEntity.status(HttpStatus.OK).body(new ReturnData(roommateDTO, response.getVal3()));
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

    @PostMapping("/generate-mfa-request")
    public ResponseEntity<Object> generateMultiFactorAuthenticationRequest(@RequestBody MFARequest request,@RequestBody Roommate roommate){
        if(request == null || roommate == null){
            log.error("Missing either roommate info from body or proper request. Roommate: {}, Request: {}",roommate,request);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        boolean result = authenticationService.sendMultiFactorAuthenticationCode(roommate,request);
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
