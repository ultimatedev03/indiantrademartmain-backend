package com.itech.itech_backend.modules.dataentry.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Import the City class
import com.itech.itech_backend.modules.city.model.City;

@Entity
@Table(name = "states",
       indexes = {
           @Index(name = "idx_state_name", columnList = "name"),
           @Index(name = "idx_state_country", columnList = "country"),
           @Index(name = "idx_state_code", columnList = "stateCode"),
           @Index(name = "idx_state_active", columnList = "isActive"),
           @Index(name = "idx_state_name_country", columnList = "name,country")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_state_name_country", columnNames = {"name", "country"}),
           @UniqueConstraint(name = "uk_state_code_country", columnNames = {"stateCode", "country"})
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class State {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "State name is required")
    @Size(min = 2, max = 100, message = "State name must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 10, message = "State code must not exceed 10 characters")
    @Column(length = 10)
    private String stateCode; // e.g., "CA", "MH", "UP"

    @NotBlank(message = "Country is required")
    @Size(min = 2, max = 100, message = "Country name must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String country;

    @Size(max = 3, message = "Country code must not exceed 3 characters")
    @Column(length = 3)
    private String countryCode; // e.g., "IN", "US", "CA"

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;

    // Population data (optional)
    private Long population;

    // Geographic coordinates (optional)
    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    // Area in square kilometers (optional)
    @Column(precision = 15, scale = 2)
    private BigDecimal areaKm2;

    // Time zone (optional)
    @Size(max = 50, message = "Time zone must not exceed 50 characters")
    @Column(length = 50)
    private String timeZone;

    // Capital city (optional)
    @Size(max = 100, message = "Capital city must not exceed 100 characters")
    @Column(length = 100)
    private String capital;

    // Website URL (optional)
    @Size(max = 255, message = "Website URL must not exceed 255 characters")
    @Column(length = 255)
    private String websiteUrl;

    // Search keywords for better searchability
    @Column(columnDefinition = "TEXT")
    private String searchKeywords;

    // URL-friendly slug
    @Size(max = 150, message = "Slug must not exceed 150 characters")
    @Column(length = 150)
    private String slug;

    // Audit fields
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Relationship with cities
    @OneToMany(mappedBy = "state", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<City> cities = new ArrayList<>();

    // Lifecycle callbacks
    @PrePersist
    @PreUpdate
    private void beforeSave() {
        generateSlug();
        generateSearchKeywords();
        normalizeData();
    }

    // Utility methods
    public void generateSlug() {
        if (this.name != null && !this.name.trim().isEmpty()) {
            this.slug = this.name.toLowerCase()
                    .replaceAll("[^a-z0-9\\s-]", "")
                    .replaceAll("\\s+", "-")
                    .replaceAll("-+", "-")
                    .replaceAll("^-|-$", "");
        }
    }

    public void generateSearchKeywords() {
        StringBuilder keywords = new StringBuilder();
        
        if (name != null) keywords.append(name.toLowerCase()).append(" ");
        if (stateCode != null) keywords.append(stateCode.toLowerCase()).append(" ");
        if (country != null) keywords.append(country.toLowerCase()).append(" ");
        if (countryCode != null) keywords.append(countryCode.toLowerCase()).append(" ");
        if (capital != null) keywords.append(capital.toLowerCase()).append(" ");
        
        this.searchKeywords = keywords.toString().trim();
    }

    private void normalizeData() {
        if (name != null) {
            name = name.trim();
        }
        if (country != null) {
            country = country.trim();
        }
        if (stateCode != null) {
            stateCode = stateCode.trim().toUpperCase();
        }
        if (countryCode != null) {
            countryCode = countryCode.trim().toUpperCase();
        }
        if (capital != null) {
            capital = capital.trim();
        }
    }

    // Helper methods
    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }

    public String getDisplayName() {
        if (stateCode != null && !stateCode.isEmpty()) {
            return name + " (" + stateCode + ")";
        }
        return name;
    }

    public String getFullName() {
        return name + ", " + country;
    }

    // Calculate distance between two states (if coordinates are available)
    public double calculateDistanceTo(State otherState) {
        if (!this.hasCoordinates() || !otherState.hasCoordinates()) {
            return -1; // Distance cannot be calculated
        }
        
        return calculateDistance(this.latitude.doubleValue(), this.longitude.doubleValue(), 
                               otherState.latitude.doubleValue(), otherState.longitude.doubleValue());
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c; // Distance in km
    }

    // Statistical methods
    public long getCityCount() {
        return cities != null ? cities.size() : 0;
    }

    public long getActiveCityCount() {
        return cities != null ? 
            cities.stream().mapToLong(city -> city.getIsActive() ? 1 : 0).sum() : 0;
    }

    @Override
    public String toString() {
        return "State{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", stateCode='" + stateCode + '\'' +
                ", country='" + country + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", isActive=" + isActive +
                ", displayOrder=" + displayOrder +
                ", population=" + population +
                ", capital='" + capital + '\'' +
                '}';
    }
}
