package com.roomies.api.util.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.roomies.api.model.DTO.MaskedRoommateDTO;
import com.roomies.api.model.roommate.Roommate;

import java.io.IOException;

public class RoommateMapSerializer extends JsonSerializer<Roommate> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void serialize(Roommate roommate, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        MaskedRoommateDTO maskedRoommateDTO = objectMapper.convertValue(roommate, MaskedRoommateDTO.class);
        jsonGenerator.writeObject(maskedRoommateDTO);

    }
}
