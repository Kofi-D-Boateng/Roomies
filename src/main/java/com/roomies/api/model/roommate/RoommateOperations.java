package com.roomies.api.model.roommate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roomies.api.enums.Rating;
import com.roomies.api.enums.Update;

import java.util.Map;

public interface RoommateOperations {
    void adjustRating(Roommate rater, Rating rating);
    void addTag(String tag);
    void blockRoommate(Roommate blockingRoommate, String reason);
    void increaseViewership(Roommate viewer);
    void updateRoommate(Map<Update,Object> updateObjectMap, ObjectMapper objectMapper);
}
