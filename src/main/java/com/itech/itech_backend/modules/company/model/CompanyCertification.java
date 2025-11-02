package com.itech.itech_backend.modules.company.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "company_certifications", indexes = {
    @Index(name = "idx_company_cert", columnList = "company_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyCertification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "certification_name", length = 200)
    private String certificationName;

    @Column(name = "certification_url", length = 500)
    private String certificationUrl;

    @Column(name = "issued_by", length = 150)
    private String issuedBy;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    // Helper methods
    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }

    public boolean isValidCertification() {
        return certificationName != null && !certificationName.isEmpty() && 
               !isExpired();
    }
}
