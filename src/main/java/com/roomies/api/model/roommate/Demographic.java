package com.roomies.api.model.roommate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.roomies.api.enums.Gender;
import com.roomies.api.enums.Grade;
import com.roomies.api.enums.Race;
import com.roomies.api.util.deserializers.AgeDeserializer;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Document(collection = "demographics")
public class Demographic implements Serializable {
    private static final long serializableId = -4466425627L;
    @Id
    @JsonIgnore
    private String id;
    @DBRef
    @JsonIgnore
    private transient Roommate roommate;
    private Race race;
    @JsonDeserialize(using = AgeDeserializer.class)
    @Transient
    private Integer age;
    private Gender gender;
    @Field("university_name")
    private String universityName;
    private String major;
    @Field("school_grade")
    private Grade schoolGrade;

    @Override
    public String toString() {
        return "Demographic{" +
                "id='" + id + '\'' +
                ", roommate=" + (roommate != null ? roommate.getId():null) +
                ", race=" + race +
                ", age=" + age +
                ", gender=" + gender +
                ", universityName='" + universityName + '\'' +
                ", major='" + major + '\'' +
                ", schoolGrade=" + schoolGrade +
                '}';
    }
}
