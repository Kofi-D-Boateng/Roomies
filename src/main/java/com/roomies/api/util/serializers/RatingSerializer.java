package com.roomies.api.util.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.roomies.api.model.roommate.Roommate;

import java.io.IOException;

public class RatingSerializer extends JsonSerializer<Roommate> {
    @Override
    public void serialize(Roommate roommate, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if(roommate.getTotalRating() == 0 || roommate.getPositiveRating() == 0){
            jsonGenerator.writeObject(0);
        }
        jsonGenerator.writeObject(roommate.getPositiveRating()/roommate.getTotalRating());
    }
}
