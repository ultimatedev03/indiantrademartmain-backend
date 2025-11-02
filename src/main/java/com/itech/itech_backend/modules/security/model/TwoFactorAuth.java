package com.itech.itech_backend.modules.security.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "two_factor_auth")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TwoFactorAuth {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;
    
    @Column(name = "secret_key", nullable = false)
    private String secretKey;
    
    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = false;
    
    @Column(name = "backup_codes", columnDefinition = "TEXT")
    private String backupCodes;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
