package com.tdit.dataprovideservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelRowData {

    private String propertyId;

    @NotBlank(message = "Property title is mandatory")
    @Size(max = 150, message = "Property title must be max 150 characters")
    private String propertyTitle;

    @Size(max = 500, message = "Description must be max 500 characters")
    private String description;

    @NotBlank(message = "Property type is mandatory")
    @Pattern(regexp = "^(Apartment|Villa|PG|Hotel|Hostel)$",
            message = "Property type must be one of: Apartment, Villa, PG, Hotel, Hostel")
    private String propertyType;

    @NotBlank(message = "Address Line 1 is mandatory")
    @Size(max = 200, message = "Address Line 1 must be max 200 characters")
    private String addressLine1;

    @NotBlank(message = "City is mandatory")
    @Size(max = 100, message = "City must be max 100 characters")
    private String city;

    @NotBlank(message = "State is mandatory")
    @Size(max = 100, message = "State must be max 100 characters")
    private String state;

    @NotBlank(message = "Country is mandatory")
    @Size(max = 100, message = "Country must be max 100 characters")
    private String country;

    @NotBlank(message = "Pincode is mandatory")
    @Pattern(regexp = "\\d{5,10}", message = "Pincode must be 5-10 digits")
    private String pincode;

    @Pattern(regexp = "^-?\\d+(\\.\\d+)?$", message = "Latitude must be a valid number")
    private String latitude;

    @Pattern(regexp = "^-?\\d+(\\.\\d+)?$", message = "Longitude must be a valid number")
    private String longitude;

    @NotBlank(message = "Host ID is mandatory")
    @Pattern(regexp = "\\d+", message = "Host ID must be a valid number")
    private String hostId;

    @NotBlank(message = "Host name is mandatory")
    @Size(max = 100, message = "Host name must be max 100 characters")
    private String hostName;

    @NotBlank(message = "Host contact is mandatory")
    @Pattern(regexp = "\\d{10,15}", message = "Host contact must be 10-15 digits")
    private String hostContact;

    @NotBlank(message = "Host email is mandatory")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Host email must be max 100 characters")
    private String hostEmail;

    @NotBlank(message = "Base price is mandatory")
    @Pattern(regexp = "^\\d+(\\.\\d+)?$", message = "Base price must be a valid number")
    private String basePrice;

    @NotBlank(message = "Currency is mandatory")
    @Pattern(regexp = "^(INR|USD|EUR|GBP|CAD|AUD|JPY|CHF|CNY|SGD|NZD|SEK|NOK|DKK|PLN|CZK|HUF|RON|BGN)$",
            message = "Currency must be a valid ISO currency code")
    private String currency;

    @Size(max = 1000, message = "Amenities must be max 1000 characters")
    private String amenities; // <-- new single column in Excel (comma-separated or JSON)

    @Size(max = 500, message = "Property URL must be max 500 characters")
    @Pattern(regexp = "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})([/\\w .-]*)*/?$",
            message = "Property URL format may be invalid")
    private String propertyUrl;

    @NotBlank(message = "Status is mandatory")
    @Pattern(regexp = "^(ACTIVE|INACTIVE|PENDING)$",
            message = "Status must be one of: ACTIVE, INACTIVE, PENDING")
    private String status;

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}",
            message = "Created date must be in format: yyyy-MM-dd HH:mm:ss")
    private String createdAt;

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}",
            message = "Updated date must be in format: yyyy-MM-dd HH:mm:ss")
    private String updatedAt;

    //  convert string to list
    public List<String> getAmenitiesList() {
        if (amenities == null || amenities.trim().isEmpty()) {
            return new java.util.ArrayList<>();
        }
        // Support JSON-like ["WiFi","AC"] or comma-separated "WiFi,AC"
        String clean = amenities.trim();
        if (clean.startsWith("[") && clean.endsWith("]")) {
            clean = clean.substring(1, clean.length() - 1).replace("\"", "");
        }
        return Arrays.stream(clean.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
