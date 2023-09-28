package com.roomies.api.model.roommate;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.roomies.api.enums.RequestStatus;
import com.roomies.api.util.serializers.MaskedRoommateSerializer;
import jakarta.annotation.PostConstruct;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "request")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoommateRequest implements Serializable {
    private static final long serializeId = -622365302L;
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
    private RequestStatus requestStatus;
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
                ", acceptedRequest=" + requestStatus +
                ", creationTimestamp=" + creationTimestamp +
                ", rejectionTimestamp=" + rejectionTimestamp +
                ", acceptedTimestamp=" + acceptedTimestamp +
                '}';
    }
}


