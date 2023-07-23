package com.roomies.api.model.DTO;

import com.roomies.api.enums.Grade;
import com.roomies.api.model.Demographic;
import com.roomies.api.model.Location;
import com.roomies.api.model.Preference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoommateDTO {

    private String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private Long dateOfBirth;
    private String profilePictureHash;
    private String biography;
    private boolean isStudent;
    private String universityName;
    private String major;
    private Grade schoolGrade;
    private Location location;
    private Demographic demographics;
    private Preference preference;

}
