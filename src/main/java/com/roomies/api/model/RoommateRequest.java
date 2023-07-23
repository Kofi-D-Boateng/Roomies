package com.roomies.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "request")
public class RoommateRequest {
    @Id
    private String id;
    @DBRef
    @Field("requested_id")
    private Roommate requestedRoommate;
    @DBRef
    @Field("requesting_id")
    private Roommate requestingRoommate;
    private String message;

}
