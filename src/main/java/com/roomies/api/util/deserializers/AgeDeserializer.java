package com.roomies.api.util.deserializers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;

public class AgeDeserializer extends JsonDeserializer<Integer> {
    @Override
    public Integer deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        long dateOfBirth = jsonParser.getLongValue();
        LocalDate birthday = LocalDate.ofEpochDay(dateOfBirth / 86400000); // Convert milliseconds to days
        LocalDate now = LocalDate.now();
        return Period.between(birthday, now).getYears();
    }
}
