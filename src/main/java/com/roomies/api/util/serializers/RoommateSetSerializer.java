package com.roomies.api.util.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.roomies.api.model.DTO.MaskedRoommateDTO;
import com.roomies.api.model.Roommate;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public class RoommateSetSerializer extends JsonSerializer<Set<Roommate>> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void serialize(Set<Roommate> roommates, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        Set<MaskedRoommateDTO> convertedSet = roommates.stream().map(roommate -> objectMapper.convertValue(roommate, MaskedRoommateDTO.class)).collect(Collectors.toSet());
        jsonGenerator.writeObject(convertedSet);
    }
}
