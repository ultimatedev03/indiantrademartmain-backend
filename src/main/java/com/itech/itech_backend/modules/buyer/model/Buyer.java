package com.itech.itech_backend.modules.buyer.model;

import com.itech.itech_backend.modules.company.model.Company;
import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.enums.VerificationStatus;
import com.itech.itech_backend.enums.KycStatus;
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
@Table(name = "buyers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Buyer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Link to unified User model (migration support)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    // Link to Company (One buyer can represent one company or individual)
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
    
    // Buyer Account Information
    @Column(nullable = false, length = 100)
    private String buyerName;
    
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    
    @Column(unique = true, length = 20)
    private String phone;
    
    @Column(nullable = false)
    private String password; // This will be encrypted
    
    // Buyer Type and Status
    @Enumerated(EnumType.STRING)
    @Column(name = "buyer_type")
    private BuyerType buyerType = BuyerType.INDIVIDUAL;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "buyer_status")
    private BuyerStatus buyerStatus = BuyerStatus.ACTIVE;
    
    @Column(name = "is_verified")
    private Boolean isVerified = false;
    
    @Column(name = "is_premium")
    private Boolean isPremium = false;
    
    // Personal/Company Information
    @Column(name = "first_name", length = 50)
    private String firstName;
    
    @Column(name = "last_name", length = 50)
    private String lastName;
    
    @Column(name = "display_name", length = 150)
    private String displayName;
    
    @Column(name = "job_title", length = 100)
    private String jobTitle;
    
    @Column(name = "department", length = 100)
    private String department;
    
    @Column(columnDefinition = "TEXT")
    private String bio;
    
    @Column(name = "profile_image_url")
    private String profileImageUrl;
    
    // Contact Information
    @Column(name = "secondary_email", length = 100)
    private String secondaryEmail;
    
    @Column(name = "secondary_phone", length = 20)
    private String secondaryPhone;
    
    @Column(name = "linkedin_url")
    private String linkedinUrl;
    
    @Column(name = "website_url")
    private String websiteUrl;
    
    // Address Information
    @Column(name = "billing_address_line1")
    private String billingAddressLine1;
    
    @Column(name = "billing_address_line2")
    private String billingAddressLine2;
    
    @Column(name = "billing_city", length = 50)
    private String billingCity;
    
    @Column(name = "billing_state", length = 50)
    private String billingState;
    
    @Column(name = "billing_postal_code", length = 10)
    private String billingPostalCode;
    
    @Column(name = "billing_country", length = 50)
    private String billingCountry = "India";
    
    @Column(name = "shipping_address_line1")
    private String shippingAddressLine1;
    
    @Column(name = "shipping_address_line2")
    private String shippingAddressLine2;
    
    @Column(name = "shipping_city", length = 50)
    private String shippingCity;
    
    @Column(name = "shipping_state", length = 50)
    private String shippingState;
    
    @Column(name = "shipping_postal_code", length = 10)
    private String shippingPostalCode;
    
    @Column(name = "shipping_country", length = 50)
    private String shippingCountry = "India";
    
    @Column(name = "same_as_billing")
    private Boolean sameAsBilling = true;
    
    // Business Information (for B2B buyers)
    @Enumerated(EnumType.STRING)
    @Column(name = "business_type")
    private BusinessType businessType;
    
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "buyer_industries", joinColumns = @JoinColumn(name = "buyer_id"))
    @Column(name = "industry")
    private List<Industry> industries;
    
    @Column(name = "company_size")
    @Enumerated(EnumType.STRING)
    private CompanySize companySize;
    
    @Column(name = "annual_budget", precision = 15, scale = 2)
    private BigDecimal annualBudget;
    
    @Column(name = "purchasing_authority")
    @Enumerated(EnumType.STRING)
    private PurchasingAuthority purchasingAuthority;
    
    // Purchasing Preferences
    @ElementCollection
    @CollectionTable(name = "buyer_preferred_categories", joinColumns = @JoinColumn(name = "buyer_id"))
    @Column(name = "category")
    private List<String> preferredCategories;
    
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "buyer_payment_methods", joinColumns = @JoinColumn(name = "buyer_id"))
    @Column(name = "payment_method")
    private List<PaymentMethod> preferredPaymentMethods;
    
    @Column(name = "credit_limit", precision = 15, scale = 2)
    private BigDecimal creditLimit;
    
    @Column(name = "payment_terms_preference")
    private Integer paymentTermsPreference; // Days
    
    // Purchase History and Analytics
    @Column(name = "total_orders", columnDefinition = "BIGINT DEFAULT 0")
    private Long totalOrders = 0L;
    
    @Column(name = "completed_orders", columnDefinition = "BIGINT DEFAULT 0")
    private Long completedOrders = 0L;
    
    @Column(name = "cancelled_orders", columnDefinition = "BIGINT DEFAULT 0")
    private Long cancelledOrders = 0L;
    
    @Column(name = "total_spent", precision = 15, scale = 2)
    private BigDecimal totalSpent = BigDecimal.ZERO;
    
    @Column(name = "average_order_value", precision = 15, scale = 2)
    private BigDecimal averageOrderValue = BigDecimal.ZERO;
    
    @Column(name = "last_order_date")
    private LocalDateTime lastOrderDate;
    
    @Column(name = "favorite_vendors", columnDefinition = "TEXT")
    private String favoriteVendors; // JSON array of vendor IDs
    
    // Engagement and Activity
    @Column(name = "profile_views", columnDefinition = "BIGINT DEFAULT 0")
    private Long profileViews = 0L;
    
    @Column(name = "product_views", columnDefinition = "BIGINT DEFAULT 0")
    private Long productViews = 0L;
    
    @Column(name = "inquiries_sent", columnDefinition = "BIGINT DEFAULT 0")
    private Long inquiriesSent = 0L;
    
    @Column(name = "quotes_requested", columnDefinition = "BIGINT DEFAULT 0")
    private Long quotesRequested = 0L;
    
    @Column(name = "reviews_written", columnDefinition = "INT DEFAULT 0")
    private Integer reviewsWritten = 0;
    
    @Column(name = "wishlist_items", columnDefinition = "INT DEFAULT 0")
    private Integer wishlistItems = 0;
    
    // Communication Preferences
    @Column(name = "email_notifications")
    private Boolean emailNotifications = true;
    
    @Column(name = "sms_notifications")
    private Boolean smsNotifications = false;
    
    @Column(name = "marketing_emails")
    private Boolean marketingEmails = true;
    
    @Column(name = "price_alerts")
    private Boolean priceAlerts = true;
    
    @Column(name = "new_product_alerts")
    private Boolean newProductAlerts = false;
    
    @Column(name = "order_updates")
    private Boolean orderUpdates = true;
    
    // Verification and KYC
    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status")
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_status")
    private KycStatus kycStatus = KycStatus.NOT_SUBMITTED;
    
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
    
    // Documents and Verification
    @ElementCollection
    @CollectionTable(name = "buyer_documents", joinColumns = @JoinColumn(name = "buyer_id"))
    @Column(name = "document_url")
    private List<String> documentUrls;
    
    // Subscription and Premium Features
    @Column(name = "subscription_type")
    @Enumerated(EnumType.STRING)
    private SubscriptionType subscriptionType = SubscriptionType.FREE;
    
    @Column(name = "subscription_start_date")
    private LocalDateTime subscriptionStartDate;
    
    @Column(name = "subscription_end_date")
    private LocalDateTime subscriptionEndDate;
    
    // Activity Tracking
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    @Column(name = "last_activity")
    private LocalDateTime lastActivity;
    
    @Column(name = "login_count", columnDefinition = "BIGINT DEFAULT 0")
    private Long loginCount = 0L;
    
    @Column(name = "session_duration_minutes", columnDefinition = "BIGINT DEFAULT 0")
    private Long sessionDurationMinutes = 0L;
    
    // Privacy and Security
    @Column(name = "profile_visibility")
    @Enumerated(EnumType.STRING)
    private ProfileVisibility profileVisibility = ProfileVisibility.PUBLIC;
    
    @Column(name = "two_factor_enabled")
    private Boolean twoFactorEnabled = false;
    
    @Column(name = "email_verified")
    private Boolean emailVerified = false;
    
    @Column(name = "phone_verified")
    private Boolean phoneVerified = false;
    
    // Additional fields for compatibility
    @Column(name = "password_hash")
    private String passwordHash;
    
    @Column(name = "is_email_verified")
    private Boolean isEmailVerified = false;
    
    @Column(name = "is_phone_verified")
    private Boolean isPhoneVerified = false;
    
    @Column(name = "is_kyc_verified")
    private Boolean isKycVerified = false;
    
    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;
    
    @Column(name = "email_verification_token")
    private String emailVerificationToken;
    
    @Column(name = "email_verification_token_expiry")
    private LocalDateTime emailVerificationTokenExpiry;
    
    @Column(name = "email_verification_date")
    private LocalDateTime emailVerificationDate;
    
    @Column(name = "phone_verification_otp")
    private String phoneVerificationOtp;
    
    @Column(name = "phone_verification_otp_expiry")
    private LocalDateTime phoneVerificationOtpExpiry;
    
    @Column(name = "phone_verification_date")
    private LocalDateTime phoneVerificationDate;
    
    @Column(name = "kyc_data", columnDefinition = "TEXT")
    private String kycDataJson; // JSON representation of Map<String, Object>
    
    @Column(name = "kyc_attempts")
    private Integer kycAttempts = 0;
    
    @Column(name = "kyc_verification_date")
    private LocalDateTime kycVerificationDate;
    
    @Column(name = "suspension_end_date")
    private LocalDateTime suspensionEndDate;
    
    @Column(name = "status_reason")
    private String statusReason;
    
    @Column(name = "subscription_expiry_date")
    private LocalDateTime subscriptionExpiryDate;
    
    @Column(name = "total_order_value", precision = 15, scale = 2)
    private BigDecimal totalOrderValue = BigDecimal.ZERO;
    
    @Column(name = "company_name")
    private String companyName;
    
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
    public enum BuyerType {
        INDIVIDUAL, BUSINESS, ENTERPRISE, GOVERNMENT, NGO
    }
    
    public enum BuyerStatus {
        ACTIVE, INACTIVE, SUSPENDED, BLOCKED, PENDING_VERIFICATION, DELETED
    }
    
    public enum BusinessType {
        STARTUP, SME, LARGE_ENTERPRISE, CORPORATION, PARTNERSHIP, 
        SOLE_PROPRIETORSHIP, GOVERNMENT, NGO, EDUCATIONAL
    }
    
    public enum Industry {
        AGRICULTURE, AUTOMOTIVE, CHEMICALS, CONSTRUCTION, ELECTRONICS, ENERGY, 
        FOOD_BEVERAGE, HEALTHCARE, INFORMATION_TECHNOLOGY, MANUFACTURING, MINING, 
        PHARMACEUTICALS, TEXTILES, TRANSPORTATION, TELECOMMUNICATIONS, EDUCATION, 
        FINANCE, REAL_ESTATE, RETAIL, HOSPITALITY
    }
    
    public enum CompanySize {
        STARTUP, SMALL_1_10, MEDIUM_11_50, LARGE_51_200, ENTERPRISE_201_1000, 
        LARGE_ENTERPRISE_1000_PLUS
    }
    
    public enum PurchasingAuthority {
        INFLUENCER, DECISION_MAKER, BUDGET_HOLDER, APPROVER, END_USER
    }
    
    public enum PaymentMethod {
        CREDIT_CARD, DEBIT_CARD, NET_BANKING, UPI, WALLET, BANK_TRANSFER, 
        CHEQUE, CASH_ON_DELIVERY, CREDIT_TERMS, CORPORATE_CREDIT
    }
    
    public enum SubscriptionType {
        FREE, BASIC, PREMIUM, ENTERPRISE
    }
    
    public enum ProfileVisibility {
        PUBLIC, PRIVATE, VERIFIED_VENDORS_ONLY, PREMIUM_ONLY
    }
}

