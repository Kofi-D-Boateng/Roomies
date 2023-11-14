package com.roomies.api.model.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.roomies.api.util.Utils;
import com.roomies.api.util.deserializers.DateMillisecondDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegistrationRequest {

    private String email;
    private String password;
    @JsonDeserialize(using = DateMillisecondDeserializer.class)
    private Long dateOfBirth;
    private String governmentIdentification;
    private Long phoneNumber;
    private boolean isAStudent;

    public boolean validateEmailAndPassword(){
        return Utils.emailCheck(email) && Utils.passwordChecker(password);
    }
}
