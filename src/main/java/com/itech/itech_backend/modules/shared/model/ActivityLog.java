package com.itech.itech_backend.modules.shared.model;

import com.itech.itech_backend.modules.admin.model.Admins;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private Admins admin;

    @Column(nullable = false)
    private String action;

    @Column(length = 1000)
    private String description;

    private String entityType; // USER, VENDOR, PRODUCT, etc.
    
    private Long entityId;
    
    private String ipAddress;
    
    private String userAgent;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}

