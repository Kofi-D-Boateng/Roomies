package com.roomies.api.util.deserializers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DateMillisecondDeserializer extends JsonDeserializer<Long> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @Override
    public Long deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        String dateTimeString = jsonParser.getText();
        LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString, FORMATTER);
        return localDateTime.toEpochSecond(ZoneOffset.UTC) * 1000;
    }
}
