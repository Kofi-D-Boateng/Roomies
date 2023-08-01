package com.roomies.api.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
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
    @Field("accepted_request")
    private Integer acceptedRequest;
    @Field("creation_timestamp")
    private Long creationTimestamp;
    @Field("rejection_timestamp")
    private Long rejectionTimestamp;
    @Field("accepted_timestamp")
    private Long acceptedTimestamp;

    @Override
    public String toString() {
        return "RoommateRequest{" +
                "id='" + id + '\'' +
                ", requestedRoommate=" + (requestedRoommate != null ? requestedRoommate.getId():null) +
                ", requestingRoommate=" + (requestingRoommate != null ? requestingRoommate.getId():null) +
                ", message='" + message + '\'' +
                ", acceptedRequest=" + acceptedRequest +
                ", creationTimestamp=" + creationTimestamp +
                ", rejectionTimestamp=" + rejectionTimestamp +
                ", acceptedTimestamp=" + acceptedTimestamp +
                '}';
    }
}
