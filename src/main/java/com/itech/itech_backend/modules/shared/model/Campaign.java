package com.itech.itech_backend.modules.shared.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "campaigns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private String type; // EMAIL, SMS, BANNER, SOCIAL

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Builder.Default
    private String status = "DRAFT"; // DRAFT, ACTIVE, PAUSED, COMPLETED

    @Builder.Default
    private int clickCount = 0;

    @Builder.Default
    private int viewCount = 0;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

