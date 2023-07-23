package com.roomies.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "preference")
public class Preference {
    @Id
    private String id;
    @JsonIgnore
    @DBRef
    private Roommate roommate;
    private Map<String, Object> preferences;
}
