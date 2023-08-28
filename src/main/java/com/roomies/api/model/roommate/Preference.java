package com.roomies.api.model.roommate;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Document(collection = "preference")
public class Preference {
    @Id
    private String id;
    @DBRef
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
