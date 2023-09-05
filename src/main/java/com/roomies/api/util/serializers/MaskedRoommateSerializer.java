package com.roomies.api.util.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.roomies.api.model.DTO.MaskedRoommateDTO;
import com.roomies.api.model.roommate.Roommate;

import java.io.IOException;

public class MaskedRoommateSerializer extends JsonSerializer<Roommate> {
    private static final ObjectMapper objM = new ObjectMapper();
    @Override
    public void serialize(Roommate roommate, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeObject(objM.convertValue(roommate, MaskedRoommateDTO.class));
    }
}
