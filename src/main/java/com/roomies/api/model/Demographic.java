package com.roomies.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.roomies.api.enums.Gender;
import com.roomies.api.enums.Race;
import com.roomies.api.util.deserializers.AgeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "demographics")
public class Demographic {
    private Long id;
    @JsonIgnore
    @DBRef
    private Roommate roommate;
    private Race race;
    @JsonDeserialize(using = AgeDeserializer.class)
    @Transient
    private Integer age;
    private Gender gender;
}
