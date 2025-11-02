package com.itech.marketplace.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// Escalation DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EscalationDto {
    private Long ticketId;
    private Long escalateToId;
    private String reason;
}

