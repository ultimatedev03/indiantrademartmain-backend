package com.itech.marketplace.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

// Ticket Trends DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketTrendsDto {
    private List<TicketTrend> trends;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TicketTrend {
        private LocalDateTime date;
        private Integer newTickets;
        private Integer resolvedTickets;
        private Integer escalatedTickets;
    }
}

