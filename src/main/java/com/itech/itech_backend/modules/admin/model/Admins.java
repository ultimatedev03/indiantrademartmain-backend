package com.itech.itech_backend.modules.admin.model;

import com.itech.itech_backend.modules.core.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "admins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admins {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Link to unified User model (migration support)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true)
    private String phone;
    
    @Column(nullable = false)
    private String password;

    @Builder.Default
    private boolean verified = false;
    
    public boolean isVerified() {
        return verified;
    }
    
    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    @Builder.Default
    private String role = "ROLE_ADMIN";

    private String department;
    private String designation;
    
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

