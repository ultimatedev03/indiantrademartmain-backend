package com.itech.marketplace.entity;

import com.itech.itech_backend.enums.TicketPriority;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sla_configurations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SLAConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketPriority priority;

    @Column(nullable = false)
    private String category;

    // Response time in minutes
    @Column(nullable = false)
    private Integer responseTimeMinutes;

    // Resolution time in minutes
    @Column(nullable = false)
    private Integer resolutionTimeMinutes;

    // Escalation time in minutes
    @Column(nullable = false)
    private Integer escalationTimeMinutes;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

