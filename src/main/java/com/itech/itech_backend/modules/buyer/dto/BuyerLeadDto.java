package com.itech.itech_backend.modules.buyer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyerLeadDto {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Please provide a valid phone number")
    private String phone;

    @Size(max = 100, message = "Company name cannot exceed 100 characters")
    private String company;

    @Size(max = 200, message = "Product interest cannot exceed 200 characters")
    private String productInterest;

    @Size(max = 1000, message = "Message cannot exceed 1000 characters")
    private String message;

    @Pattern(regexp = "^(LOW|MEDIUM|HIGH)$", message = "Urgency must be LOW, MEDIUM, or HIGH")
    private String urgency;

    @Pattern(regexp = "^(WEBSITE|SOCIAL_MEDIA|EMAIL|PHONE|REFERRAL|DIRECT)$", 
             message = "Source must be a valid lead source")
    private String source;

    // Additional fields for enhanced lead capture
    private String interestedCategories;
    private String priceRange;
    private String timeline;
    private String specificRequirements;
    private String companySize;
    private String industry;
    private String currentSupplier;
    private Boolean agreesToMarketing;
}

