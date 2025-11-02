package com.itech.itech_backend.modules.support.model;

import com.itech.itech_backend.enums.TicketStatus;
import com.itech.itech_backend.enums.TicketPriority;
import com.itech.itech_backend.modules.core.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "support_tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String ticketNumber;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String subject;

    @Column(length = 2000)
    private String description;
    
    private String category; // Support category like 'Technical', 'Billing', etc.

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TicketStatus status = TicketStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TicketPriority priority = TicketPriority.MEDIUM;

    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    private LocalDateTime assignedAt;

    @Column(length = 2000)
    private String response;

    @ManyToOne
    @JoinColumn(name = "responded_by")
    private User respondedBy;

    private LocalDateTime respondedAt;

    private LocalDateTime resolvedAt;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

