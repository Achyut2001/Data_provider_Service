package com.tdit.dataprovideservice.entity;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyDTO {

    private Long propertyId;

    @NotBlank(message = "Property title is required")
    private String propertyTitle;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Property type is required")
    private String propertyType;

    @NotBlank(message = "Address Line 1 is required")
    private String addressLine1;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Country is required")
    private String country;

    @Pattern(regexp = "\\d{5,10}", message = "PinCode must be 5-10 digits")
    private String pincode;

    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private Double latitude;

    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private Double longitude;

    @NotNull(message = "Host ID is required")
    private Long hostId;

    @NotBlank(message = "Host name is required")
    private String hostName;

    @NotBlank(message = "Host contact is required")
    private String hostContact;

    @Email(message = "Invalid email format")
    private String hostEmail;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base price must be greater than 0")
    private Double basePrice;

    @NotBlank(message = "Currency is required")
    private String currency;

    // JSONB → List<String>
    private List<String> amenities;

    @NotBlank(message = "Property URL is required")
    private String propertyUrl;

    @NotBlank(message = "Status is required")
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
