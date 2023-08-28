package com.roomies.api.enums;

public enum MFARequest {
    SMS("SMS"),
    EMAIL("Email");

    private final String value;

    MFARequest(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
