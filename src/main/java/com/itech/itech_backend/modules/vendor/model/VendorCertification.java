package com.itech.itech_backend.modules.vendor.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "vendor_certifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorCertification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private VendorProfile vendor;

    @Column(name = "certification_name", nullable = false, length = 150)
    private String certificationName;

    @Column(name = "certification_number", length = 100)
    private String certificationNumber;

    @Column(name = "issuing_authority", length = 200)
    private String issuingAuthority;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "certificate_url", length = 500)
    private String certificateUrl;

    @Column(name = "is_verified")
    @Builder.Default
    private Boolean isVerified = false;

    @Column(name = "verification_date")
    private LocalDate verificationDate;
}
