package com.roomies.api.util.deserializers;

import com.fasterxml.jackson.databind.*;
import com.roomies.api.model.DTO.MaskedRoommateDTO;
import com.roomies.api.model.roommate.Roommate;


import java.io.IOException;

public class RoommateMapKeyDeserializer extends KeyDeserializer {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object deserializeKey(String s, DeserializationContext deserializationContext) throws IOException {
        MaskedRoommateDTO maskedKey = objectMapper.convertValue(s, MaskedRoommateDTO.class);
        return objectMapper.convertValue(maskedKey, Roommate.class);
    }
}
