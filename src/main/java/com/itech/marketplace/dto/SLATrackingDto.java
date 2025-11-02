package com.itech.marketplace.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

// SLA Tracking DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SLATrackingDto {
    private Long id;
    private Long ticketId;
    private String ticketNumber;
    private String status;
    private String priority;
    private LocalDateTime responseDeadline;
    private LocalDateTime resolutionDeadline;
    private Boolean responseBreached;
    private Boolean resolutionBreached;
    private Double responseComplianceScore;
    private Double resolutionComplianceScore;
}

