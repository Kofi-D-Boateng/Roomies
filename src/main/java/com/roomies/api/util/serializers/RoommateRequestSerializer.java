package com.roomies.api.util.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.roomies.api.model.DTO.RoommateRequestDTO;
import com.roomies.api.model.roommate.RoommateRequest;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public class RoommateRequestSerializer extends JsonSerializer<Set<RoommateRequest>> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void serialize(Set<RoommateRequest> roommateRequests, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        Set<RoommateRequestDTO> convertedSet = roommateRequests.stream().map(
                request -> objectMapper.convertValue(request,RoommateRequestDTO.class)
        ).collect(Collectors.toSet());
        jsonGenerator.writeObject(convertedSet);
    }
}
