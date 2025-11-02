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
public class UpdateVendorDto {
    
    // Company Association
    private Long companyId;
    
    // Vendor Account Information
    @Size(max = 100, message = "Vendor name must not exceed 100 characters")
    private String vendorName;
    
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    @Pattern(regexp = "^[+]?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phone;
    
    // Profile Information
    @Size(max = 150, message = "Display name must not exceed 150 characters")
    private String displayName;
    
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;
    
    private String profileImageUrl;
    private String coverImageUrl;
    
    // Contact Person Details
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
    
    // Performance Metrics (usually updated by system, but can be manually adjusted)
    @Min(value = 1, message = "Response time must be at least 1 hour")
    @Max(value = 168, message = "Response time cannot exceed 168 hours (1 week)")
    private Integer responseTimeHours;
    
    @Min(value = 1, message = "Fulfillment time must be at least 1 day")
    @Max(value = 365, message = "Fulfillment time cannot exceed 365 days")
    private Integer fulfillmentTimeDays;
    
    // Financial Information
    @DecimalMin(value = "0.0", inclusive = false, message = "Minimum order value must be greater than zero")
    private BigDecimal minimumOrderValue;
    
    @DecimalMin(value = "0.0", message = "Credit limit must be non-negative")
    private BigDecimal creditLimit;
    
    @Min(value = 1, message = "Payment terms must be at least 1 day")
    @Max(value = 365, message = "Payment terms cannot exceed 365 days")
    private Integer paymentTermsDays;
    
    private List<Vendor.PaymentMethod> acceptedPaymentMethods;
    
    // Service Areas
    private List<String> serviceAreas;
    private Boolean deliveryAvailable;
    private Boolean installationService;
    private Boolean afterSalesSupport;
    
    // Certification and Compliance
    private List<String> certifications;
    private Boolean isoCertified;
    private Boolean qualityAssured;
    
    // Documents
    private List<String> documentUrls;
    
    // Marketing and Promotional
    private String promotionalBannerUrl;
    private String promotionalVideoUrl;
    private String socialMediaLinks;
    
    // Settings and Preferences
    private Boolean emailNotifications;
    private Boolean smsNotifications;
    private Boolean autoApproveOrders;
    private Vendor.CatalogVisibility catalogVisibility;
    
    // Admin-only fields (for admin panel updates)
    private VendorType vendorType;
    private Vendor.VendorStatus vendorStatus;
    private Boolean isActive;
    private Boolean featuredVendor;
    private Boolean priorityListing;
}

