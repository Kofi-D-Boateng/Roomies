package com.roomies.api.enums;

public enum Rating {
    UP("Up"),
    DOWN("Down");

    private final String value;

    Rating(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
