package com.roomies.api.enums;

public enum RateLimitStatus {
    EXCEEDED_THRESHOLD("Exceeded Threshold"),
    ACCEPTABLE("Acceptable"),
    BLOCKED("Blocked"),
    NOT_FOUND("Not Found");

    private final String value;

    RateLimitStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
