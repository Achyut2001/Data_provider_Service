package com.tdit.dataprovideservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "properties")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long propertyId;

    @Column(length = 150)
    private String propertyTitle;

    @Column(length = 500)
    private String description;

    @Column(length = 50)
    private String propertyType;

    @Column(length = 200)
    private String addressLine1;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(length = 100)
    private String country;

    @Column(length = 10)
    private String pincode;

    private Double latitude;
    private Double longitude;

    @Column(name = "host_id")
    private Long hostId;

    @Column(name = "host_name", length = 100)
    private String hostName;

    @Column(name = "host_contact", length = 15)
    private String hostContact;

    @Column(name = "host_email", length = 100)
    private String hostEmail;

    @Column(name = "base_price")
    private Double basePrice;

    @Column(length = 3)
    private String currency;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> amenities;

    @Column(name = "property_url", length = 500)
    private String propertyUrl;

    @Column(length = 20)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
