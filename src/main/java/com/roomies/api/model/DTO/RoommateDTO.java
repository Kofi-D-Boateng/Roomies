package com.roomies.api.model.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.roomies.api.enums.Grade;
import com.roomies.api.model.*;
import com.roomies.api.util.deserializers.RoommateMapKeyDeserializer;
import com.roomies.api.util.deserializers.RoommateSetDeserializer;
import com.roomies.api.util.serializers.RoommateMapSerializer;
import com.roomies.api.util.serializers.RoommateRequestSerializer;
import com.roomies.api.util.serializers.RoommateSetSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoommateDTO {
    private String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private Long dateOfBirth;
    private String profilePictureHash;
    private String biography;
    private Long availabilityDate;
    private Long rating;
    private boolean acceptingCoed;
    private boolean isStudent;
    private String universityName;
    private String major;
    private Grade schoolGrade;
    private Location location;
    private Demographic demographics;
    private Preference preference;
    @JsonSerialize(using = RoommateRequestSerializer.class)
    @JsonDeserialize(using = RoommateSetDeserializer.class)
    private Set<RoommateRequest> roommateRequests = new HashSet<>();
    @JsonSerialize(keyUsing = RoommateMapSerializer.class)
    @JsonDeserialize(keyUsing= RoommateMapKeyDeserializer.class)
    private Map<Roommate,String> blockedRoommates = new HashMap<>();
    @JsonSerialize(using = RoommateSetSerializer.class)
    @JsonDeserialize(using = RoommateSetDeserializer.class)
    private Set<Roommate> viewersSet = new HashSet<>();
    @JsonSerialize(using = RoommateSetSerializer.class)
    @JsonDeserialize(using = RoommateSetDeserializer.class)
    private Set<Roommate> raterSet = new HashSet<>();

}
