package com.itech.itech_backend.modules.buyer.dto;

import com.itech.itech_backend.modules.buyer.model.Buyer;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBuyerDto {
    
    // Company Association
    private Long companyId;
    
    // Buyer Account Information (some fields like email should be updated carefully)
    @Size(max = 100, message = "Buyer name must not exceed 100 characters")
    private String buyerName;
    
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    @Pattern(regexp = "^[+]?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phone;
    
    // Buyer Type (be careful with updates as it affects business logic)
    private Buyer.BuyerType buyerType;
    
    // Personal Information
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;
    
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;
    
    @Size(max = 150, message = "Display name must not exceed 150 characters")
    private String displayName;
    
    @Size(max = 100, message = "Job title must not exceed 100 characters")
    private String jobTitle;
    
    @Size(max = 100, message = "Department must not exceed 100 characters")
    private String department;
    
    @Size(max = 1000, message = "Bio must not exceed 1000 characters")
    private String bio;
    
    // Contact Information
    @Email(message = "Invalid secondary email format")
    @Size(max = 100, message = "Secondary email must not exceed 100 characters")
    private String secondaryEmail;
    
    @Pattern(regexp = "^[+]?[1-9]\\d{1,14}$", message = "Invalid secondary phone number format")
    private String secondaryPhone;
    
    @Pattern(regexp = "^(https?://)?(www\\.)?linkedin\\.com/.*$", message = "Invalid LinkedIn URL")
    private String linkedinUrl;
    
    @Pattern(regexp = "^(https?://)?(www\\.)?[a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.[a-zA-Z]{2,}(/.*)?$", 
             message = "Invalid website URL format")
    private String websiteUrl;
    
    // Address Information
    private String billingAddressLine1;
    private String billingAddressLine2;
    
    @Size(max = 50, message = "City name must not exceed 50 characters")
    private String billingCity;
    
    @Size(max = 50, message = "State name must not exceed 50 characters")
    private String billingState;
    
    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Invalid postal code format")
    private String billingPostalCode;
    
    @Size(max = 50, message = "Country name must not exceed 50 characters")
    private String billingCountry;
    
    // Shipping Address
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
    
    @DecimalMin(value = "0.0", message = "Annual budget must be non-negative")
    private BigDecimal annualBudget;
    
    private Buyer.PurchasingAuthority purchasingAuthority;
    
    // Purchasing Preferences
    private List<String> preferredCategories;
    private List<Buyer.PaymentMethod> preferredPaymentMethods;
    
    @DecimalMin(value = "0.0", message = "Credit limit must be non-negative")
    private BigDecimal creditLimit;
    
    @Min(value = 0, message = "Payment terms preference must be non-negative")
    @Max(value = 365, message = "Payment terms preference cannot exceed 365 days")
    private Integer paymentTermsPreference;
    
    // Communication Preferences
    private Boolean emailNotifications;
    private Boolean smsNotifications;
    private Boolean marketingEmails;
    private Boolean priceAlerts;
    private Boolean newProductAlerts;
    private Boolean orderUpdates;
    
    // Privacy Settings
    private Buyer.ProfileVisibility profileVisibility;
    private Boolean twoFactorEnabled;
    
    // Marketing preferences
    private Boolean allowMarketingEmails;
    private Boolean allowPromotionalSms;
    
    // Status updates (be careful - might need admin privileges)
    private Buyer.BuyerStatus status;
    
    // Subscription preferences
    private Buyer.SubscriptionType subscriptionType;
    private Boolean autoRenewSubscription;
    
    // Additional profile settings
    private String timeZone;
    private String language;
    private String currency;
    
    // Note: Password updates should be handled separately through dedicated endpoints
    // Note: Sensitive fields like verification status, KYC info should be handled through dedicated admin endpoints
}

