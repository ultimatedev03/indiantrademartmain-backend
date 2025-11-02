package com.itech.itech_backend.modules.support.service;

import com.itech.marketplace.dto.*;
import java.util.List;

public interface SupportAnalyticsService {
    SupportDashboardDto getDashboardOverview();
    TicketTrendsDto getTicketTrends(Integer days);
    List<AgentPerformanceDto> getAgentPerformance();
    CategoryAnalysisDto getCategoryAnalysis();
    void bulkAssignTickets(BulkAssignmentDto bulkAssignmentDto);
}

