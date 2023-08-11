package com.roomies.api.model.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RoommateRequestDTO {
    private String id;
    private MaskedRoommateDTO requestedRoommate;
    private MaskedRoommateDTO requestingRoommate;
    private String message;
    private Integer acceptedRequest;
    private Long creationTimestamp;

    @Override
    public String toString() {
        return "RoommateRequestDTO{" +
                "id='" + id + '\'' +
                ", requestedRoommate=" + (requestedRoommate != null ? requestedRoommate.getId():null) +
                ", requestingRoommate=" + (requestingRoommate != null ? requestingRoommate.getId():null) +
                ", message='" + message + '\'' +
                ", acceptedRequest=" + acceptedRequest +
                ", creationTimestamp=" + creationTimestamp +
                '}';
    }
}
