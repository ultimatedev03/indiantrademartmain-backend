package com.itech.marketplace.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

// Bulk Assignment DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkAssignmentDto {
    private List<Long> ticketIds;
    private Long assignToId;
}

