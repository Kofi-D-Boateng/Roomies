package com.roomies.api.configuration.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.roomies.api.model.roommate.Roommate;
import com.roomies.api.util.serializers.RatingSerializer;
import com.roomies.api.util.serializers.RoommateMapSerializer;
import com.roomies.api.util.serializers.RoommateSetSerializer;
import org.springframework.context.annotation.Bean;

public class GsonConfiguration {


    @Bean
    public Gson gson(){
        return new GsonBuilder()
                .registerTypeAdapter(Roommate.class, new RatingSerializer())
                .registerTypeAdapter(Roommate.class, new RoommateMapSerializer())
                .registerTypeAdapter(Roommate.class, new RoommateSetSerializer())
                .create();

    }
}
