package com.itech.marketplace.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

// Overdue Ticket DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OverdueTicketDto {
    private Long ticketId;
    private String ticketNumber;
    private String subject;
    private LocalDateTime dueDate;
}

