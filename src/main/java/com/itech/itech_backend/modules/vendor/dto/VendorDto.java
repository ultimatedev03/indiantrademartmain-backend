package com.itech.itech_backend.modules.vendor.dto;

import com.itech.itech_backend.enums.VendorType;
import com.itech.itech_backend.modules.company.dto.CompanyDto;
import com.itech.itech_backend.modules.vendor.model.Vendor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorDto {
    
    private Long id;
    private CompanyDto company;
    
    // Vendor Account Information
    private String vendorName;
    private String email;
    private String phone;
    
    // Vendor Type and Status
    private VendorType vendorType;
    private Vendor.VendorStatus vendorStatus;
    private Boolean isActive;
    private Boolean isVerified;
    
    // Profile Information
    private String displayName;
    private String description;
    private String profileImageUrl;
    private String coverImageUrl;
    
    // Contact Person Details
    private String contactPersonName;
    private String contactPersonDesignation;
    private String contactPersonPhone;
    private String contactPersonEmail;
    
    // Business Information
    private Integer establishedYear;
    private String businessType;
    private List<Vendor.BusinessCategory> categories;
    private List<String> specializations;
    
    // Performance Metrics
    private Long totalOrders;
    private Long completedOrders;
    private Long cancelledOrders;
    private BigDecimal totalRevenue;
    private BigDecimal averageRating;
    private Integer totalReviews;
    private Integer responseTimeHours;
    private Integer fulfillmentTimeDays;
    
    // Financial Information
    private BigDecimal minimumOrderValue;
    private BigDecimal creditLimit;
    private Integer paymentTermsDays;
    private List<Vendor.PaymentMethod> acceptedPaymentMethods;
    
    // Service Areas
    private List<String> serviceAreas;
    private Boolean deliveryAvailable;
    private Boolean installationService;
    private Boolean afterSalesSupport;
    
    // Certification and Compliance
    private List<String> certifications;
    private Boolean isoCertified;
    private Boolean qualityAssured;
    
    // KYC and Verification
    private Boolean kycSubmitted;
    private Boolean kycApproved;
    private LocalDateTime kycSubmittedAt;
    private LocalDateTime kycApprovedAt;
    private String kycApprovedBy;
    private String kycRejectionReason;
    
    // Documents
    private List<String> documentUrls;
    
    // Subscription and Premium Features
    private LocalDateTime subscriptionStartDate;
    private LocalDateTime subscriptionEndDate;
    private Boolean featuredVendor;
    private Boolean priorityListing;
    
    // Marketing and Promotional
    private String promotionalBannerUrl;
    private String promotionalVideoUrl;
    private String socialMediaLinks;
    
    // Analytics and Insights
    private Long profileViews;
    private Long productViews;
    private Long inquiryCount;
    private LocalDateTime lastLogin;
    private LocalDateTime lastActivity;
    
    // Settings and Preferences
    private Boolean emailNotifications;
    private Boolean smsNotifications;
    private Boolean autoApproveOrders;
    private Vendor.CatalogVisibility catalogVisibility;
    
    // Audit Fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    
    // Additional computed fields for API responses
    private Double completionRate; // completedOrders / totalOrders * 100
    private Double cancellationRate; // cancelledOrders / totalOrders * 100
    private Boolean isSubscriptionActive;
    private Integer daysUntilSubscriptionExpiry;
    private String vendorBadge; // DIAMOND, PLATINUM, GOLD, BASIC
    private Boolean isEligibleForPremium;
}

