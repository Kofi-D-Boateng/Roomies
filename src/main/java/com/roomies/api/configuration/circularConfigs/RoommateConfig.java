package com.roomies.api.configuration.circularConfigs;

import com.roomies.api.model.roommate.Roommate;
import com.roomies.api.model.roommate.RoommateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class RoommateConfig {


    @Autowired
    @Lazy
    private RoommateRequest roommateRequest;


    @Bean
    public Roommate roommate(){
        Roommate roommate = new Roommate();
        Set<RoommateRequest> set = new HashSet<>();
        set.add(roommateRequest);
        roommate.setRoommateRequests(set);
        return roommate;
    }
}
