package com.roomies.api.enums;

public enum Unit {
    METRIC("Metric"),
    IMPERIAL("Imperial");

    private final String value;
    Unit(String val){value = val;}

    public String getValue(){return value;}
}
