package com.roomies.api.configuration.serialization;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.roomies.api.model.Roommate;
import com.roomies.api.util.deserializers.RoommateMapKeyDeserializer;
import com.roomies.api.util.serializers.RoommateMapSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class JacksonConfiguration {

    @Bean
    public ObjectMapper objectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);

        SimpleModule module = new SimpleModule();
        module.addSerializer(Roommate.class, new RoommateMapSerializer());
        module.addKeyDeserializer(Roommate.class, new RoommateMapKeyDeserializer());
//        module.addSerializer(Set.class, new RoommateSetSerializer());
//        module.addDeserializer(Set.class, new RoommateSetDeserializer());
        objectMapper.registerModule(module);
        return objectMapper;
    }
}
