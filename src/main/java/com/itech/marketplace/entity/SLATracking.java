package com.itech.marketplace.entity;

import com.itech.itech_backend.modules.support.model.SupportTicket;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sla_tracking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SLATracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private SupportTicket ticket;

    @ManyToOne
    @JoinColumn(name = "sla_config_id", nullable = false)
    private SLAConfiguration slaConfiguration;

    // SLA deadlines
    private LocalDateTime responseDeadline;
    private LocalDateTime resolutionDeadline;
    private LocalDateTime escalationDeadline;

    // Actual times
    private LocalDateTime actualResponseTime;
    private LocalDateTime actualResolutionTime;

    // SLA breach flags
    @Builder.Default
    private Boolean responseBreached = false;
    @Builder.Default
    private Boolean resolutionBreached = false;
    @Builder.Default
    private Boolean escalated = false;

    // Time calculations in minutes
    private Long responseTimeMinutes;
    private Long resolutionTimeMinutes;

    // SLA compliance percentages
    private Double responseComplianceScore;
    private Double resolutionComplianceScore;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Helper methods for SLA calculations
    public void calculateResponseCompliance() {
        if (actualResponseTime != null && responseDeadline != null) {
            long actualMinutes = java.time.Duration.between(ticket.getCreatedAt(), actualResponseTime).toMinutes();
            long allowedMinutes = slaConfiguration.getResponseTimeMinutes();
            this.responseTimeMinutes = actualMinutes;
            this.responseComplianceScore = Math.min(100.0, (double) allowedMinutes / actualMinutes * 100);
            this.responseBreached = actualMinutes > allowedMinutes;
        }
    }

    public void calculateResolutionCompliance() {
        if (actualResolutionTime != null && resolutionDeadline != null) {
            long actualMinutes = java.time.Duration.between(ticket.getCreatedAt(), actualResolutionTime).toMinutes();
            long allowedMinutes = slaConfiguration.getResolutionTimeMinutes();
            this.resolutionTimeMinutes = actualMinutes;
            this.resolutionComplianceScore = Math.min(100.0, (double) allowedMinutes / actualMinutes * 100);
            this.resolutionBreached = actualMinutes > allowedMinutes;
        }
    }
}

