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
public class CreateBuyerDto {
    
    // Company Association (Optional - can be linked later)
    private Long companyId;
    
    // Buyer Account Information
    @NotBlank(message = "Buyer name is required")
    @Size(max = 100, message = "Buyer name must not exceed 100 characters")
    private String buyerName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+]?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phone;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]", 
             message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
    private String password;
    
    // Buyer Type (defaults to INDIVIDUAL)
    @NotNull(message = "Buyer type is required")
    private Buyer.BuyerType buyerType = Buyer.BuyerType.INDIVIDUAL;
    
    // Personal Information
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
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
    @NotBlank(message = "Billing address is required")
    private String billingAddressLine1;
    
    private String billingAddressLine2;
    
    @NotBlank(message = "Billing city is required")
    @Size(max = 50, message = "City name must not exceed 50 characters")
    private String billingCity;
    
    @NotBlank(message = "Billing state is required")
    @Size(max = 50, message = "State name must not exceed 50 characters")
    private String billingState;
    
    @NotBlank(message = "Billing postal code is required")
    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Invalid postal code format")
    private String billingPostalCode;
    
    @Size(max = 50, message = "Country name must not exceed 50 characters")
    private String billingCountry = "India";
    
    // Shipping Address (optional, can use billing if same)
    private String shippingAddressLine1;
    private String shippingAddressLine2;
    private String shippingCity;
    private String shippingState;
    private String shippingPostalCode;
    private String shippingCountry = "India";
    private Boolean sameAsBilling = true;
    
    // Business Information (for B2B buyers)
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
    private Boolean emailNotifications = true;
    private Boolean smsNotifications = false;
    private Boolean marketingEmails = true;
    private Boolean priceAlerts = true;
    private Boolean newProductAlerts = false;
    private Boolean orderUpdates = true;
    
    // Privacy Settings
    private Buyer.ProfileVisibility profileVisibility = Buyer.ProfileVisibility.PUBLIC;
    private Boolean twoFactorEnabled = false;
    
    // Terms and Conditions Acceptance
    @NotNull(message = "Terms and conditions must be accepted")
    @AssertTrue(message = "Terms and conditions must be accepted")
    private Boolean acceptedTermsAndConditions;
    
    @NotNull(message = "Privacy policy must be accepted")
    @AssertTrue(message = "Privacy policy must be accepted")
    private Boolean acceptedPrivacyPolicy;
    
    // Marketing preferences
    private Boolean allowMarketingEmails = true;
    private Boolean allowPromotionalSms = false;
    
    // Referral information (optional)
    private String referralCode;
    private String referralSource; // How did you hear about us
}

