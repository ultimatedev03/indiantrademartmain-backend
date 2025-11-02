package com.itech.itech_backend.modules.core.model;

import com.itech.itech_backend.enums.KycDocumentType;
import com.itech.itech_backend.enums.KycStatus;
import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.vendor.model.Vendors;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "kyc_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendors vendor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KycDocumentType documentType;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String filePath;

    private String originalFileName;

    private Long fileSize;

    private String mimeType;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private KycStatus status = KycStatus.PENDING;

    private String rejectionReason;

    @ManyToOne
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    private LocalDateTime reviewedAt;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

