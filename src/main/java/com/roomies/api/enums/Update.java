package com.roomies.api.enums;

public enum Update {
    FIRST_NAME("First Name"),
    MIDDLE_NAME("Middle Name"),
    LAST_NAME("Last Name"),
    PHONE_NUMBER("Phone Number"),
    BIOGRAPHY("Biography"),
    STUDENT_STATUS("Student Status"),
    UNIVERSITY("University"),
    MAJOR("Major"),
    SCHOOL_GRADE("School Grade"),
    COUNTRY("Country"),
    AREA("Area"),
    AREA_CODE("Area Code"),
    ADDRESS("Address"),
    HOUSE_NUMBER("House Number"),
    GENDER("Gender"),
    PREFERENCE("Preference");

    private final String value;

    Update(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
