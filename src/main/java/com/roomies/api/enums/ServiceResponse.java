package com.roomies.api.enums;

public enum ServiceResponse {
    FAULTY_IDENTIFIERS("Faulty Identifiers"),
    FAULTY_EMAIL_OR_PASSWORD("Faulty Email or Password"),
    FAULTY_TOKEN("Faulty Token"),
    GENDER_MISMATCH("Gender Mismatch"),
    SUCCESSFUL("Successful"),
    USED_EMAIL("Used Email"),
    UNSUCCESSFUL("Unsuccessful");

    private final String value;

    ServiceResponse(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
