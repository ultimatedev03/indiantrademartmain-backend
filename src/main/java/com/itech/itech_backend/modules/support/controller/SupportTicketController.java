package com.itech.itech_backend.modules.support.controller;

import com.itech.itech_backend.modules.support.model.SupportTicket;
import com.itech.itech_backend.modules.support.service.SupportTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/support-tickets")
@RequiredArgsConstructor
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    @PostMapping
    public ResponseEntity<SupportTicket> createSupportTicket(@RequestBody SupportTicket supportTicket) {
        System.out.println("🎫 Creating support ticket: " + supportTicket.getSubject());
        SupportTicket createdTicket = supportTicketService.createSupportTicket(supportTicket);
        return ResponseEntity.ok(createdTicket);
    }

    @GetMapping
    public ResponseEntity<List<SupportTicket>> getSupportTickets(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String search) {
        System.out.println("🔍 Fetching support tickets with filters - Status: " + status + ", Priority: " + priority);
        List<SupportTicket> tickets = supportTicketService.getFilteredSupportTickets(status, priority, search);
        System.out.println("📋 Found " + tickets.size() + " support tickets");
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupportTicket> getSupportTicket(@PathVariable Long id) {
        SupportTicket ticket = supportTicketService.getSupportTicketById(id);
        return ResponseEntity.ok(ticket);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SupportTicket> updateSupportTicket(
            @PathVariable Long id, 
            @RequestBody Map<String, Object> updates) {
        System.out.println("🔄 Updating ticket " + id + " with: " + updates);
        SupportTicket updatedTicket = supportTicketService.updateSupportTicket(id, updates);
        return ResponseEntity.ok(updatedTicket);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<SupportTicket> updateTicketStatus(
            @PathVariable Long id, 
            @RequestBody Map<String, String> statusUpdate) {
        String newStatus = statusUpdate.get("status");
        String assignedTo = statusUpdate.get("assignedTo");
        SupportTicket updatedTicket = supportTicketService.updateTicketStatus(id, newStatus, assignedTo);
        return ResponseEntity.ok(updatedTicket);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getTicketStats() {
        Map<String, Object> stats = supportTicketService.getTicketStats();
        return ResponseEntity.ok(stats);
    }
}

