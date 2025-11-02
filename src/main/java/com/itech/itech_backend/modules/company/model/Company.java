package com.itech.itech_backend.modules.company.model;

import com.itech.itech_backend.modules.core.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "companies", indexes = {
    @Index(name = "idx_company_gst", columnList = "gstNumber"),
    @Index(name = "idx_company_pan", columnList = "panNumber"),
    @Index(name = "idx_company_status", columnList = "status"),
    @Index(name = "idx_company_verification", columnList = "verificationStatus")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id")
    private User ownerUser;

    @Column(name = "company_name", nullable = false, length = 150)
    private String companyName;
    
    @Column(name = "legal_name", length = 150)
    private String legalName;
    
    @Column(unique = true, length = 20)
    private String gstNumber;
    
    @Column(unique = true, length = 21)
    private String panNumber;
    
    @Column(length = 20)
    private String cinNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "company_type")
    private CompanyType companyType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "business_category")
    private BusinessCategory businessCategory;
    
    @Column(name = "established_year")
    private Integer establishedYear;
    
    @Column(name = "employee_count")
    @Enumerated(EnumType.STRING)
    private EmployeeCount employeeCount;
    
    @Column(name = "annual_turnover")
    @Enumerated(EnumType.STRING)
    private AnnualTurnover annualTurnover;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "website_url")
    private String websiteUrl;
    
    // Contact Information
    @Column(name = "primary_email", length = 100)
    private String primaryEmail;
    
    @Column(name = "primary_phone", length = 20)
    private String primaryPhone;
    
    @Column(name = "secondary_phone", length = 20)
    private String secondaryPhone;
    
    // Address Information
    @Column(name = "address_line1")
    private String addressLine1;
    
    @Column(name = "address_line2")
    private String addressLine2;
    
    @Column(length = 50)
    private String city;
    
    @Column(length = 50)
    private String state;
    
    @Column(name = "postal_code", length = 10)
    private String postalCode;
    
    @Column(length = 50)
    private String country = "India";
    
    // Verification Status
    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status")
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;
    
    @Column(name = "is_verified")
    private Boolean isVerified = false;
    
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;
    
    @Column(name = "verified_by")
    private String verifiedBy;
    
    // Business Preferences
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "company_industries", joinColumns = @JoinColumn(name = "company_id"))
    @Column(name = "industry")
    private List<Industry> industries;
    
    @ElementCollection
    @CollectionTable(name = "company_certifications", joinColumns = @JoinColumn(name = "company_id"))
    @Column(name = "certification")
    private List<String> certifications;
    
    // Social Media & Online Presence
    @Column(name = "linkedin_url")
    private String linkedinUrl;
    
    @Column(name = "facebook_url")
    private String facebookUrl;
    
    @Column(name = "twitter_url")
    private String twitterUrl;
    
    @Column(name = "instagram_url")
    private String instagramUrl;
    
    // Logo and Images
    @Column(name = "logo_url")
    private String logoUrl;
    
    @ElementCollection
    @CollectionTable(name = "company_images", joinColumns = @JoinColumn(name = "company_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;
    
    // Business Hours
    @Column(name = "business_hours")
    private String businessHours;
    
    @Column(name = "working_days")
    private String workingDays;
    
    // Banking Information
    @Column(name = "bank_name")
    private String bankName;
    
    @Column(name = "account_number")
    private String accountNumber;
    
    @Column(name = "ifsc_code")
    private String ifscCode;
    
    @Column(name = "account_holder_name")
    private String accountHolderName;
    
    // Status and Metadata
    @Enumerated(EnumType.STRING)
    private CompanyStatus status = CompanyStatus.ACTIVE;
    
    @Column(name = "is_premium")
    private Boolean isPremium = false;
    
    @Column(name = "premium_expires_at")
    private LocalDateTime premiumExpiresAt;
    
    @Column(name = "subscription_type")
    @Enumerated(EnumType.STRING)
    private SubscriptionType subscriptionType = SubscriptionType.FREE;
    
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
    public enum CompanyType {
        PRIVATE_LIMITED, PUBLIC_LIMITED, PARTNERSHIP, PROPRIETORSHIP, LLP, NGO, TRUST, SOCIETY
    }
    
    public enum BusinessCategory {
        MANUFACTURER, TRADER, SERVICE_PROVIDER, IMPORTER, EXPORTER, RETAILER, WHOLESALER, DISTRIBUTOR
    }
    
    public enum EmployeeCount {
        ONE_TO_TEN, ELEVEN_TO_FIFTY, FIFTY_ONE_TO_HUNDRED, HUNDRED_ONE_TO_FIVE_HUNDRED, FIVE_HUNDRED_PLUS
    }
    
    public enum AnnualTurnover {
        UPTO_1_CRORE, ONE_TO_TEN_CRORE, TEN_TO_FIFTY_CRORE, FIFTY_TO_HUNDRED_CRORE, HUNDRED_CRORE_PLUS
    }
    
    public enum VerificationStatus {
        PENDING, IN_PROGRESS, VERIFIED, REJECTED
    }
    
    public enum Industry {
        AGRICULTURE, AUTOMOTIVE, CHEMICALS, CONSTRUCTION, ELECTRONICS, ENERGY, FOOD_BEVERAGE,
        HEALTHCARE, INFORMATION_TECHNOLOGY, MANUFACTURING, MINING, PHARMACEUTICALS, 
        TEXTILES, TRANSPORTATION, TELECOMMUNICATIONS, EDUCATION, FINANCE, REAL_ESTATE
    }
    
    public enum CompanyStatus {
        ACTIVE, INACTIVE, SUSPENDED, BLOCKED
    }
    
    public enum SubscriptionType {
        FREE, BASIC, PREMIUM, ENTERPRISE
    }
    
    // Backward compatibility methods
    public String getName() {
        return this.companyName;
    }
    
    public void setName(String name) {
        this.companyName = name;
    }
}

