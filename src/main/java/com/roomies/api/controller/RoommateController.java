package com.roomies.api.controller;

import com.roomies.api.enums.ServiceResponse;
import com.roomies.api.enums.Update;
import com.roomies.api.service.RoommateService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/roommate")
@Slf4j
public class RoommateController {

    @Autowired
    RoommateService roommateService;

    @PutMapping("/secured/update/profile")
    public ResponseEntity<Object>updateProfile(@RequestParam String id, @RequestBody Map<Update,Object> updateObjectMap){
        if(id == null || updateObjectMap == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    @PostMapping("/secured/roommate-request")
    public ResponseEntity<Object> requestRoommate(@RequestParam("id") String userId, @RequestParam("requestUserId") String requestUserId,@RequestBody String message){
        if(userId == null || requestUserId == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        ServiceResponse response = roommateService.requestRoommate(userId,requestUserId,message);
        if(!response.equals(ServiceResponse.SUCCESSFUL)) return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @DeleteMapping("/secured/remove-request")
    public ResponseEntity<Object> removeRequest(@RequestParam("_id") String userId, @RequestParam("request_id") String requestId){
        if(userId == null || requestId == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        ServiceResponse response = roommateService.removeRequest(userId,requestId);
        if(!response.equals(ServiceResponse.SUCCESSFUL)) return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }



}
