package com.itech.itech_backend.modules.buyer.dto;

import com.itech.itech_backend.modules.buyer.model.Buyer;
import com.itech.itech_backend.modules.company.dto.CompanyDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuyerDto {
    
    private Long id;
    private CompanyDto company;
    
    // Buyer Account Information
    private String buyerName;
    private String email;
    private String phone;
    
    // Buyer Type and Status
    private Buyer.BuyerType buyerType;
    private Buyer.BuyerStatus buyerStatus;
    private Boolean isVerified;
    private Boolean isPremium;
    
    // Personal/Company Information
    private String firstName;
    private String lastName;
    private String displayName;
    private String jobTitle;
    private String department;
    private String bio;
    private String profileImageUrl;
    
    // Contact Information
    private String secondaryEmail;
    private String secondaryPhone;
    private String linkedinUrl;
    private String websiteUrl;
    
    // Address Information
    private String billingAddressLine1;
    private String billingAddressLine2;
    private String billingCity;
    private String billingState;
    private String billingPostalCode;
    private String billingCountry;
    
    private String shippingAddressLine1;
    private String shippingAddressLine2;
    private String shippingCity;
    private String shippingState;
    private String shippingPostalCode;
    private String shippingCountry;
    private Boolean sameAsBilling;
    
    // Business Information
    private Buyer.BusinessType businessType;
    private List<Buyer.Industry> industries;
    private Buyer.CompanySize companySize;
    private BigDecimal annualBudget;
    private Buyer.PurchasingAuthority purchasingAuthority;
    
    // Purchasing Preferences
    private List<String> preferredCategories;
    private List<Buyer.PaymentMethod> preferredPaymentMethods;
    private BigDecimal creditLimit;
    private Integer paymentTermsPreference;
    
    // Purchase History and Analytics
    private Long totalOrders;
    private Long completedOrders;
    private Long cancelledOrders;
    private BigDecimal totalSpent;
    private BigDecimal averageOrderValue;
    private LocalDateTime lastOrderDate;
    private String favoriteVendors;
    
    // Engagement and Activity
    private Long profileViews;
    private Long productViews;
    private Long inquiriesSent;
    private Long quotesRequested;
    private Integer reviewsWritten;
    private Integer wishlistItems;
    
    // Communication Preferences
    private Boolean emailNotifications;
    private Boolean smsNotifications;
    private Boolean marketingEmails;
    private Boolean priceAlerts;
    private Boolean newProductAlerts;
    private Boolean orderUpdates;
    
    // Verification and KYC
    private Boolean kycSubmitted;
    private Boolean kycApproved;
    private LocalDateTime kycSubmittedAt;
    private LocalDateTime kycApprovedAt;
    private String kycApprovedBy;
    private String kycRejectionReason;
    
    // Documents
    private List<String> documentUrls;
    
    // Subscription and Premium Features
    private Buyer.SubscriptionType subscriptionType;
    private LocalDateTime subscriptionStartDate;
    private LocalDateTime subscriptionEndDate;
    
    // Activity Tracking
    private LocalDateTime lastLogin;
    private LocalDateTime lastActivity;
    private Long loginCount;
    private Long sessionDurationMinutes;
    
    // Privacy and Security
    private Buyer.ProfileVisibility profileVisibility;
    private Boolean twoFactorEnabled;
    private Boolean emailVerified;
    private Boolean phoneVerified;
    
    // Audit Fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    
    // Computed fields for API responses
    private Double orderCompletionRate; // completedOrders / totalOrders * 100
    private Double orderCancellationRate; // cancelledOrders / totalOrders * 100
    private Boolean isActiveCustomer; // ordered in last 90 days
    private Boolean isSubscriptionActive;
    private Integer daysUntilSubscriptionExpiry;
    private String buyerTier; // BRONZE, SILVER, GOLD, PLATINUM based on total spent
    private Boolean isEligibleForPremium;
    private Integer daysSinceLastOrder;
    private Double engagementScore; // based on activity metrics
}

