package com.itech.itech_backend.modules.core.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "locations", indexes = {
    @Index(name = "idx_location_country", columnList = "country"),
    @Index(name = "idx_location_state", columnList = "state"),
    @Index(name = "idx_location_city", columnList = "city"),
    @Index(name = "idx_location_pincode", columnList = "pincode")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @Builder.Default
    private String country = "India";

    @Column(nullable = false, length = 100)
    private String state;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 10)
    private String pincode;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Helper methods
    public String getFullAddress() {
        return city + ", " + state + ", " + country + " - " + pincode;
    }

    public boolean isValidPincode() {
        return pincode != null && pincode.matches("\\d{6}");
    }
}
