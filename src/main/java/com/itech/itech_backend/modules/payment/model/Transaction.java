package com.itech.itech_backend.modules.payment.model;

import com.itech.itech_backend.modules.vendor.model.Vendors;
import com.itech.itech_backend.modules.core.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String transactionId;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendors vendor;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "refund_id")
    private Refund refund;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TransactionType type = TransactionType.PAYMENT;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String razorpayTransactionId;
    private String bankReferenceNumber;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum TransactionType {
        PAYMENT, REFUND, COMMISSION, PAYOUT, ADJUSTMENT
    }

    public enum TransactionStatus {
        PENDING, COMPLETED, FAILED, CANCELLED
    }
}

