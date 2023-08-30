package com.roomies.api.util.deserializers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.roomies.api.enums.Update;

import java.io.IOException;

public class UpdateDeserializer extends JsonDeserializer<Update> {
    @Override
    public Update deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        return Update.valueOf(jsonParser.getText());
    }
}
