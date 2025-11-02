package com.itech.itech_backend.modules.buyer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadStatsDto {

    private Integer totalLeads;
    private Integer newLeads;
    private Integer contactedLeads;
    private Integer qualifiedLeads;
    private Integer convertedLeads;
    private Integer closedLostLeads;
    
    private Double conversionRate;
    private Double contactRate;
    private Double qualificationRate;
    private Double averageScore;
    private Double averageConversionValue;
    
    private Integer periodDays;
    private String salesRep;
    
    // Additional metrics
    private Integer highPriorityLeads;
    private Integer mediumPriorityLeads;
    private Integer lowPriorityLeads;
    
    private Integer leadsNeedingFollowUp;
    private Integer staleLeads;
    private Integer unassignedLeads;
    
    // Source breakdown
    private Integer websiteLeads;
    private Integer socialMediaLeads;
    private Integer emailLeads;
    private Integer referralLeads;
    private Integer directLeads;
    
    // Time-based metrics
    private Double averageTimeToContact;
    private Double averageTimeToConversion;
    
    // Performance indicators
    private String trendDirection; // UP, DOWN, STABLE
    private Double trendPercentage;
    private String topPerformingSource;
    private String topPerformingSalesRep;
}

