package com.itech.itech_backend.modules.buyer.dto;

import com.itech.itech_backend.enums.LeadInteraction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadResponseDto {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String company;
    private String productInterest;
    private String message;
    private String urgency;
    private String source;
    private Integer leadScore;
    private String status;
    private String assignedSalesRep;
    private String ipAddress;
    private String userAgent;
    private String notes;
    private List<LeadInteraction> interactions;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;
    private LocalDateTime followUpDate;

    // Additional fields for enhanced lead information
    private String interestedCategories;
    private String priceRange;
    private String timeline;
    private String specificRequirements;
    private String companySize;
    private String industry;
    private String currentSupplier;
    private Boolean agreesToMarketing;
    private Boolean converted;
    private Double conversionValue;
    private LocalDateTime conversionDate;
    private String conversionNotes;
    
    // Calculated fields
    private Integer daysSinceCreated;
    private Integer daysSinceLastActivity;
    private String priorityLevel;
    private Boolean needsFollowUp;
}

