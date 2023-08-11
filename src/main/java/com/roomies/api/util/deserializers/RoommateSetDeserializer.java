package com.roomies.api.util.deserializers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roomies.api.model.DTO.MaskedRoommateDTO;
import com.roomies.api.model.Roommate;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class RoommateSetDeserializer extends JsonDeserializer<Set<Roommate>> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public Set<Roommate> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode rootNode = jsonParser.readValueAsTree();
        Set<Roommate> roommateSet = new HashSet<>();
        rootNode.forEach(jsonNode -> {
            try {
                MaskedRoommateDTO maskedRoommateDTO = objectMapper.treeToValue(jsonNode, MaskedRoommateDTO.class);
                Roommate roommate = objectMapper.convertValue(maskedRoommateDTO, Roommate.class);
                roommateSet.add(roommate);
            } catch (JsonProcessingException e) {
                log.trace("Could not convert maskedRoommateDTO to Roommate in deserializer");
            }
        });
        return roommateSet;
    }
}
