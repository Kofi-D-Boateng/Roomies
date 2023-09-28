package com.roomies.api.enums;

public enum RequestStatus {
    PENDING("PENDING"),ACCEPTED("ACCEPTED"),REJECTED("REJECTED");
    private final String val;

    RequestStatus(String v){val = v;}

    public String getVal() {
        return val;
    }
}
