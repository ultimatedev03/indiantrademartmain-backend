package com.itech.itech_backend.controller;

import com.itech.itech_backend.modules.shared.dto.*;
import com.itech.itech_backend.modules.payment.model.*;
import com.itech.itech_backend.modules.shared.service.*;
import com.itech.itech_backend.modules.support.service.SLATrackingService;
import com.itech.itech_backend.modules.support.service.KnowledgeBaseService;
import com.itech.itech_backend.modules.support.service.SupportAnalyticsService;
import com.itech.itech_backend.modules.support.service.SLAConfigurationService;
import jakarta.validation.Valid;

import com.itech.marketplace.dto.*;
import com.itech.marketplace.entity.*;
import com.itech.marketplace.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/support-dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SupportDashboardController {

    private final SLATrackingService slaTrackingService;
    private final KnowledgeBaseService knowledgeBaseService;
    private final SupportAnalyticsService supportAnalyticsService;
    private final SLAConfigurationService slaConfigurationService;

    // SLA Management
    @GetMapping("/sla/configurations")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPORT')")
    public ResponseEntity<List<SLAConfiguration>> getSLAConfigurations() {
        List<SLAConfiguration> configs = slaConfigurationService.getAllConfigurations();
        return ResponseEntity.ok(configs);
    }

    @PostMapping("/sla/configurations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SLAConfiguration> createSLAConfiguration(@Valid @RequestBody SLAConfigurationDto dto) {
        SLAConfiguration config = slaConfigurationService.createConfiguration(dto);
        return ResponseEntity.ok(config);
    }

    @PutMapping("/sla/configurations/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SLAConfiguration> updateSLAConfiguration(
            @PathVariable Long id, 
            @Valid @RequestBody SLAConfigurationDto dto) {
        SLAConfiguration config = slaConfigurationService.updateConfiguration(id, dto);
        return ResponseEntity.ok(config);
    }

    // SLA Tracking and Reports
    @GetMapping("/sla/tracking")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPORT')")
    public ResponseEntity<Page<SLATrackingDto>> getSLATracking(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean breached,
            Pageable pageable) {
        Page<SLATrackingDto> tracking = slaTrackingService.getSLATracking(status, breached, pageable);
        return ResponseEntity.ok(tracking);
    }

    @GetMapping("/sla/reports/overview")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPORT')")
    public ResponseEntity<SLAReportDto> getSLAOverview(
            @RequestParam(required = false) String period) {
        SLAReportDto report = slaTrackingService.generateSLAReport(period);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/sla/reports/compliance")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPORT')")
    public ResponseEntity<SLAComplianceDto> getSLACompliance() {
        SLAComplianceDto compliance = slaTrackingService.getSLACompliance();
        return ResponseEntity.ok(compliance);
    }

    // Knowledge Base Management
    @GetMapping("/knowledge-base/articles")
    public ResponseEntity<Page<KnowledgeBaseArticle>> getKnowledgeBaseArticles(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean published,
            Pageable pageable) {
        Page<KnowledgeBaseArticle> articles = knowledgeBaseService.getArticles(category, search, published, pageable);
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/knowledge-base/articles/{id}")
    public ResponseEntity<KnowledgeBaseArticle> getKnowledgeBaseArticle(@PathVariable Long id) {
        KnowledgeBaseArticle article = knowledgeBaseService.getArticleById(id);
        knowledgeBaseService.incrementViewCount(id);
        return ResponseEntity.ok(article);
    }

    @GetMapping("/knowledge-base/articles/slug/{slug}")
    public ResponseEntity<KnowledgeBaseArticle> getKnowledgeBaseArticleBySlug(@PathVariable String slug) {
        KnowledgeBaseArticle article = knowledgeBaseService.getArticleBySlug(slug);
        knowledgeBaseService.incrementViewCount(article.getId());
        return ResponseEntity.ok(article);
    }

    @PostMapping("/knowledge-base/articles")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPORT')")
    public ResponseEntity<KnowledgeBaseArticle> createKnowledgeBaseArticle(@Valid @RequestBody KnowledgeBaseArticleDto dto) {
        KnowledgeBaseArticle article = knowledgeBaseService.createArticle(dto);
        return ResponseEntity.ok(article);
    }

    @PutMapping("/knowledge-base/articles/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPORT')")
    public ResponseEntity<KnowledgeBaseArticle> updateKnowledgeBaseArticle(
            @PathVariable Long id, 
            @Valid @RequestBody KnowledgeBaseArticleDto dto) {
        KnowledgeBaseArticle article = knowledgeBaseService.updateArticle(id, dto);
        return ResponseEntity.ok(article);
    }

    @DeleteMapping("/knowledge-base/articles/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteKnowledgeBaseArticle(@PathVariable Long id) {
        knowledgeBaseService.deleteArticle(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/knowledge-base/articles/{id}/feedback")
    public ResponseEntity<Void> submitArticleFeedback(
            @PathVariable Long id, 
            @RequestBody Map<String, Boolean> feedback) {
        boolean helpful = feedback.getOrDefault("helpful", false);
        knowledgeBaseService.submitFeedback(id, helpful);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/knowledge-base/categories")
    public ResponseEntity<List<String>> getKnowledgeBaseCategories() {
        List<String> categories = knowledgeBaseService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/knowledge-base/featured")
    public ResponseEntity<List<KnowledgeBaseArticle>> getFeaturedArticles() {
        List<KnowledgeBaseArticle> articles = knowledgeBaseService.getFeaturedArticles();
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/knowledge-base/popular")
    public ResponseEntity<List<KnowledgeBaseArticle>> getPopularArticles() {
        List<KnowledgeBaseArticle> articles = knowledgeBaseService.getPopularArticles();
        return ResponseEntity.ok(articles);
    }

    // Support Analytics and Dashboard
    @GetMapping("/analytics/dashboard")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPORT')")
    public ResponseEntity<SupportDashboardDto> getSupportDashboard() {
        SupportDashboardDto dashboard = supportAnalyticsService.getDashboardOverview();
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/analytics/ticket-trends")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPORT')")
    public ResponseEntity<TicketTrendsDto> getTicketTrends(
            @RequestParam(required = false, defaultValue = "30") Integer days) {
        TicketTrendsDto trends = supportAnalyticsService.getTicketTrends(days);
        return ResponseEntity.ok(trends);
    }

    @GetMapping("/analytics/agent-performance")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPORT')")
    public ResponseEntity<List<AgentPerformanceDto>> getAgentPerformance() {
        List<AgentPerformanceDto> performance = supportAnalyticsService.getAgentPerformance();
        return ResponseEntity.ok(performance);
    }

    @GetMapping("/analytics/category-analysis")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPORT')")
    public ResponseEntity<CategoryAnalysisDto> getCategoryAnalysis() {
        CategoryAnalysisDto analysis = supportAnalyticsService.getCategoryAnalysis();
        return ResponseEntity.ok(analysis);
    }

    // Advanced Support Features
    @PostMapping("/tickets/{ticketId}/escalate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPORT')")
    public ResponseEntity<Void> escalateTicket(
            @PathVariable Long ticketId,
            @RequestBody EscalationDto escalationDto) {
        slaTrackingService.escalateTicket(ticketId, escalationDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tickets/bulk-assign")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPORT')")
    public ResponseEntity<Void> bulkAssignTickets(@RequestBody BulkAssignmentDto bulkAssignmentDto) {
        supportAnalyticsService.bulkAssignTickets(bulkAssignmentDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tickets/overdue")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPORT')")
    public ResponseEntity<List<OverdueTicketDto>> getOverdueTickets() {
        List<OverdueTicketDto> tickets = slaTrackingService.getOverdueTickets();
        return ResponseEntity.ok(tickets);
    }

    @PostMapping("/auto-responses/suggest")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPORT')")
    public ResponseEntity<List<AutoResponseSuggestionDto>> suggestAutoResponses(@RequestBody SuggestResponseDto dto) {
        List<AutoResponseSuggestionDto> suggestions = knowledgeBaseService.suggestAutoResponses(dto);
        return ResponseEntity.ok(suggestions);
    }
}

