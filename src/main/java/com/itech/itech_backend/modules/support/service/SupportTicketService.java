package com.itech.itech_backend.modules.support.service;

import com.itech.itech_backend.enums.TicketPriority;
import com.itech.itech_backend.enums.TicketStatus;
import com.itech.itech_backend.modules.support.model.SupportTicket;
import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.support.repository.SupportTicketRepository;
import com.itech.itech_backend.modules.core.repository.UserRepository;
import com.itech.itech_backend.modules.shared.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Transactional
    public SupportTicket createSupportTicket(Long userId, String subject, String description, 
                                              String category, TicketPriority priority) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

            String ticketNumber = generateTicketNumber();
            
            SupportTicket supportTicket = SupportTicket.builder()
                .ticketNumber(ticketNumber)
                .user(user)
                .subject(subject)
                .description(description)
                .category(category)
                .priority(priority)
                .status(TicketStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .build();
                
            SupportTicket savedTicket = supportTicketRepository.save(supportTicket);
            
            // Send confirmation email to user
            sendTicketCreatedEmail(savedTicket);
            
            log.info("Support ticket created: {}", ticketNumber);
            return savedTicket;
            
        } catch (Exception e) {
            log.error("Error creating support ticket", e);
            throw new RuntimeException("Failed to create support ticket: " + e.getMessage());
        }
    }

    public SupportTicket createSupportTicket(SupportTicket supportTicket) {
        return supportTicketRepository.save(supportTicket);
    }

    public List<SupportTicket> getAllSupportTickets() {
        return supportTicketRepository.findAll();
    }
    
    public Page<SupportTicket> getAllSupportTickets(Pageable pageable) {
        return supportTicketRepository.findAllByOrderByCreatedAtDesc(pageable);
    }
    
    public Page<SupportTicket> getUserTickets(Long userId, Pageable pageable) {
        return supportTicketRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
    
    public Page<SupportTicket> getTicketsByStatus(TicketStatus status, Pageable pageable) {
        return supportTicketRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
    }
    
    public Page<SupportTicket> getTicketsByPriority(TicketPriority priority, Pageable pageable) {
        return supportTicketRepository.findByPriorityOrderByCreatedAtDesc(priority, pageable);
    }
    
    public SupportTicket getTicketById(Long ticketId) {
        return supportTicketRepository.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Support ticket not found"));
    }
    
    public SupportTicket getTicketByNumber(String ticketNumber) {
        return supportTicketRepository.findByTicketNumber(ticketNumber)
            .orElseThrow(() -> new RuntimeException("Support ticket not found"));
    }
    
    @Transactional
    public SupportTicket updateTicketStatus(Long ticketId, TicketStatus status, Long assignedToId) {
        try {
            SupportTicket ticket = getTicketById(ticketId);
            
            TicketStatus oldStatus = ticket.getStatus();
            ticket.setStatus(status);
            ticket.setUpdatedAt(LocalDateTime.now());
            
            if (assignedToId != null) {
                User assignedTo = userRepository.findById(assignedToId)
                    .orElseThrow(() -> new RuntimeException("Assigned user not found"));
                ticket.setAssignedTo(assignedTo);
            }
            
            if (status == TicketStatus.RESOLVED || status == TicketStatus.CLOSED) {
                ticket.setResolvedAt(LocalDateTime.now());
            }
            
            SupportTicket updatedTicket = supportTicketRepository.save(ticket);
            
            // Send status update email to user
            sendTicketStatusUpdateEmail(updatedTicket, oldStatus);
            
            log.info("Ticket {} status updated from {} to {}", ticket.getTicketNumber(), oldStatus, status);
            return updatedTicket;
            
        } catch (Exception e) {
            log.error("Error updating ticket status", e);
            throw new RuntimeException("Failed to update ticket status: " + e.getMessage());
        }
    }
    
    @Transactional
    public SupportTicket addTicketResponse(Long ticketId, String response, Long responderId) {
        try {
            SupportTicket ticket = getTicketById(ticketId);
            User responder = userRepository.findById(responderId)
                .orElseThrow(() -> new RuntimeException("Responder not found"));
            
            // Add response to ticket (assuming there's a response field or separate entity)
            ticket.setResponse(response);
            ticket.setRespondedBy(responder);
            ticket.setRespondedAt(LocalDateTime.now());
            ticket.setUpdatedAt(LocalDateTime.now());
            
            if (ticket.getStatus() == TicketStatus.OPEN) {
                ticket.setStatus(TicketStatus.IN_PROGRESS);
            }
            
            SupportTicket updatedTicket = supportTicketRepository.save(ticket);
            
            // Send response email to user
            sendTicketResponseEmail(updatedTicket);
            
            log.info("Response added to ticket {}", ticket.getTicketNumber());
            return updatedTicket;
            
        } catch (Exception e) {
            log.error("Error adding ticket response", e);
            throw new RuntimeException("Failed to add ticket response: " + e.getMessage());
        }
    }
    
    public Map<String, Object> getTicketStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalTickets = supportTicketRepository.count();
        long openTickets = supportTicketRepository.countByStatus(TicketStatus.OPEN);
        long inProgressTickets = supportTicketRepository.countByStatus(TicketStatus.IN_PROGRESS);
        long resolvedTickets = supportTicketRepository.countByStatus(TicketStatus.RESOLVED);
        long closedTickets = supportTicketRepository.countByStatus(TicketStatus.CLOSED);
        
        stats.put("totalTickets", totalTickets);
        stats.put("openTickets", openTickets);
        stats.put("inProgressTickets", inProgressTickets);
        stats.put("resolvedTickets", resolvedTickets);
        stats.put("closedTickets", closedTickets);
        
        // Calculate resolution rate
        double resolutionRate = totalTickets > 0 ? (double) (resolvedTickets + closedTickets) / totalTickets * 100 : 0;
        stats.put("resolutionRate", Math.round(resolutionRate * 100.0) / 100.0);
        
        // Priority distribution
        stats.put("highPriorityTickets", supportTicketRepository.countByPriority(TicketPriority.HIGH));
        stats.put("mediumPriorityTickets", supportTicketRepository.countByPriority(TicketPriority.MEDIUM));
        stats.put("lowPriorityTickets", supportTicketRepository.countByPriority(TicketPriority.LOW));
        
        return stats;
    }
    
    public List<SupportTicket> getUnassignedTickets() {
        return supportTicketRepository.findByAssignedToIsNullAndStatusIn(
            List.of(TicketStatus.OPEN, TicketStatus.IN_PROGRESS)
        );
    }
    
    public List<SupportTicket> getTicketsAssignedTo(Long userId) {
        return supportTicketRepository.findByAssignedToIdAndStatusIn(
            userId, List.of(TicketStatus.OPEN, TicketStatus.IN_PROGRESS)
        );
    }
    
    @Transactional
    public SupportTicket assignTicket(Long ticketId, Long assigneeId) {
        try {
            SupportTicket ticket = getTicketById(ticketId);
            User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new RuntimeException("Assignee not found"));
            
            ticket.setAssignedTo(assignee);
            ticket.setAssignedAt(LocalDateTime.now());
            ticket.setUpdatedAt(LocalDateTime.now());
            
            if (ticket.getStatus() == TicketStatus.OPEN) {
                ticket.setStatus(TicketStatus.IN_PROGRESS);
            }
            
            SupportTicket updatedTicket = supportTicketRepository.save(ticket);
            
            log.info("Ticket {} assigned to user {}", ticket.getTicketNumber(), assignee.getName());
            return updatedTicket;
            
        } catch (Exception e) {
            log.error("Error assigning ticket", e);
            throw new RuntimeException("Failed to assign ticket: " + e.getMessage());
        }
    }
    
    private String generateTicketNumber() {
        return "TKT" + System.currentTimeMillis();
    }
    
    private void sendTicketCreatedEmail(SupportTicket ticket) {
        try {
            String subject = "Support Ticket Created - " + ticket.getTicketNumber();
            String body = String.format(
                "Dear %s,\n\n" +
                "Your support ticket has been created successfully.\n\n" +
                "Ticket Number: %s\n" +
                "Subject: %s\n" +
                "Priority: %s\n" +
                "Status: %s\n\n" +
                "We will respond to your ticket as soon as possible.\n\n" +
                "Best regards,\n" +
                "Support Team",
                ticket.getUser().getName(),
                ticket.getTicketNumber(),
                ticket.getSubject(),
                ticket.getPriority(),
                ticket.getStatus()
            );
            
            emailService.sendEmail(ticket.getUser().getEmail(), subject, body);
        } catch (Exception e) {
            log.error("Failed to send ticket created email", e);
        }
    }
    
    private void sendTicketStatusUpdateEmail(SupportTicket ticket, TicketStatus oldStatus) {
        try {
            String subject = "Ticket Status Update - " + ticket.getTicketNumber();
            String body = String.format(
                "Dear %s,\n\n" +
                "Your support ticket status has been updated.\n\n" +
                "Ticket Number: %s\n" +
                "Subject: %s\n" +
                "Previous Status: %s\n" +
                "Current Status: %s\n\n" +
                "Best regards,\n" +
                "Support Team",
                ticket.getUser().getName(),
                ticket.getTicketNumber(),
                ticket.getSubject(),
                oldStatus,
                ticket.getStatus()
            );
            
            emailService.sendEmail(ticket.getUser().getEmail(), subject, body);
        } catch (Exception e) {
            log.error("Failed to send ticket status update email", e);
        }
    }
    
    private void sendTicketResponseEmail(SupportTicket ticket) {
        try {
            String subject = "New Response on Your Ticket - " + ticket.getTicketNumber();
            String body = String.format(
                "Dear %s,\n\n" +
                "We have responded to your support ticket.\n\n" +
                "Ticket Number: %s\n" +
                "Subject: %s\n" +
                "Response: %s\n\n" +
                "Please login to your account to view the full response.\n\n" +
                "Best regards,\n" +
                "Support Team",
                ticket.getUser().getName(),
                ticket.getTicketNumber(),
                ticket.getSubject(),
                ticket.getResponse()
            );
            
            emailService.sendEmail(ticket.getUser().getEmail(), subject, body);
        } catch (Exception e) {
            log.error("Failed to send ticket response email", e);
        }
    }

    // Additional methods for frontend API integration
    public List<SupportTicket> getFilteredSupportTickets(String status, String priority, String search) {
        log.info("🔍 Filtering tickets - Status: {}, Priority: {}, Search: {}", status, priority, search);
        
        List<SupportTicket> tickets = supportTicketRepository.findAll();
        
        // Apply search filter
        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase();
            tickets = tickets.stream()
                .filter(ticket -> 
                    ticket.getSubject().toLowerCase().contains(searchLower) ||
                    ticket.getDescription().toLowerCase().contains(searchLower) ||
                    ticket.getUser().getName().toLowerCase().contains(searchLower) ||
                    ticket.getUser().getEmail().toLowerCase().contains(searchLower) ||
                    ticket.getTicketNumber().toLowerCase().contains(searchLower)
                )
                .toList();
        }
        
        // Apply status filter
        if (status != null && !"ALL".equals(status) && !status.trim().isEmpty()) {
            TicketStatus ticketStatus = TicketStatus.valueOf(status);
            tickets = tickets.stream()
                .filter(ticket -> ticket.getStatus() == ticketStatus)
                .toList();
        }
        
        // Apply priority filter
        if (priority != null && !"ALL".equals(priority) && !priority.trim().isEmpty()) {
            TicketPriority ticketPriority = TicketPriority.valueOf(priority);
            tickets = tickets.stream()
                .filter(ticket -> ticket.getPriority() == ticketPriority)
                .toList();
        }
        
        log.info("📊 Filtered to {} tickets", tickets.size());
        return tickets;
    }

    public SupportTicket getSupportTicketById(Long id) {
        return getTicketById(id);
    }

    public SupportTicket updateSupportTicket(Long id, Map<String, Object> updates) {
        log.info("🔄 Updating ticket {} with: {}", id, updates);
        
        SupportTicket ticket = getTicketById(id);
        
        // Update fields based on the updates map
        if (updates.containsKey("status")) {
            String statusStr = (String) updates.get("status");
            TicketStatus status = TicketStatus.valueOf(statusStr);
            ticket.setStatus(status);
        }
        
        if (updates.containsKey("priority")) {
            String priorityStr = (String) updates.get("priority");
            TicketPriority priority = TicketPriority.valueOf(priorityStr);
            ticket.setPriority(priority);
        }
        
        if (updates.containsKey("assignedTo")) {
            String assignedToName = (String) updates.get("assignedTo");
            // For now, we'll store the name as a string
            // In a real implementation, you'd find the User by name/email
            ticket.setAssignedAt(LocalDateTime.now());
        }
        
        if (updates.containsKey("response")) {
            ticket.setResponse((String) updates.get("response"));
            ticket.setRespondedAt(LocalDateTime.now());
        }
        
        if (updates.containsKey("category")) {
            ticket.setCategory((String) updates.get("category"));
        }
        
        // Handle status-specific updates
        TicketStatus status = ticket.getStatus();
        if (status == TicketStatus.RESOLVED || status == TicketStatus.CLOSED) {
            ticket.setResolvedAt(LocalDateTime.now());
        }
        
        ticket.setUpdatedAt(LocalDateTime.now());
        
        SupportTicket updated = supportTicketRepository.save(ticket);
        log.info("✅ Support ticket {} updated successfully", id);
        return updated;
    }

    public SupportTicket updateTicketStatus(Long id, String newStatus, String assignedTo) {
        log.info("🔄 Updating ticket {} status to: {}, assigned to: {}", id, newStatus, assignedTo);
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", newStatus);
        
        if (assignedTo != null && !assignedTo.trim().isEmpty()) {
            updates.put("assignedTo", assignedTo);
        }
        
        return updateSupportTicket(id, updates);
    }

    public Map<String, Object> getTicketStats() {
        return getTicketStatistics();
    }
}

