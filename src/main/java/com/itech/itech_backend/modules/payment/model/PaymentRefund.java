package com.itech.itech_backend.modules.payment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_refunds", indexes = {
    @Index(name = "idx_refund_reference", columnList = "refund_reference", unique = true),
    @Index(name = "idx_refund_payment", columnList = "original_payment_id"),
    @Index(name = "idx_refund_status", columnList = "refund_status"),
    @Index(name = "idx_refund_created", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRefund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_payment_id", nullable = false)
    private Payment originalPayment;

    @Column(name = "refund_reference", nullable = false, unique = true, length = 100)
    private String refundReference;

    @Column(name = "gateway_refund_id", length = 100)
    private String gatewayRefundId;

    @Column(name = "refund_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal refundAmount;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency = "INR";

    @Enumerated(EnumType.STRING)
    @Column(name = "refund_status", nullable = false)
    private RefundStatus refundStatus = RefundStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "refund_type")
    private RefundType refundType = RefundType.REFUND;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "initiated_at")
    private LocalDateTime initiatedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "failed_at")
    private LocalDateTime failedAt;

    @Column(name = "gateway_response_code", length = 20)
    private String gatewayResponseCode;

    @Column(name = "gateway_response_message", length = 500)
    private String gatewayResponseMessage;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    public enum RefundStatus {
        PENDING,
        PROCESSING,
        PROCESSED,
        FAILED,
        CANCELLED
    }

    public enum RefundType {
        REFUND,
        PARTIAL_REFUND,
        CHARGEBACK,
        REVERSAL
    }
}

