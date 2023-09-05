package com.roomies.api.configuration.serialization;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer;
import com.roomies.api.model.roommate.Roommate;
import com.roomies.api.model.roommate.RoommateRequest;
import com.roomies.api.util.converters.RequestConverter;
import com.roomies.api.util.deserializers.RoommateMapKeyDeserializer;
import com.roomies.api.util.deserializers.RoommateSetDeserializer;
import com.roomies.api.util.serializers.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;


@Configuration
public class JacksonConfiguration {

    @Bean
    public ObjectMapper objectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);

        SimpleModule module = new SimpleModule();
        module.addSerializer(Roommate.class, new RatingSerializer());
        module.addSerializer(Roommate.class, new RoommateMapSerializer());
        module.addKeyDeserializer(Roommate.class, new RoommateMapKeyDeserializer());
        module.addSerializer(Roommate.class,new MaskedRoommateSerializer());
        objectMapper.registerModule(module);
        return objectMapper;
    }
}
