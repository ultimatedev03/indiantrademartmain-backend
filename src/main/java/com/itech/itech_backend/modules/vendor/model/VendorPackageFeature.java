package com.itech.itech_backend.modules.vendor.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "vendor_package_features")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorPackageFeature {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_package_id", nullable = false)
    private VendorPackage vendorPackage;
    
    @Column(nullable = false)
    private String featureName;
    
    @Column(length = 500)
    private String description;
    
    @Enumerated(EnumType.STRING)
    private FeatureType featureType;
    
    private String value; // For features with specific values
    
    @Builder.Default
    private Boolean isIncluded = true;
    
    @Builder.Default
    private Boolean isHighlighted = false;
    
    private Integer displayOrder;
    
    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt;
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public enum FeatureType {
        CORE, PREMIUM, BUSINESS, TECHNICAL, SUPPORT, LIMIT, BENEFIT
    }
}
