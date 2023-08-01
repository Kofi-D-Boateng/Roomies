package com.roomies.api.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.roomies.api.enums.Grade;
import com.roomies.api.enums.Rating;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
@Document(collection = "roommates")
@JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
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
    @Field("availability_timestamp")
    private Long availabilityDate;
    @Field("positive_rating")
    private Long positiveRating = 0L;
    @Field("total_rating")
    private Long totalRating = 0L;
    @Field("coed_accepting")
    private boolean acceptingCoed;
    @Field("current_student")
    private boolean isStudent;
    @Field("mfa_active")
    private boolean mfaActive;
    @Field("verification_done")
    private boolean isVerified;
    @Field("decommission_timestamp")
    private Long dateForDeletion;
    @DBRef
    private Location location;
    @DBRef
    private Demographic demographics;
    @DBRef
    private Preference preference;
    @Field("roommate_requests")
    @DBRef
    private Set<RoommateRequest> roommateRequests = new HashSet<>();
    @Field("blocked_roommates")
    @DBRef
    private Map<Roommate,String> blockedRoommates = new HashMap<>();
    @Field("viewers")
    @DBRef
    private Set<Roommate> viewersSet = new HashSet<>();
    @Field("raters")
    @DBRef
    private Set<Roommate> raterSet = new HashSet<>();
    private Set<String> tags = new HashSet<>();

    public void adjustRating(Roommate rater, Rating rating){
        if(this.raterSet.contains(rater)) return;
        if(rating == Rating.UP){
            log.info("Rater ID {} increased rating for {}",rater.getId(),this.id);
            this.positiveRating++;
        }
        this.totalRating++;
        this.raterSet.add(rater);
    }

    public void blockRoommate(Roommate blockingRoommate, String reason){
        if(this.blockedRoommates.containsKey(blockingRoommate) || blockingRoommate == this) return;
        log.info("{} is blocking roommate with id: {}",this.id,blockingRoommate.getId());
        this.blockedRoommates.putIfAbsent(blockingRoommate,reason);
    }

    public void addTag(String tag){
        if(this.tags.contains(tag)) return;
        this.tags.add(tag);
    }

    public void increaseViewership(Roommate viewer){
        if(this.viewersSet.contains(viewer) || viewer == this) return;
        this.viewersSet.add(viewer);
    }


}
