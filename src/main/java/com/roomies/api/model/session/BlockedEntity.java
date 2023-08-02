package com.roomies.api.model.session;


import com.roomies.api.model.Roommate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "blocked")
public class BlockedEntity {
    @Id
    private String id;
    @DBRef
    private Roommate roommate;
    private String ip;
    private List<String> userAgents = new ArrayList<>();
    private Long blockedDate;
    private String reason;
}
