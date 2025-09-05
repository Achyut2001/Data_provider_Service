package com.tdit.dataprovideservice.entity;

import java.util.Arrays;

public enum Status {
    REJECTED,
    APPROVED;

    public static Status fromString(String value) {
        if (value == null) {
            return null;
        }
        String clean = value.trim();
        return Arrays.stream(Status.values())
                .filter(s -> s.name().equalsIgnoreCase(clean))
                .findFirst()
                .orElse(null);
    }
}
