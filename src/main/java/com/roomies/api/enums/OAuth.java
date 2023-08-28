package com.roomies.api.enums;

public enum OAuth {
    FACEBOOK("Facebook"),
    GOOGLE("Google"),
    GITHUB("GitHub");

    private final String value;

    OAuth(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
