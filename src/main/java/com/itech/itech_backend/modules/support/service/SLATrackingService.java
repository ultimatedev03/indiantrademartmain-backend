package com.itech.itech_backend.modules.support.service;

import com.itech.marketplace.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface SLATrackingService {
    Page<SLATrackingDto> getSLATracking(String status, Boolean breached, Pageable pageable);
    SLAReportDto generateSLAReport(String period);
    SLAComplianceDto getSLACompliance();
    void escalateTicket(Long ticketId, EscalationDto escalationDto);
    List<OverdueTicketDto> getOverdueTickets();
}

