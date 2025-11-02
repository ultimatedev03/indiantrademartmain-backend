package com.itech.itech_backend.modules.vendor.model;

import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.enums.VendorType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "legacy_vendors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vendors {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
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
    private String role = "ROLE_VENDOR";

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private VendorType vendorType = VendorType.BASIC;

    private String businessName;
    private String businessAddress;
    private String city;
    private String state;
    private String pincode;
    private String gstNumber;
    private String panNumber;
    
    // KYC Status
    @Builder.Default
    private boolean kycSubmitted = false;
    
    @Builder.Default
    private boolean kycApproved = false;
    
    private LocalDateTime kycSubmittedAt;
    
    private LocalDateTime kycApprovedAt;
    
    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;
    
    private String rejectionReason;
    
    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

