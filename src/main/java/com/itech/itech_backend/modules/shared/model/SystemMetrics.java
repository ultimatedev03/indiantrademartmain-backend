package com.itech.itech_backend.modules.shared.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_metrics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String metricName;

    @Column(nullable = false)
    private String metricValue;

    @Column(nullable = false)
    private String metricType;

    @Column(length = 500)
    private String description;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}

