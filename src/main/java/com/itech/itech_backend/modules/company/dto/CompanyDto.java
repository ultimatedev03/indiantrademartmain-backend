package com.itech.itech_backend.modules.company.dto;

import com.itech.itech_backend.modules.company.model.Company;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {
    
    private Long id;
    private String name;
    private String legalName;
    private String gstNumber;
    private String panNumber;
    private String cinNumber;
    private Company.CompanyType companyType;
    private Company.BusinessCategory businessCategory;
    private Integer establishedYear;
    private Company.EmployeeCount employeeCount;
    private Company.AnnualTurnover annualTurnover;
    private String description;
    private String websiteUrl;
    
    // Contact Information
    private String primaryEmail;
    private String primaryPhone;
    private String secondaryPhone;
    
    // Address Information
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    
    // Verification Status
    private Company.VerificationStatus verificationStatus;
    private Boolean isVerified;
    private LocalDateTime verifiedAt;
    private String verifiedBy;
    
    // Business Preferences
    private List<Company.Industry> industries;
    private List<String> certifications;
    
    // Social Media & Online Presence
    private String linkedinUrl;
    private String facebookUrl;
    private String twitterUrl;
    private String instagramUrl;
    
    // Logo and Images
    private String logoUrl;
    private List<String> imageUrls;
    
    // Business Hours
    private String businessHours;
    private String workingDays;
    
    // Status and Metadata
    private Company.CompanyStatus status;
    private Boolean isPremium;
    private LocalDateTime premiumExpiresAt;
    private Company.SubscriptionType subscriptionType;
    
    // Audit Fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}

