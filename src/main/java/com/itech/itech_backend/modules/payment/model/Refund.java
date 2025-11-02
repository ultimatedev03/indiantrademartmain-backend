package com.itech.itech_backend.modules.payment.model;

import com.itech.itech_backend.modules.vendor.model.Vendors;
import com.itech.itech_backend.modules.core.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "refunds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String refundId;

    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendors vendor;

    @Column(nullable = false)
    private BigDecimal refundAmount;

    @Column(nullable = false)
    private BigDecimal originalAmount;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RefundStatus status = RefundStatus.REQUESTED;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RefundType type = RefundType.FULL;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(columnDefinition = "TEXT")
    private String adminNotes;

    private String razorpayRefundId;
    
    @ManyToOne
    @JoinColumn(name = "processed_by")
    private User processedBy;

    @Builder.Default
    private LocalDateTime requestedAt = LocalDateTime.now();
    
    private LocalDateTime processedAt;
    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum RefundStatus {
        REQUESTED, APPROVED, REJECTED, PROCESSING, COMPLETED, FAILED
    }

    public enum RefundType {
        FULL, PARTIAL
    }
}

