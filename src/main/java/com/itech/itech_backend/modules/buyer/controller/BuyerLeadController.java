package com.itech.itech_backend.modules.buyer.controller;

import com.itech.itech_backend.modules.buyer.dto.BuyerLeadDto;
import com.itech.itech_backend.modules.buyer.dto.LeadResponseDto;
import com.itech.itech_backend.modules.buyer.dto.LeadStatsDto;
import com.itech.itech_backend.modules.shared.model.BuyerLead;
import com.itech.itech_backend.modules.buyer.service.BuyerLeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/buyer-leads")
@RequiredArgsConstructor
public class BuyerLeadController {

    private final BuyerLeadService buyerLeadService;

    // Create new lead (public endpoint for lead capture)
    @PostMapping
    public ResponseEntity<LeadResponseDto> createLead(
            @Valid @RequestBody BuyerLeadDto leadDto,
            HttpServletRequest request) {
        
        System.out.println("🔥 Buyer lead creation request received: " + leadDto.getEmail());
        
        // Capture IP and User Agent for tracking
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        
        LeadResponseDto response = buyerLeadService.createLead(leadDto, ipAddress, userAgent);
        
        System.out.println("✅ Buyer lead created successfully with ID: " + response.getId());
        
        return ResponseEntity.ok(response);
    }

    // Get leads for sales team (admin/sales role)
    @GetMapping("/sales")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES')")
    public ResponseEntity<Page<LeadResponseDto>> getLeadsForSales(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String urgency,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String assignedRep,
            @RequestParam(required = false) Integer minScore,
            @RequestParam(required = false) String search) {
        
        Pageable pageable = PageRequest.of(page, size);
        
        Page<LeadResponseDto> leads = buyerLeadService.getLeadsForSales(
            status, urgency, source, assignedRep, minScore, search, pageable);
        
        return ResponseEntity.ok(leads);
    }

    // Get lead by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES')")
    public ResponseEntity<LeadResponseDto> getLeadById(@PathVariable Long id) {
        LeadResponseDto lead = buyerLeadService.getLeadById(id);
        return ResponseEntity.ok(lead);
    }

    // Update lead status
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES')")
    public ResponseEntity<LeadResponseDto> updateLeadStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String notes) {
        
        LeadResponseDto updatedLead = buyerLeadService.updateLeadStatus(id, status, notes);
        return ResponseEntity.ok(updatedLead);
    }

    // Assign lead to sales rep
    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LeadResponseDto> assignLead(
            @PathVariable Long id,
            @RequestParam String salesRep,
            @RequestParam(required = false) String notes) {
        
        LeadResponseDto updatedLead = buyerLeadService.assignLead(id, salesRep, notes);
        return ResponseEntity.ok(updatedLead);
    }

    // Get high priority leads
    @GetMapping("/high-priority")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES')")
    public ResponseEntity<List<LeadResponseDto>> getHighPriorityLeads(
            @RequestParam(defaultValue = "70") int minScore,
            @RequestParam(defaultValue = "20") int limit) {
        
        List<LeadResponseDto> leads = buyerLeadService.getHighPriorityLeads(minScore, limit);
        return ResponseEntity.ok(leads);
    }

    // Get leads needing follow-up
    @GetMapping("/follow-up")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES')")
    public ResponseEntity<List<LeadResponseDto>> getLeadsNeedingFollowUp() {
        List<LeadResponseDto> leads = buyerLeadService.getLeadsNeedingFollowUp();
        return ResponseEntity.ok(leads);
    }

    // Get leads by sales rep
    @GetMapping("/sales-rep/{salesRep}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES')")
    public ResponseEntity<Page<LeadResponseDto>> getLeadsBySalesRep(
            @PathVariable String salesRep,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<LeadResponseDto> leads = buyerLeadService.getLeadsBySalesRep(salesRep, pageable);
        return ResponseEntity.ok(leads);
    }

    // Get lead statistics
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES')")
    public ResponseEntity<LeadStatsDto> getLeadStats(
            @RequestParam(required = false) String salesRep,
            @RequestParam(defaultValue = "30") int days) {
        
        LeadStatsDto stats = buyerLeadService.getLeadStats(salesRep, days);
        return ResponseEntity.ok(stats);
    }

    // Get lead conversion analytics
    @GetMapping("/analytics/conversion")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getConversionAnalytics(
            @RequestParam(defaultValue = "90") int days) {
        
        Map<String, Object> analytics = buyerLeadService.getConversionAnalytics(days);
        return ResponseEntity.ok(analytics);
    }

    // Get lead sources analytics
    @GetMapping("/analytics/sources")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getLeadSourcesAnalytics(
            @RequestParam(defaultValue = "30") int days) {
        
        Map<String, Object> analytics = buyerLeadService.getLeadSourcesAnalytics(days);
        return ResponseEntity.ok(analytics);
    }

    // Get lead conversion funnel data
    @GetMapping("/funnel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getLeadFunnel(
            @RequestParam(defaultValue = "30") int days) {
        
        Map<String, Object> funnelData = buyerLeadService.getLeadFunnel(days);
        return ResponseEntity.ok(funnelData);
    }

    // Get similar leads
    @GetMapping("/{id}/similar")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES')")
    public ResponseEntity<List<LeadResponseDto>> getSimilarLeads(
            @PathVariable Long id,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<LeadResponseDto> similarLeads = buyerLeadService.getSimilarLeads(id, limit);
        return ResponseEntity.ok(similarLeads);
    }

    // Export leads to CSV
    @GetMapping("/export")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> exportLeads(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo) {
        
        String csvData = buyerLeadService.exportLeadsToCSV(status, dateFrom, dateTo);
        return ResponseEntity.ok()
                .header("Content-Type", "text/csv")
                .header("Content-Disposition", "attachment; filename=buyer_leads.csv")
                .body(csvData);
    }

    // Get lead activity timeline
    @GetMapping("/{id}/timeline")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES')")
    public ResponseEntity<List<Map<String, Object>>> getLeadTimeline(@PathVariable Long id) {
        List<Map<String, Object>> timeline = buyerLeadService.getLeadTimeline(id);
        return ResponseEntity.ok(timeline);
    }

    // Update lead score manually
    @PutMapping("/{id}/score")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LeadResponseDto> updateLeadScore(
            @PathVariable Long id,
            @RequestParam Integer score,
            @RequestParam(required = false) String reason) {
        
        LeadResponseDto updatedLead = buyerLeadService.updateLeadScore(id, score, reason);
        return ResponseEntity.ok(updatedLead);
    }

    // Bulk update leads
    @PutMapping("/bulk-update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> bulkUpdateLeads(
            @RequestBody Map<String, Object> updateData) {
        
        buyerLeadService.bulkUpdateLeads(updateData);
        return ResponseEntity.ok("Bulk update completed successfully");
    }

    // Delete lead
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteLead(@PathVariable Long id) {
        buyerLeadService.deleteLead(id);
        return ResponseEntity.ok("Lead deleted successfully");
    }

    // Add interaction to lead
    @PostMapping("/{id}/interactions")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES')")
    public ResponseEntity<LeadResponseDto> addInteraction(
            @PathVariable Long id,
            @RequestBody Map<String, Object> interactionData) {
        
        LeadResponseDto updatedLead = buyerLeadService.addInteraction(id, interactionData);
        return ResponseEntity.ok(updatedLead);
    }

    // Get leads by category
    @GetMapping("/category/{category}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES')")
    public ResponseEntity<List<LeadResponseDto>> getLeadsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "20") int limit) {
        
        List<LeadResponseDto> leads = buyerLeadService.getLeadsByCategory(category, limit);
        return ResponseEntity.ok(leads);
    }

    // Get leads by price range
    @GetMapping("/price-range")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES')")
    public ResponseEntity<List<LeadResponseDto>> getLeadsByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice,
            @RequestParam(defaultValue = "20") int limit) {
        
        List<LeadResponseDto> leads = buyerLeadService.getLeadsByPriceRange(minPrice, maxPrice, limit);
        return ResponseEntity.ok(leads);
    }

    // Get stale leads (no activity for X days)
    @GetMapping("/stale")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES')")
    public ResponseEntity<List<LeadResponseDto>> getStaleLeads(
            @RequestParam(defaultValue = "7") int days,
            @RequestParam(defaultValue = "50") int limit) {
        
        List<LeadResponseDto> staleLeads = buyerLeadService.getStaleLeads(days, limit);
        return ResponseEntity.ok(staleLeads);
    }

    // Get dashboard metrics for sales team
    @GetMapping("/dashboard-metrics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES')")
    public ResponseEntity<Map<String, Object>> getDashboardMetrics() {
        Map<String, Object> metrics = buyerLeadService.getDashboardMetrics();
        return ResponseEntity.ok(metrics);
    }

    // Utility method to get client IP address
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            return xForwardedForHeader.split(",")[0].trim();
        }
    }
}

