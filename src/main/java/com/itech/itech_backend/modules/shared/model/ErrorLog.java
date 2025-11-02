package com.itech.itech_backend.modules.shared.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "error_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String errorType;

    @Column(length = 2000)
    private String errorMessage;

    @Column(length = 5000)
    private String stackTrace;

    private String endpoint;
    
    private String httpMethod;
    
    private Integer httpStatus;
    
    private String userId;
    
    private String ipAddress;
    
    private String userAgent;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}

