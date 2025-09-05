package com.tdit.dataprovideservice.entity;

import java.util.Arrays;

public enum Property_Type {
    Apartment,
    Villa,
    PG,
    Hotel,
    Hostel;

    public static Property_Type fromString(String value) {
        if (value == null) {
            return null;
        }
        // Trim + remove multiple/hidden spaces
        String cleanValue = value.trim().replaceAll("\\s+", "");
        return Arrays.stream(Property_Type.values())
                .filter(e -> e.name().equalsIgnoreCase(cleanValue))
                .findFirst()
                .orElse(null);
    }
}
