package com.itech.itech_backend.modules.vendor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "verification_documents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String vendorId;

    @Column(nullable = false)
    private String documentType;

    @Column
    private String documentNumber;

    @Column(nullable = false)
    private String documentUrl;

    @Column
    private LocalDateTime expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(length = 1000)
    private String rejectionReason;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    @Column
    private LocalDateTime verifiedAt;

    public enum Status {
        PENDING,
        VERIFIED,
        REJECTED,
        EXPIRED,
        FAILED
    }
}
