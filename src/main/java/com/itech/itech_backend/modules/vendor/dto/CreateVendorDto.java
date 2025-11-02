package com.itech.itech_backend.modules.vendor.dto;

import com.itech.itech_backend.enums.VendorType;
import com.itech.itech_backend.modules.vendor.model.Vendor;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateVendorDto {
    
    // Company Association (Optional - can be linked later)
    private Long companyId;
    
    // Vendor Account Information
    @NotBlank(message = "Vendor name is required")
    @Size(max = 100, message = "Vendor name must not exceed 100 characters")
    private String vendorName;
    
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
    
    // Vendor Type (defaults to BASIC)
    private VendorType vendorType = VendorType.BASIC;
    
    // Profile Information
    @Size(max = 150, message = "Display name must not exceed 150 characters")
    private String displayName;
    
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;
    
    // Contact Person Details
    @NotBlank(message = "Contact person name is required")
    @Size(max = 100, message = "Contact person name must not exceed 100 characters")
    private String contactPersonName;
    
    @Size(max = 100, message = "Contact person designation must not exceed 100 characters")
    private String contactPersonDesignation;
    
    @Pattern(regexp = "^[+]?[1-9]\\d{1,14}$", message = "Invalid contact person phone number format")
    private String contactPersonPhone;
    
    @Email(message = "Invalid contact person email format")
    @Size(max = 100, message = "Contact person email must not exceed 100 characters")
    private String contactPersonEmail;
    
    // Business Information
    @Min(value = 1800, message = "Established year must be valid")
    @Max(value = 2024, message = "Established year cannot be in the future")
    private Integer establishedYear;
    
    @Size(max = 100, message = "Business type must not exceed 100 characters")
    private String businessType;
    
    private List<Vendor.BusinessCategory> categories;
    private List<String> specializations;
    
    // Financial Information
    @DecimalMin(value = "0.0", inclusive = false, message = "Minimum order value must be greater than zero")
    private BigDecimal minimumOrderValue;
    
    @Min(value = 1, message = "Payment terms must be at least 1 day")
    @Max(value = 365, message = "Payment terms cannot exceed 365 days")
    private Integer paymentTermsDays;
    
    private List<Vendor.PaymentMethod> acceptedPaymentMethods;
    
    // Service Areas
    private List<String> serviceAreas;
    private Boolean deliveryAvailable = false;
    private Boolean installationService = false;
    private Boolean afterSalesSupport = false;
    
    // Certification and Compliance
    private List<String> certifications;
    private Boolean isoCertified = false;
    private Boolean qualityAssured = false;
    
    // Settings and Preferences
    private Boolean emailNotifications = true;
    private Boolean smsNotifications = true;
    private Boolean autoApproveOrders = false;
    private Vendor.CatalogVisibility catalogVisibility = Vendor.CatalogVisibility.PUBLIC;
    
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
}

