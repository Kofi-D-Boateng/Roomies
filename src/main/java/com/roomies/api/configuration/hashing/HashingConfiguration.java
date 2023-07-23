package com.roomies.api.configuration.hashing;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class HashingConfiguration {

    @Bean
    public BCryptPasswordEncoder hashEncoder(){
        return new BCryptPasswordEncoder(10);
    }
}
