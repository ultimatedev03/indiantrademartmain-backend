package com.itech.itech_backend.modules.shared.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "seo_keywords")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeoKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String keyword;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String targetPage; // HOMEPAGE, CATEGORY, PRODUCT

    private Long targetId; // Category ID or Product ID

    @Builder.Default
    private boolean isActive = true;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

