package com.roomies.api.controller;

import com.roomies.api.enums.ServiceResponse;
import com.roomies.api.enums.Update;
import com.roomies.api.model.Roommate;
import com.roomies.api.service.RoommateService;
import com.roomies.api.util.custom.ResponseTuple;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/v1/roommate")
@Slf4j
public class RoommateController {

    @Autowired
    RoommateService roommateService;

    @GetMapping("/secured/profile")
    public ResponseEntity<Roommate> getUserProfile(@RequestParam("id") String id){
        if(id == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        ResponseTuple<ServiceResponse,Roommate,Object> response = roommateService.getUserProfile(id);
        if(!response.getVal1().equals(ServiceResponse.SUCCESSFUL)) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(response.getVal2());
    }

    @PutMapping("/secured/update")
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

    @PutMapping("/secured/accept-request")
    public ResponseEntity<Object> acceptRequest(@RequestParam("requestId") String requestId){
        if(requestId == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        ServiceResponse response = roommateService.acceptRequest(requestId);
        if(!response.equals(ServiceResponse.SUCCESSFUL)) return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @DeleteMapping("/secured/remove-request")
    public ResponseEntity<Object> removeRequest(@RequestParam("requestId") String requestId){
        if(requestId == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        ServiceResponse response = roommateService.removeRequest(requestId);
        if(!response.equals(ServiceResponse.SUCCESSFUL)) return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PutMapping("/secured/viewership")
    public ResponseEntity<Object> increaseViewerShip(@RequestParam("userId") String userId, @RequestParam("viewedUserId") String viewedUserId){
        if(userId == null || viewedUserId == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        ServiceResponse response = roommateService.incrementViewership(userId,viewedUserId);
        if(!response.equals(ServiceResponse.SUCCESSFUL)) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PutMapping("/secured/block-roommate")
    public ResponseEntity<Object> blockRoommate(@RequestParam("userId") String userId, @RequestParam("blockingUserId") String blockingUserId, @RequestBody String reasonForBlocking){
        if(userId == null || blockingUserId == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        ServiceResponse response = roommateService.blockRoommate(userId,blockingUserId, reasonForBlocking);
        if(!response.equals(ServiceResponse.SUCCESSFUL)) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }


}
