package com.itech.itech_backend.modules.company.dto;

import com.itech.itech_backend.modules.company.model.Company;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompanyDto {
    
    @Size(max = 100, message = "Company name must not exceed 100 characters")
    private String name;
    
    @Size(max = 150, message = "Legal name must not exceed 150 characters")
    private String legalName;
    
    @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$", message = "Invalid GST number format")
    private String gstNumber;
    
    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "Invalid PAN number format")
    private String panNumber;
    
    @Size(max = 21, message = "CIN number must not exceed 21 characters")
    private String cinNumber;
    
    private Company.CompanyType companyType;
    private Company.BusinessCategory businessCategory;
    
    @Min(value = 1800, message = "Established year must be valid")
    @Max(value = 2024, message = "Established year cannot be in the future")
    private Integer establishedYear;
    
    private Company.EmployeeCount employeeCount;
    private Company.AnnualTurnover annualTurnover;
    
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;
    
    @Pattern(regexp = "^(https?://)?(www\\.)?[a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.[a-zA-Z]{2,}(/.*)?$", 
             message = "Invalid website URL format")
    private String websiteUrl;
    
    // Contact Information
    @Email(message = "Invalid email format")
    private String primaryEmail;
    
    @Pattern(regexp = "^[+]?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String primaryPhone;
    
    @Pattern(regexp = "^[+]?[1-9]\\d{1,14}$", message = "Invalid secondary phone number format")
    private String secondaryPhone;
    
    // Address Information
    private String addressLine1;
    private String addressLine2;
    
    @Size(max = 50, message = "City name must not exceed 50 characters")
    private String city;
    
    @Size(max = 50, message = "State name must not exceed 50 characters")
    private String state;
    
    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Invalid postal code format")
    private String postalCode;
    
    @Size(max = 50, message = "Country name must not exceed 50 characters")
    private String country;
    
    // Business Preferences
    private List<Company.Industry> industries;
    private List<String> certifications;
    
    // Social Media & Online Presence
    @Pattern(regexp = "^(https?://)?(www\\.)?linkedin\\.com/.*$", message = "Invalid LinkedIn URL")
    private String linkedinUrl;
    
    @Pattern(regexp = "^(https?://)?(www\\.)?facebook\\.com/.*$", message = "Invalid Facebook URL")
    private String facebookUrl;
    
    @Pattern(regexp = "^(https?://)?(www\\.)?twitter\\.com/.*$", message = "Invalid Twitter URL")
    private String twitterUrl;
    
    @Pattern(regexp = "^(https?://)?(www\\.)?instagram\\.com/.*$", message = "Invalid Instagram URL")
    private String instagramUrl;
    
    // Logo and Images
    private String logoUrl;
    private List<String> imageUrls;
    
    // Business Hours
    private String businessHours;
    private String workingDays;
    
    // Banking Information
    private String bankName;
    private String accountNumber;
    private String ifscCode;
    private String accountHolderName;
    
    // Status Updates (Only for admin users)
    private Company.CompanyStatus status;
}

