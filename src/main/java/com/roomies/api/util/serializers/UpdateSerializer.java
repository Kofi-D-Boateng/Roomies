package com.roomies.api.util.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.roomies.api.enums.Update;

import java.io.IOException;

public class UpdateSerializer extends JsonSerializer<Update> {
    @Override
    public void serialize(Update update, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(update.toString());
    }
}
