package com.itech.itech_backend.modules.vendor.model;

import com.itech.itech_backend.modules.payment.model.Subscription;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "vendor_packages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorPackage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(nullable = false)
    private String displayName;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal discountedPrice;
    
    @Column(nullable = false)
    private Integer durationDays;
    
    @Enumerated(EnumType.STRING)
    private DurationType durationType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Subscription.PlanType planType;
    
    private String badge; // MOST_POPULAR, RECOMMENDED, etc.
    
    private String color; // For UI styling - hex color code
    
    private String icon; // Icon name or URL
    
    // Core Features
    private Integer maxProducts;
    
    private Integer maxLeads;
    
    private Integer maxOrders;
    
    private Integer maxQuotations;
    
    private Integer maxProductImages;
    
    // Premium Features
    @Builder.Default
    private Boolean featuredListing = false;
    
    @Builder.Default
    private Boolean prioritySupport = false;
    
    @Builder.Default
    private Boolean analyticsAccess = false;
    
    @Builder.Default
    private Boolean chatbotPriority = false;
    
    @Builder.Default
    private Boolean customBranding = false;
    
    @Builder.Default
    private Boolean bulkImportExport = false;
    
    @Builder.Default
    private Boolean apiAccess = false;
    
    @Builder.Default
    private Boolean multiLocationSupport = false;
    
    @Builder.Default
    private Boolean inventoryManagement = false;
    
    @Builder.Default
    private Boolean customerInsights = false;
    
    @Builder.Default
    private Boolean marketplaceIntegration = false;
    
    @Builder.Default
    private Boolean socialMediaIntegration = false;
    
    // Business Features
    @Builder.Default
    private Boolean gstCompliance = false;
    
    @Builder.Default
    private Boolean invoiceGeneration = false;
    
    @Builder.Default
    private Boolean paymentGateway = false;
    
    @Builder.Default
    private Boolean shippingIntegration = false;
    
    @Builder.Default
    private Boolean returnManagement = false;
    
    @Builder.Default
    private Boolean loyaltyProgram = false;
    
    // Technical Features
    private Integer searchRanking; // 1 = highest priority
    
    private Integer storageLimit; // in GB
    
    private Integer bandwidthLimit; // in GB
    
    private Integer apiCallLimit; // per month
    
    // Pricing & Offers
    @Column(precision = 10, scale = 2)
    private BigDecimal setupFee;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal monthlyPrice;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal yearlyPrice;
    
    private Integer trialDays;
    
    private String offerText;
    
    @Builder.Default
    private Boolean isActive = true;
    
    @Builder.Default
    private Boolean isPopular = false;
    
    @Builder.Default
    private Integer sortOrder = 0;
    
    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt;
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // One-to-Many relationship with features
    @OneToMany(mappedBy = "vendorPackage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VendorPackageFeature> features;
    
    public enum DurationType {
        MONTHLY, YEARLY, LIFETIME
    }
}
