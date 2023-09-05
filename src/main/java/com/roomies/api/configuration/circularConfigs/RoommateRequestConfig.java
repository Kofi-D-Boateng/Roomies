package com.roomies.api.configuration.circularConfigs;

import com.roomies.api.model.roommate.Roommate;
import com.roomies.api.model.roommate.RoommateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class RoommateRequestConfig {
    @Autowired
    @Lazy
    private Roommate roommate1;

    @Autowired
    @Lazy
    private Roommate roommate2;

    @Bean
    public RoommateRequest roommateRequest(){
        RoommateRequest roommateRequest = new RoommateRequest();
        roommateRequest.setRequestedRoommate(roommate1);
        roommateRequest.setRequestingRoommate(roommate2);
        return roommateRequest;
    }
}
