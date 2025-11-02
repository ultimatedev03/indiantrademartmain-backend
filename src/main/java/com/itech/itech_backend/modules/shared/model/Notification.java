package com.itech.itech_backend.modules.shared.model;

import com.itech.itech_backend.modules.shared.model.enums.NotificationType;
import com.itech.itech_backend.modules.core.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private NotificationType type = NotificationType.INFO;

    @Builder.Default
    private boolean isRead = false;

    private String actionUrl;

    private String relatedEntityId; // ID of related entity (order, quote, etc.)

    private String metadata; // JSON string for additional data

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime readAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}

