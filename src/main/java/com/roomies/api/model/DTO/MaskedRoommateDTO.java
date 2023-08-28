package com.roomies.api.model.DTO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.roomies.api.enums.Grade;
import com.roomies.api.model.roommate.Demographic;
import com.roomies.api.model.roommate.Location;
import com.roomies.api.model.roommate.Preference;
import com.roomies.api.util.serializers.RatingSerializer;
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
    private Long dateOfBirth;
    private String profilePictureHash;
    private String biography;
    private Long availabilityDate;
    @JsonSerialize(using = RatingSerializer.class)
    private Long rating;
    private boolean acceptingCoed;
    private boolean isStudent;
    private String universityName;
    private String major;
    private Grade schoolGrade;
    private Location location;
    private Demographic demographics;
    private Preference preference;
}
