package com.itech.itech_backend.modules.admin.model;

import com.itech.itech_backend.modules.core.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "site_configurations", indexes = {
    @Index(name = "idx_site_config_key", columnList = "config_key"),
    @Index(name = "idx_site_config_updated_by", columnList = "updated_by")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "config_key", nullable = false, unique = true, length = 255)
    private String configKey;

    @Column(name = "config_value", columnDefinition = "TEXT")
    private String configValue;

    @Column(name = "config_type", length = 50)
    @Builder.Default
    private String configType = "STRING";

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    @Builder.Default
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Enums
    public enum ConfigType {
        STRING, INTEGER, BOOLEAN, JSON, URL, EMAIL, COLOR, FILE_PATH
    }

    // Helper methods
    public boolean getBooleanValue() {
        if (configValue == null) return false;
        return Boolean.parseBoolean(configValue);
    }

    public Integer getIntegerValue() {
        if (configValue == null) return null;
        try {
            return Integer.parseInt(configValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Double getDoubleValue() {
        if (configValue == null) return null;
        try {
            return Double.parseDouble(configValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public boolean isValidUrl() {
        return configValue != null && configValue.matches("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$");
    }

    public boolean isValidEmail() {
        return configValue != null && configValue.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}
