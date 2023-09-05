package com.roomies.api;

import com.roomies.api.configuration.circularConfigs.RoommateConfig;
import com.roomies.api.configuration.circularConfigs.RoommateRequestConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;

import java.util.*;

@SpringBootApplication
@Import({RoommateConfig.class, RoommateRequestConfig.class})
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}
}
