package com.itech.itech_backend.modules.buyer.model;

import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.company.model.Company;
import com.itech.itech_backend.enums.VerificationStatus;
import com.itech.itech_backend.enums.KycStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Optimized BuyerProfile model with proper normalization
 * This table stores ONLY buyer-specific data with FK reference to USER table
 */
@Entity
@Table(name = "buyer_profile", indexes = {
    @Index(name = "idx_buyer_user_id", columnList = "user_id"),
    @Index(name = "idx_buyer_status", columnList = "buyer_status"),
    @Index(name = "idx_buyer_type", columnList = "buyer_type"),
    @Index(name = "idx_buyer_company", columnList = "company_id"),
    @Index(name = "idx_buyer_verification", columnList = "verification_status"),
    @Index(name = "idx_buyer_kyc", columnList = "kyc_status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyerProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ===============================
    // CORE RELATIONSHIP TO USER
    // ===============================
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    // ===============================
    // BUYER IDENTITY & STATUS
    // ===============================
    @Column(name = "buyer_name", nullable = false, length = 100)
    private String buyerName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "buyer_type", nullable = false)
    @Builder.Default
    private BuyerType buyerType = BuyerType.INDIVIDUAL;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "buyer_status", nullable = false)
    @Builder.Default
    private BuyerStatus buyerStatus = BuyerStatus.ACTIVE;
    
    // ===============================
    // PERSONAL INFORMATION
    // ===============================
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
    
    @Column(name = "linkedin_url")
    private String linkedinUrl;
    
    @Column(name = "website_url")
    private String websiteUrl;
    
    // ===============================
    // COMPANY ASSOCIATION
    // ===============================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
    
    // ===============================
    // BUSINESS INFORMATION
    // ===============================
    @Enumerated(EnumType.STRING)
    @Column(name = "business_type")
    private BusinessType businessType;
    
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "buyer_industries", joinColumns = @JoinColumn(name = "buyer_profile_id"))
    @Column(name = "industry")
    private List<Industry> industries;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "company_size")
    private CompanySize companySize;
    
    @Column(name = "annual_budget", precision = 15, scale = 2)
    private BigDecimal annualBudget;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "purchasing_authority")
    private PurchasingAuthority purchasingAuthority;
    
    // ===============================
    // PURCHASING PREFERENCES
    // ===============================
    @ElementCollection
    @CollectionTable(name = "buyer_preferred_categories", joinColumns = @JoinColumn(name = "buyer_profile_id"))
    @Column(name = "category")
    private List<String> preferredCategories;
    
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "buyer_payment_methods", joinColumns = @JoinColumn(name = "buyer_profile_id"))
    @Column(name = "payment_method")
    private List<PaymentMethod> preferredPaymentMethods;
    
    @Column(name = "credit_limit", precision = 15, scale = 2)
    private BigDecimal creditLimit;
    
    @Column(name = "payment_terms_preference")
    private Integer paymentTermsPreference; // Days
    
    // ===============================
    // COMMUNICATION PREFERENCES
    // ===============================
    @Column(name = "email_notifications")
    @Builder.Default
    private Boolean emailNotifications = true;
    
    @Column(name = "sms_notifications")
    @Builder.Default
    private Boolean smsNotifications = false;
    
    @Column(name = "marketing_emails")
    @Builder.Default
    private Boolean marketingEmails = true;
    
    @Column(name = "price_alerts")
    @Builder.Default
    private Boolean priceAlerts = true;
    
    @Column(name = "new_product_alerts")
    @Builder.Default
    private Boolean newProductAlerts = false;
    
    @Column(name = "order_updates")
    @Builder.Default
    private Boolean orderUpdates = true;
    
    // ===============================
    // VERIFICATION & KYC
    // ===============================
    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status")
    @Builder.Default
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_status")
    @Builder.Default
    private KycStatus kycStatus = KycStatus.NOT_SUBMITTED;
    
    @Column(name = "kyc_submitted_at")
    private LocalDateTime kycSubmittedAt;
    
    @Column(name = "kyc_approved_at")
    private LocalDateTime kycApprovedAt;
    
    @Column(name = "kyc_approved_by")
    private String kycApprovedBy;
    
    @Column(name = "kyc_rejection_reason")
    private String kycRejectionReason;
    
    @ElementCollection
    @CollectionTable(name = "buyer_kyc_documents", joinColumns = @JoinColumn(name = "buyer_profile_id"))
    @Column(name = "document_url")
    private List<String> kycDocuments;
    
    // ===============================
    // SUBSCRIPTION & PREMIUM
    // ===============================
    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_type")
    @Builder.Default
    private SubscriptionType subscriptionType = SubscriptionType.FREE;
    
    @Column(name = "subscription_start_date")
    private LocalDateTime subscriptionStartDate;
    
    @Column(name = "subscription_end_date")
    private LocalDateTime subscriptionEndDate;
    
    @Column(name = "is_premium")
    @Builder.Default
    private Boolean isPremium = false;
    
    // ===============================
    // ANALYTICS & ENGAGEMENT
    // ===============================
    @Column(name = "total_orders", columnDefinition = "BIGINT DEFAULT 0")
    @Builder.Default
    private Long totalOrders = 0L;
    
    @Column(name = "total_order_value", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalOrderValue = BigDecimal.ZERO;
    
    @Column(name = "average_order_value", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal averageOrderValue = BigDecimal.ZERO;
    
    @Column(name = "last_order_date")
    private LocalDateTime lastOrderDate;
    
    @Column(name = "profile_views", columnDefinition = "BIGINT DEFAULT 0")
    @Builder.Default
    private Long profileViews = 0L;
    
    @Column(name = "product_views", columnDefinition = "BIGINT DEFAULT 0")
    @Builder.Default
    private Long productViews = 0L;
    
    @Column(name = "inquiries_sent", columnDefinition = "BIGINT DEFAULT 0")
    @Builder.Default
    private Long inquiriesSent = 0L;
    
    // ===============================
    // PRIVACY SETTINGS
    // ===============================
    @Enumerated(EnumType.STRING)
    @Column(name = "profile_visibility")
    @Builder.Default
    private ProfileVisibility profileVisibility = ProfileVisibility.PUBLIC;
    
    // ===============================
    // AUDIT FIELDS
    // ===============================
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "updated_by")
    private String updatedBy;
    
    // ===============================
    // ENUMS
    // ===============================
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
    
    // ===============================
    // HELPER METHODS
    // ===============================
    
    /**
     * Get buyer's full name
     */
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (buyerName != null) {
            return buyerName;
        } else if (user != null) {
            return user.getName();
        }
        return "Unknown Buyer";
    }
    
    /**
     * Check if buyer is verified
     */
    public boolean isVerified() {
        return user != null && user.isVerified() && 
               verificationStatus == VerificationStatus.VERIFIED;
    }
    
    /**
     * Check if KYC is completed
     */
    public boolean isKycCompleted() {
        return kycStatus == KycStatus.APPROVED;
    }
    
    /**
     * Update analytics when order is placed
     */
    public void incrementOrderStats(BigDecimal orderValue) {
        this.totalOrders += 1;
        this.totalOrderValue = this.totalOrderValue.add(orderValue);
        this.averageOrderValue = this.totalOrderValue.divide(BigDecimal.valueOf(this.totalOrders), 2, RoundingMode.HALF_UP);
        this.lastOrderDate = LocalDateTime.now();
    }
}
