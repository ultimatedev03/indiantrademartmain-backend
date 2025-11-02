package com.itech.itech_backend.modules.city.model;

import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.dataentry.entity.State;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cities",
       indexes = {
           @Index(name = "idx_city_name", columnList = "name"),
           @Index(name = "idx_city_country", columnList = "country"),
           @Index(name = "idx_city_state_province", columnList = "state_province"),
           @Index(name = "idx_city_active", columnList = "is_active"),
           @Index(name = "idx_city_major", columnList = "is_major_city"),
           @Index(name = "idx_city_slug", columnList = "slug"),
           @Index(name = "idx_city_coordinates", columnList = "latitude,longitude"),
           @Index(name = "idx_city_name_country", columnList = "name,country")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_city_slug", columnNames = {"slug"}),
           @UniqueConstraint(name = "uk_city_name_country", columnNames = {"name", "country"})
       })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class City {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "City name is required")
    @Size(min = 2, max = 100, message = "City name must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String name;
    
    @Size(max = 100, message = "State/Province must not exceed 100 characters")
    @Column(name = "state_province", length = 100)
    private String stateProvince;
    
    // Relationship with State entity (optional, for normalized state data)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id")
    private State state;
    
    @NotBlank(message = "Country is required")
    @Size(min = 2, max = 100, message = "Country name must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String country;
    
    @Column(name = "postal_code")
    private String postalCode;
    
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
    
    // Geographic coordinates (optional)
    @Column(name = "latitude")
    private Double latitude;
    
    @Column(name = "longitude")
    private Double longitude;
    
    // Additional information
    @Column(name = "time_zone")
    private String timeZone;
    
    @Column(name = "area_code")
    private String areaCode;
    
    @Column(length = 1000)
    private String notes;
    
    // Display and sorting
    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;
    
    @Column(name = "is_major_city")
    @Builder.Default
    private Boolean isMajorCity = false;
    
    // SEO and search
    @Column(name = "search_keywords")
    private String searchKeywords;
    
    @Column(name = "slug", unique = true)
    private String slug;
    
    // Employee who added this city
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_employee_id")
    private User createdByEmployee;
    
    // Timestamps
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        
        if (slug == null || slug.isEmpty()) {
            slug = generateSlugFromName(name, stateProvince, country);
        }
        
        if (displayOrder == null) {
            displayOrder = 0;
        }
        
        if (searchKeywords == null || searchKeywords.isEmpty()) {
            generateSearchKeywords();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    private String generateSlugFromName(String name, String state, String country) {
        String combined = name;
        if (state != null && !state.isEmpty()) {
            combined += "-" + state;
        }
        if (country != null && !country.isEmpty()) {
            combined += "-" + country;
        }
        
        return combined.toLowerCase()
                      .replaceAll("[^a-z0-9\\s]", "")
                      .replaceAll("\\s+", "-")
                      .trim();
    }
    
    private void generateSearchKeywords() {
        StringBuilder keywords = new StringBuilder();
        
        if (name != null) keywords.append(name).append(" ");
        if (stateProvince != null) keywords.append(stateProvince).append(" ");
        if (country != null) keywords.append(country).append(" ");
        if (postalCode != null) keywords.append(postalCode).append(" ");
        
        this.searchKeywords = keywords.toString().trim();
    }
    
    // Utility methods
    public String getFullName() {
        StringBuilder fullName = new StringBuilder(name);
        
        if (stateProvince != null && !stateProvince.isEmpty()) {
            fullName.append(", ").append(stateProvince);
        }
        
        if (country != null && !country.isEmpty()) {
            fullName.append(", ").append(country);
        }
        
        return fullName.toString();
    }
    
    public String getDisplayName() {
        if (stateProvince != null && !stateProvince.isEmpty()) {
            return name + ", " + stateProvince;
        }
        return name;
    }
    
    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }
    
    public String getCoordinatesString() {
        if (hasCoordinates()) {
            return String.format("%.6f, %.6f", latitude, longitude);
        }
        return null;
    }
    
    // Distance calculation (if coordinates are available)
    public double distanceTo(City other) {
        if (!this.hasCoordinates() || !other.hasCoordinates()) {
            return -1; // Cannot calculate distance
        }
        
        return calculateDistance(this.latitude, this.longitude, other.latitude, other.longitude);
    }
    
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Haversine formula for distance calculation
        final double R = 6371; // Radius of the Earth in kilometers
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c; // Distance in kilometers
    }
}
