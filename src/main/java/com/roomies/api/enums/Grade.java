package com.roomies.api.enums;

public enum Grade {
    FRESHMEN("Freshmen"),
    SOPHOMORE("Sophomore"),
    JUNIOR("Junior"),
    SENIOR("Senior"),
    MASTERS("Masters"),
    DOCTORATE("Doctorate"),
    PHD("PhD");

    private final String value;

    Grade(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
