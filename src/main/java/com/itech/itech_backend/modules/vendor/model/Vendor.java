package com.itech.itech_backend.modules.vendor.model;

import com.itech.itech_backend.enums.VendorType;
import com.itech.itech_backend.enums.VendorBusinessType;
import com.itech.itech_backend.enums.VerificationStatus;
import com.itech.itech_backend.modules.company.model.Company;
import com.itech.itech_backend.modules.core.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "vendors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vendor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Link to User (required in database)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // Link to Company (One vendor can represent one company)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
    
    // Vendor Account Information
    @Column(name = "business_name", nullable = false, length = 255)
    private String businessName;
    
    // Contact Information
    @Column(name = "email", length = 255)
    private String email;
    
    @Column(name = "phone", length = 20)
    private String phone;
    
    // Business Type and Status
    @Enumerated(EnumType.STRING)
    @Column(name = "business_type", nullable = false)
    private VendorBusinessType businessType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status")
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "is_verified")
    private Boolean isVerified = false;
    
    // Profile Information
    @Column(name = "display_name", length = 150)
    private String displayName;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "profile_image_url")
    private String profileImageUrl;
    
    @Column(name = "cover_image_url")
    private String coverImageUrl;
    
    // Contact Person Details
    @Column(name = "contact_person_name", length = 100)
    private String contactPersonName;
    
    @Column(name = "contact_person_designation", length = 100)
    private String contactPersonDesignation;
    
    @Column(name = "contact_person_phone", length = 20)
    private String contactPersonPhone;
    
    @Column(name = "contact_person_email", length = 100)
    private String contactPersonEmail;
    
    // Business Information
    @Column(name = "established_year")
    private Integer establishedYear;
    
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "vendor_categories", joinColumns = @JoinColumn(name = "vendor_id"))
    @Column(name = "category")
    private List<BusinessCategory> categories;
    
    @ElementCollection
    @CollectionTable(name = "vendor_specializations", joinColumns = @JoinColumn(name = "vendor_id"))
    @Column(name = "specialization")
    private List<String> specializations;
    
    // Performance Metrics
    @Column(name = "total_orders", columnDefinition = "BIGINT DEFAULT 0")
    private Long totalOrders = 0L;
    
    @Column(name = "completed_orders", columnDefinition = "BIGINT DEFAULT 0")
    private Long completedOrders = 0L;
    
    @Column(name = "cancelled_orders", columnDefinition = "BIGINT DEFAULT 0")
    private Long cancelledOrders = 0L;
    
    @Column(name = "total_revenue", precision = 15, scale = 2)
    private BigDecimal totalRevenue = BigDecimal.ZERO;
    
    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;
    
    @Column(name = "total_reviews", columnDefinition = "INT DEFAULT 0")
    private Integer totalReviews = 0;
    
    @Column(name = "response_time_hours")
    private Integer responseTimeHours;
    
    @Column(name = "fulfillment_time_days")
    private Integer fulfillmentTimeDays;
    
    // Financial Information
    @Column(name = "minimum_order_value", precision = 15, scale = 2)
    private BigDecimal minimumOrderValue;
    
    @Column(name = "credit_limit", precision = 15, scale = 2)
    private BigDecimal creditLimit;
    
    @Column(name = "payment_terms_days")
    private Integer paymentTermsDays;
    
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "vendor_payment_methods", joinColumns = @JoinColumn(name = "vendor_id"))
    @Column(name = "payment_method")
    private List<PaymentMethod> acceptedPaymentMethods;
    
    // Service Areas
    @ElementCollection
    @CollectionTable(name = "vendor_service_areas", joinColumns = @JoinColumn(name = "vendor_id"))
    @Column(name = "area")
    private List<String> serviceAreas; // Cities, States, or Regions
    
    @Column(name = "delivery_available")
    private Boolean deliveryAvailable = false;
    
    @Column(name = "installation_service")
    private Boolean installationService = false;
    
    @Column(name = "after_sales_support")
    private Boolean afterSalesSupport = false;
    
    // Certification and Compliance
    @ElementCollection
    @CollectionTable(name = "vendor_certifications", joinColumns = @JoinColumn(name = "vendor_id"))
    @Column(name = "certification")
    private List<String> certifications;
    
    @Column(name = "iso_certified")
    private Boolean isoCertified = false;
    
    @Column(name = "quality_assured")
    private Boolean qualityAssured = false;
    
    // KYC and Verification
    @Column(name = "kyc_submitted")
    private Boolean kycSubmitted = false;
    
    @Column(name = "kyc_approved")
    private Boolean kycApproved = false;
    
    @Column(name = "kyc_submitted_at")
    private LocalDateTime kycSubmittedAt;
    
    @Column(name = "kyc_approved_at")
    private LocalDateTime kycApprovedAt;
    
    @Column(name = "kyc_approved_by")
    private String kycApprovedBy;
    
    @Column(name = "kyc_rejection_reason")
    private String kycRejectionReason;
    
    // Documents
    @ElementCollection
    @CollectionTable(name = "vendor_documents", joinColumns = @JoinColumn(name = "vendor_id"))
    @Column(name = "document_url")
    private List<String> documentUrls;
    
    // Subscription and Premium Features
    @Column(name = "subscription_start_date")
    private LocalDateTime subscriptionStartDate;
    
    @Column(name = "subscription_end_date")
    private LocalDateTime subscriptionEndDate;
    
    @Column(name = "featured_vendor")
    private Boolean featuredVendor = false;
    
    @Column(name = "priority_listing")
    private Boolean priorityListing = false;
    
    // Marketing and Promotional
    @Column(name = "promotional_banner_url")
    private String promotionalBannerUrl;
    
    @Column(name = "promotional_video_url")
    private String promotionalVideoUrl;
    
    @Column(name = "social_media_links", columnDefinition = "TEXT")
    private String socialMediaLinks; // JSON format
    
    // Analytics and Insights
    @Column(name = "profile_views", columnDefinition = "BIGINT DEFAULT 0")
    private Long profileViews = 0L;
    
    @Column(name = "product_views", columnDefinition = "BIGINT DEFAULT 0")
    private Long productViews = 0L;
    
    @Column(name = "inquiry_count", columnDefinition = "BIGINT DEFAULT 0")
    private Long inquiryCount = 0L;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    @Column(name = "last_activity")
    private LocalDateTime lastActivity;
    
    // Settings and Preferences
    @Column(name = "email_notifications")
    private Boolean emailNotifications = true;
    
    @Column(name = "sms_notifications")
    private Boolean smsNotifications = true;
    
    @Column(name = "auto_approve_orders")
    private Boolean autoApproveOrders = false;
    
    @Column(name = "catalog_visibility")
    @Enumerated(EnumType.STRING)
    private CatalogVisibility catalogVisibility = CatalogVisibility.PUBLIC;
    
    // Audit Fields
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "updated_by")
    private String updatedBy;
    
    // Enums
    public enum VendorStatus {
        PENDING, APPROVED, REJECTED, SUSPENDED, INACTIVE
    }
    
    public enum BusinessCategory {
        MANUFACTURER, DISTRIBUTOR, WHOLESALER, RETAILER, SERVICE_PROVIDER, 
        IMPORTER, EXPORTER, TRADER, CONTRACTOR, CONSULTANT
    }
    
    public enum PaymentMethod {
        CREDIT_CARD, DEBIT_CARD, NET_BANKING, UPI, WALLET, CASH_ON_DELIVERY, 
        BANK_TRANSFER, CHEQUE, CREDIT_TERMS, ADVANCE_PAYMENT
    }
    
    public enum CatalogVisibility {
        PUBLIC, PRIVATE, PREMIUM_ONLY, VERIFIED_BUYERS_ONLY
    }
}

