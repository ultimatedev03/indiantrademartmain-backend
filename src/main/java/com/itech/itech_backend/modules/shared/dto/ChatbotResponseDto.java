package com.itech.itech_backend.modules.shared.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatbotResponseDto {
    
    private String response;
    private String sessionId;
    private List<VendorRecommendationDto> recommendations;
    private boolean hasRecommendations;
    
    // New fields for role-based responses
    private List<LeadRecommendationDto> leadRecommendations;
    private boolean hasLeadRecommendations;
    private String responseType; // VENDOR_RECOMMENDATIONS, LEAD_RECOMMENDATIONS, GENERAL
    private String userRole; // NON_LOGGED, BUYER, VENDOR, ADMIN
    private boolean requiresLogin;
    private String suggestedAction; // LOGIN, CONTACT_VENDOR, VIEW_LEADS, etc.
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VendorRecommendationDto {
        private Long vendorId;
        private String vendorName;
        private String vendorEmail;
        private String vendorPhone;
        private String vendorType;
        private Double performanceScore;
        private List<String> products;
        private List<String> categories;
        private String reason; // Why this vendor is recommended
        private String contactUrl; // Direct contact link
        private String profileUrl; // Vendor profile page
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LeadRecommendationDto {
        private Long leadId;
        private String leadName;
        private String leadEmail;
        private String leadPhone;
        private String company;
        private String productInterest;
        private String urgency;
        private Integer leadScore;
        private String status;
        private LocalDateTime createdAt;
        private String reason; // Why this lead is recommended
        private String contactUrl; // Direct contact link
        private List<String> interestedCategories;
        private String priceRange;
        private String timeline;
    }
}

