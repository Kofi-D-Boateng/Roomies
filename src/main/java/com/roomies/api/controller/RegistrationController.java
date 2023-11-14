package com.roomies.api.controller;


import com.roomies.api.enums.ServiceResponse;
import com.roomies.api.model.request.RegistrationRequest;
import com.roomies.api.service.RegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.roomies.api.util.Utils.*;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/v1/registration")
@Slf4j
public class RegistrationController {

    @Autowired
    RegistrationService registrationService;

    @PostMapping("/signup")
    public ResponseEntity<ServiceResponse> registration(@RequestBody RegistrationRequest request, HttpServletRequest servletRequest){
        if(!request.validateEmailAndPassword()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        log.info("New Signup request for ip: {} ",getRealIp(servletRequest));
        ServiceResponse response = registrationService.registerNewUser(request);
        if(!response.equals(ServiceResponse.SUCCESSFUL)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/verification")
    public ResponseEntity<ServiceResponse> verification(@RequestParam("token") String verificationToken, HttpServletRequest request){
        log.info("Beginning Registration process for token: {}",verificationToken);

        if(verificationToken == null){
            log.error("Token was not present in the url path.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        ServiceResponse response = registrationService.verification(verificationToken);

        if(response.equals(ServiceResponse.UNSUCCESSFUL)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
