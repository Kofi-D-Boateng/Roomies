package com.roomies.api.model.DTO;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoommateRequestDTO {
    private String id;
    private MaskedRoommateDTO requestedRoommate;
    private MaskedRoommateDTO requestingRoommate;
    private String message;
    private Integer acceptedRequest;
    private Integer acceptedTimestamp;
    private Long creationTimestamp;
    private Long rejectionTimestamp;

    @Override
    public String toString() {
        return "RoommateRequestDTO{" +
                "id='" + id + '\'' +
                ", requestedRoommate=" + (requestedRoommate != null ? requestedRoommate.getId():null) +
                ", requestingRoommate=" + (requestingRoommate != null ? requestingRoommate.getId():null) +
                ", message='" + message + '\'' +
                ", acceptedRequest=" + acceptedRequest +
                ", acceptedTimestamp=" + acceptedTimestamp +
                ", creationTimestamp=" + creationTimestamp +
                ", rejectionTimestamp=" + rejectionTimestamp +
                '}';
    }
}
