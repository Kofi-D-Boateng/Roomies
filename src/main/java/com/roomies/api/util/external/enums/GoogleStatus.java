package com.roomies.api.util.external.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum GoogleStatus {
    OK("OK"),
    ZERO_RESULTS("ZERO_RESULT"),
    OVER_DAILY_LIMIT("OVER_DAILY_LIMIT"),
    OVER_QUERY_LIMIT("OVER_QUERY_LIMIT"),
    REQUEST_DENIED("REQUEST_DENIED"),
    INVALID_REQUEST("INVALID_REQUEST"),
    UNKNOWN_ERROR("UNKNOWN_ERROR");

    private final String value;

    public String getValue(){return value;}
}
