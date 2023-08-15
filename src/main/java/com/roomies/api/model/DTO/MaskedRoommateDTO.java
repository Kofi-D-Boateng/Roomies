package com.roomies.api.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MaskedRoommateDTO {
    private String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String profilePictureHash;
}
