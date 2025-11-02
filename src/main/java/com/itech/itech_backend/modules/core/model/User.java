package com.itech.itech_backend.modules.core.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_email", columnList = "email"),
    @Index(name = "idx_users_phone", columnList = "phone"),
    @Index(name = "idx_users_role", columnList = "role")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(unique = true, nullable = false, length = 255)
    private String email;

    @Column(unique = true, length = 20)
    private String phone;
    
    @Column(nullable = false, length = 255)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserRole role = UserRole.BUYER;
    
    @Column(name = "is_verified", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private Boolean isVerified = false;
    
    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    @Builder.Default
    private Boolean isActive = true;
    
    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;
    
    @Column(length = 500)
    private String address;
    
    @Column(length = 100)
    private String city;
    
    @Column(length = 100)
    private String state;
    
    @Column(length = 10)
    private String pincode;
    
    @Column(length = 100)
    @Builder.Default
    private String country = "India";

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
    public enum UserRole {
        BUYER, SELLER, ADMIN, SUPPORT, CTO, DATA_ENTRY
    }
    
    // Helper methods
    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }
    
    public boolean isSeller() {
        return role == UserRole.SELLER;
    }
    
    public boolean isBuyer() {
        return role == UserRole.BUYER;
    }
    
    public boolean isSupport() {
        return role == UserRole.SUPPORT;
    }
    
    public boolean isCTO() {
        return role == UserRole.CTO;
    }
    
    public boolean isDataEntry() {
        return role == UserRole.DATA_ENTRY;
    }
    
    // Backward compatibility methods
    public boolean isVerified() {
        return this.isVerified != null && this.isVerified;
    }
    
    public void setVerified(boolean verified) {
        this.isVerified = verified;
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
    }
    
    public boolean getActive() {
        return this.isActive != null && this.isActive;
    }
    
    // Role utility methods for backward compatibility
    public String getRoleAsString() {
        return this.role != null ? this.role.name() : null;
    }
    
    public void setRole(String roleString) {
        if (roleString != null) {
            try {
                this.role = UserRole.valueOf(roleString.toUpperCase());
            } catch (IllegalArgumentException e) {
                this.role = UserRole.BUYER; // default fallback
            }
        }
    }
    
    public UserRole getRoleEnum() {
        return this.role;
    }
    
    public void setRoleEnum(UserRole role) {
        this.role = role;
    }
    
    // Additional helper methods for role string operations
    public String getRoleFormatted() {
        if (this.role == null) return "";
        return this.role.name().replace("_", " ").toLowerCase();
    }
    
    public boolean hasRole(String roleName) {
        return this.role != null && this.role.name().equalsIgnoreCase(roleName);
    }
    
    public boolean isRoleEmpty() {
        return this.role == null;
    }
    
    public String getRoleWithReplace(String oldStr, String newStr) {
        return this.role != null ? this.role.name().replace(oldStr, newStr) : "";
    }
    
    // Note: Custom builder methods are handled by Lombok's @Builder annotation
}

