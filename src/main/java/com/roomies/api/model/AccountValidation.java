package com.roomies.api.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "verification")
public class AccountValidation {
    @Id
    private String id;

    @DBRef
    private Roommate roommate;

    @Field("verification_token")
    private String verificationToken;

    @Field("valid_for")
    private Long validFor;

    @Field("verification_timestamp")
    private Long verifiedAt;
}
