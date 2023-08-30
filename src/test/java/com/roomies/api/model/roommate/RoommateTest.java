package com.roomies.api.model.roommate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roomies.api.enums.Rating;
import com.roomies.api.enums.Update;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RoommateTest {

    private static final Roommate roommate = new Roommate();
    private static final Roommate newRoommate = new Roommate();

    private static final ObjectMapper objectMapper = new ObjectMapper();


    @BeforeAll
    public static void setup(){
        roommate.setId(UUID.randomUUID().toString());
        newRoommate.setId(UUID.randomUUID().toString());
    }
    @Test
    void adjustRating() {
        assertEquals(0,roommate.getPositiveRating());
        assertEquals(0,roommate.getTotalRating());
        roommate.adjustRating(newRoommate, Rating.UP);
        assertEquals(1,roommate.getPositiveRating());
        assertEquals(1,roommate.getTotalRating());
        assertTrue(roommate.getRaterSet().contains(newRoommate));

        roommate.adjustRating(newRoommate,Rating.UP);
        assertEquals(1,roommate.getPositiveRating());
        assertEquals(1,roommate.getTotalRating());
    }

    @Test
    void blockRoommate() {
        assertEquals(0,roommate.getBlockedRoommates().size());
        roommate.blockRoommate(newRoommate,"THIS IS A TEST");
        assertEquals(1,roommate.getBlockedRoommates().size());
        assertEquals(roommate.getBlockedRoommates().get(newRoommate),"THIS IS A TEST");
    }

    @Test
    void increaseViewership() {
        assertEquals(0,roommate.getViewersSet().size());
        roommate.increaseViewership(newRoommate);
        assertEquals(1,roommate.getViewersSet().size());
        assertTrue(roommate.getViewersSet().contains(newRoommate));
    }

    @Test
    void updateRoommate() {
        Map<Update,Object> updateObjectMap = createMap();
        roommate.updateRoommate(updateObjectMap,objectMapper);
        assertEquals("Kofi", roommate.getFirstName());
        assertEquals("Boateng", roommate.getLastName());
        assertNull(roommate.getPhoneNumber());
    }

    private Map<Update,Object> createMap(){
        Map<Update,Object> updateObjectMap = new HashMap<>();
        updateObjectMap.putIfAbsent(Update.FIRST_NAME,"Kofi");
        updateObjectMap.putIfAbsent(Update.LAST_NAME,"Boateng");
        updateObjectMap.putIfAbsent(Update.PHONE_NUMBER,null);
        return  updateObjectMap;
    }
}