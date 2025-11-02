package com.itech.itech_backend.modules.vendor.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.company.model.Company;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "extended_vendor_profiles", indexes = {
    @Index(name = "idx_vendor_status", columnList = "vendor_status"),
    @Index(name = "idx_vendor_type", columnList = "vendor_type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", unique = true)
    private Company company;

    @Column(name = "vendor_name", nullable = false, length = 150)
    private String vendorName;

    @Column(name = "display_name", length = 200)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(name = "vendor_type")
    @Builder.Default
    private VendorType vendorType = VendorType.BASIC;

    @Enumerated(EnumType.STRING)
    @Column(name = "vendor_status")
    @Builder.Default
    private VendorStatus vendorStatus = VendorStatus.PENDING;

    // Profile Information
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

    // Contact Person Details
    @Column(name = "contact_person_name", length = 150)
    private String contactPersonName;

    @Column(name = "contact_person_designation", length = 100)
    private String contactPersonDesignation;

    @Column(name = "contact_person_phone", length = 20)
    private String contactPersonPhone;

    @Column(name = "contact_person_email", length = 150)
    private String contactPersonEmail;

    // Business Information
    @Column(name = "business_type", length = 100)
    private String businessType;

    // Performance Metrics
    @Column(name = "total_orders", columnDefinition = "BIGINT DEFAULT 0")
    @Builder.Default
    private Long totalOrders = 0L;

    @Column(name = "completed_orders", columnDefinition = "BIGINT DEFAULT 0")
    @Builder.Default
    private Long completedOrders = 0L;

    @Column(name = "cancelled_orders", columnDefinition = "BIGINT DEFAULT 0")
    @Builder.Default
    private Long cancelledOrders = 0L;

    @Column(name = "total_revenue", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    @Column(name = "average_rating", precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(name = "total_reviews", columnDefinition = "INT DEFAULT 0")
    @Builder.Default
    private Integer totalReviews = 0;

    @Column(name = "response_time_hours")
    @Builder.Default
    private Integer responseTimeHours = 24;

    @Column(name = "fulfillment_time_days")
    @Builder.Default
    private Integer fulfillmentTimeDays = 7;

    // Financial Information
    @Column(name = "minimum_order_value", precision = 15, scale = 2)
    private BigDecimal minimumOrderValue;

    @Column(name = "credit_limit", precision = 15, scale = 2)
    private BigDecimal creditLimit;

    @Column(name = "payment_terms_days")
    @Builder.Default
    private Integer paymentTermsDays = 0;

    // Services
    @Column(name = "delivery_available")
    @Builder.Default
    private Boolean deliveryAvailable = false;

    @Column(name = "installation_service")
    @Builder.Default
    private Boolean installationService = false;

    @Column(name = "after_sales_support")
    @Builder.Default
    private Boolean afterSalesSupport = false;

    // Certifications
    @Column(name = "iso_certified")
    @Builder.Default
    private Boolean isoCertified = false;

    @Column(name = "quality_assured")
    @Builder.Default
    private Boolean qualityAssured = false;

    // KYC Status
    @Column(name = "kyc_submitted")
    @Builder.Default
    private Boolean kycSubmitted = false;

    @Column(name = "kyc_approved")
    @Builder.Default
    private Boolean kycApproved = false;

    @Column(name = "kyc_submitted_at")
    private LocalDateTime kycSubmittedAt;

    @Column(name = "kyc_approved_at")
    private LocalDateTime kycApprovedAt;

    @Column(name = "kyc_approved_by", length = 100)
    private String kycApprovedBy;

    @Column(name = "kyc_rejection_reason", columnDefinition = "TEXT")
    private String kycRejectionReason;

    // Premium Features
    @Column(name = "featured_vendor")
    @Builder.Default
    private Boolean featuredVendor = false;

    @Column(name = "priority_listing")
    @Builder.Default
    private Boolean priorityListing = false;

    @Column(name = "promotional_banner_url", length = 500)
    private String promotionalBannerUrl;

    @Column(name = "promotional_video_url", length = 500)
    private String promotionalVideoUrl;

    @Column(name = "social_media_links", columnDefinition = "JSON")
    @JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    private JsonNode socialMediaLinks;

    // Analytics
    @Column(name = "profile_views", columnDefinition = "BIGINT DEFAULT 0")
    @Builder.Default
    private Long profileViews = 0L;

    @Column(name = "product_views", columnDefinition = "BIGINT DEFAULT 0")
    @Builder.Default
    private Long productViews = 0L;

    @Column(name = "inquiry_count", columnDefinition = "BIGINT DEFAULT 0")
    @Builder.Default
    private Long inquiryCount = 0L;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "last_activity")
    private LocalDateTime lastActivity;

    // Settings
    @Column(name = "email_notifications")
    @Builder.Default
    private Boolean emailNotifications = true;

    @Column(name = "sms_notifications")
    @Builder.Default
    private Boolean smsNotifications = true;

    @Column(name = "auto_approve_orders")
    @Builder.Default
    private Boolean autoApproveOrders = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "catalog_visibility")
    @Builder.Default
    private CatalogVisibility catalogVisibility = CatalogVisibility.PUBLIC;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    // Audit Fields
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // One-to-Many relationships
    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VendorCategory> categories;

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VendorSpecialization> specializations;

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VendorCertification> certifications;

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VendorServiceArea> serviceAreas;

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VendorPaymentMethod> paymentMethods;

    // Enums
    public enum VendorType {
        BASIC, PREMIUM, ENTERPRISE
    }

    public enum VendorStatus {
        PENDING, APPROVED, REJECTED, SUSPENDED, INACTIVE
    }

    public enum CatalogVisibility {
        PUBLIC, PRIVATE, PREMIUM_ONLY, VERIFIED_BUYERS_ONLY
    }

    // Helper Methods
    public boolean isApprovedVendor() {
        return vendorStatus == VendorStatus.APPROVED;
    }

    public boolean isVerifiedVendor() {
        return kycApproved != null && kycApproved;
    }

    public boolean isPremiumVendor() {
        return vendorType == VendorType.PREMIUM || vendorType == VendorType.ENTERPRISE;
    }

    public double getOrderCompletionRate() {
        if (totalOrders == null || totalOrders == 0) return 0.0;
        return (completedOrders != null ? completedOrders.doubleValue() : 0.0) / totalOrders.doubleValue() * 100.0;
    }

    public void incrementProfileViews() {
        this.profileViews = (this.profileViews != null ? this.profileViews : 0L) + 1;
    }

    public void incrementInquiryCount() {
        this.inquiryCount = (this.inquiryCount != null ? this.inquiryCount : 0L) + 1;
    }

    public void updateLastActivity() {
        this.lastActivity = LocalDateTime.now();
    }
}
