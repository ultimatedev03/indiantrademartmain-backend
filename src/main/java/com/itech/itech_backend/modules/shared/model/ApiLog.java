package com.itech.itech_backend.modules.shared.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "api_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String endpoint;

    @Column(nullable = false)
    private String method;

    @Column(nullable = false)
    private int statusCode;

    private Long responseTime;

    private String userAgent;

    private String ipAddress;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}

