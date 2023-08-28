package com.roomies.api.enums;

public enum Race {
    AFRICAN_OR_BLACK("African or Black"),
    EUROPEAN_OR_WHITE("European or White"),
    ASIAN_OR_PACIFIC_ISLANDER("Asian or Pacific Islander"),
    MIDDLE_EASTERN("Middle Eastern"),
    LATIN("Latin"),
    MIX("Mixed");

    private final String value;

    Race(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
