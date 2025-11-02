package com.itech.itech_backend.modules.vendor.dto;

import com.itech.itech_backend.modules.payment.model.Subscription;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorPackagePlanDto {
    
    private Long id;
    
    private String name;
    
    private String displayName;
    
    private String description;
    
    private BigDecimal price;
    
    private BigDecimal discountedPrice;
    
    private Integer durationDays;
    
    private String durationType; // MONTHLY, YEARLY, LIFETIME
    
    private Subscription.PlanType planType;
    
    private String badge; // MOST_POPULAR, RECOMMENDED, etc.
    
    private String color; // For UI styling
    
    private String icon; // Icon name or URL
    
    // Core Features
    private Integer maxProducts;
    
    private Integer maxLeads;
    
    private Integer maxOrders;
    
    private Integer maxQuotations;
    
    private Integer maxProductImages;
    
    // Premium Features
    private Boolean featuredListing;
    
    private Boolean prioritySupport;
    
    private Boolean analyticsAccess;
    
    private Boolean chatbotPriority;
    
    private Boolean customBranding;
    
    private Boolean bulkImportExport;
    
    private Boolean apiAccess;
    
    private Boolean multiLocationSupport;
    
    private Boolean inventoryManagement;
    
    private Boolean customerInsights;
    
    private Boolean marketplaceIntegration;
    
    private Boolean socialMediaIntegration;
    
    // Business Features
    private Boolean gstCompliance;
    
    private Boolean invoiceGeneration;
    
    private Boolean paymentGateway;
    
    private Boolean shippingIntegration;
    
    private Boolean returnManagement;
    
    private Boolean loyaltyProgram;
    
    // Technical Features
    private Integer searchRanking; // 1 = highest priority
    
    private Integer storageLimit; // in GB
    
    private Integer bandwidthLimit; // in GB
    
    private Integer apiCallLimit; // per month
    
    private List<String> features; // Additional text features
    
    private List<String> benefits; // Plan benefits
    
    private List<String> limitations; // What's not included
    
    // Pricing & Offers
    private BigDecimal setupFee;
    
    private BigDecimal monthlyPrice;
    
    private BigDecimal yearlyPrice;
    
    private Integer trialDays;
    
    private String offerText;
    
    private Boolean isActive;
    
    private Boolean isPopular;
    
    private Integer sortOrder;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Subscription Status (if user has this plan)
    private Boolean isCurrentPlan;
    
    private LocalDateTime subscriptionStartDate;
    
    private LocalDateTime subscriptionEndDate;
    
    private Subscription.SubscriptionStatus subscriptionStatus;
}
