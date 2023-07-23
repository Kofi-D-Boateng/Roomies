package com.roomies.api.model;

import com.roomies.api.enums.Grade;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "roommates")
public class Roommate {

    @Id
    private String id;
    @Field("oauth_id")
    private String oauthId;
    @Field("first_name")
    private String firstName;
    @Field("middle_name")
    private String middleName;
    @Field("last_name")
    private String lastName;
    private String email;
    private String password;
    @Field("phone_number")
    private Long phoneNumber;
    @Field("date_of_birth")
    private Long dateOfBirth;
    @Field("social_security")
    private String socialSecurityHash;
    @Field("profile_picture")
    private String profilePictureHash;
    private String biography;
    @Field("current_student")
    private boolean isStudent;
    @Field("mfa_active")
    private boolean mfaActive;
    @Field("verification_done")
    private boolean isVerified;
    @Field("decommission_timestamp")
    private Long dateForDeletion;
    @Field("university_name")
    private String universityName;
    private String major;
    @Field("school_grade")
    private Grade schoolGrade;
    @DBRef
    private Location location;
    @DBRef
    private Demographic demographics;
    @DBRef
    private Preference preference;
    @DBRef
    Set<RoommateRequest> roommateRequests = new HashSet<>();

}
