package com.roomies.api.model.roommate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.roomies.api.enums.Grade;
import com.roomies.api.enums.Rating;
import com.roomies.api.enums.Update;
import com.roomies.api.util.deserializers.RoommateMapKeyDeserializer;
import com.roomies.api.util.deserializers.RoommateSetDeserializer;
import com.roomies.api.util.serializers.RoommateMapSerializer;
import com.roomies.api.util.serializers.RoommateRequestSerializer;
import com.roomies.api.util.serializers.RoommateSetSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "roommates")
public class Roommate implements RoommateOperations, Serializable  {
    private static final long serializableId = 45466425627L;
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
    @Field("is_enabled")
    private boolean authorized;
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
    @JsonSerialize(using = RoommateRequestSerializer.class)
    private Set<RoommateRequest> roommateRequests = new HashSet<>();
    @Field("blocked_roommates")
    @DBRef
    @JsonSerialize(keyUsing = RoommateMapSerializer.class)
    @JsonDeserialize(keyUsing= RoommateMapKeyDeserializer.class)
    private Map<Roommate,String> blockedRoommates = new HashMap<>();
    @Field("viewers")
    @DBRef
    @JsonSerialize(using = RoommateSetSerializer.class)
    private Set<Roommate> viewersSet = new HashSet<>();
    @Field("raters")
    @DBRef
    @JsonSerialize(using = RoommateSetSerializer.class)
    @JsonDeserialize(using = RoommateSetDeserializer.class)
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

    public void addTag(String tag){this.tags.add(tag);}

    public void blockRoommate(Roommate blockingRoommate, String reason){
        if(this.blockedRoommates.containsKey(blockingRoommate) || blockingRoommate == this) return;
        log.info("{} is blocking roommate with id: {}",this.id,blockingRoommate.getId());
        this.blockedRoommates.putIfAbsent(blockingRoommate,reason);
    }

    public void increaseViewership(Roommate viewer){this.viewersSet.add(viewer);}

    public void updateRoommate(Map<Update,Object> updateObjectMap, ObjectMapper objectMapper){
        Map<Update,Object> checkedMap = checkViolations(updateObjectMap);
        checkedMap.entrySet().stream().parallel().forEach(entry -> {
            Update key = entry.getKey();
            switch (key) {
                case FIRST_NAME -> setFirstName((String) entry.getValue());
                case MIDDLE_NAME -> setMiddleName((String) entry.getValue());
                case LAST_NAME -> setLastName((String) entry.getValue());
                case PHONE_NUMBER -> setPhoneNumber((Long) entry.getValue());
                case BIOGRAPHY -> setBiography((String) entry.getValue());
                case STUDENT_STATUS -> setStudent((Boolean) entry.getValue());
                case UNIVERSITY -> demographics.setUniversityName((String) entry.getValue());
                case MAJOR -> demographics.setMajor((String) entry.getValue());
                case SCHOOL_GRADE -> demographics.setSchoolGrade((Grade) entry.getValue());
                case COUNTRY -> location.setCountry((String) entry.getValue());
                case AREA -> location.setArea((String) entry.getValue());
                case AREA_CODE -> location.setAreaCode((String) entry.getValue());
                case ADDRESS -> location.setStreet((String) entry.getValue());
                case PREFERENCE -> {
                    Map<String,Object> newPreferences = objectMapper.convertValue(entry.getValue(), new TypeReference<Map<String, Object>>() {});
                    preference.getPreferences().putAll(newPreferences);
                }
                default -> log.warn("No values where found in the map provided");
            }
        });
    }

    private Map<Update,Object> checkViolations(Map<Update,Object> updateObjectMap) {
        return updateObjectMap.entrySet().stream().parallel().filter(entry -> entry.getValue() != null).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
