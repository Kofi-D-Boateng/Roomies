package com.roomies.api.model.roommate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Document(collection = "preference")
public class Preference implements Serializable {
    private static final long serializableId = 77466425627L;
    @Id
    private String id;
    @DBRef
    @JsonIgnore
    private Roommate roommate;
    private Map<String, Object> preferences;

    @Override
    public String toString() {
        return "Preference{" +
                "id='" + id + '\'' +
                ", roommate=" + (roommate != null ? roommate.getId():null) +
                ", preferences=" + preferences +
                '}';
    }
}
