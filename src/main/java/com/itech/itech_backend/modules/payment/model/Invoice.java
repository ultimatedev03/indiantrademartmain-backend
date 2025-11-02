package com.itech.itech_backend.modules.payment.model;

import com.itech.itech_backend.modules.vendor.model.Vendors;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String invoiceNumber;

    @ManyToOne
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendors vendor;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    @Column(nullable = false)
    private BigDecimal subtotal;

    @Column(nullable = false)
    private BigDecimal cgstAmount;

    @Column(nullable = false)
    private BigDecimal sgstAmount;

    @Column(nullable = false)
    private BigDecimal igstAmount;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private BigDecimal cgstRate; // e.g., 9%

    @Column(nullable = false)
    private BigDecimal sgstRate; // e.g., 9%

    @Column(nullable = false)
    private BigDecimal igstRate; // e.g., 18%

    private String vendorGstNumber;
    private String companyGstNumber;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private InvoiceStatus status = InvoiceStatus.GENERATED;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private InvoiceType type = InvoiceType.SUBSCRIPTION;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String billingAddress;
    private String shippingAddress;

    private LocalDateTime dueDate;
    private LocalDateTime paidAt;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum InvoiceStatus {
        GENERATED, SENT, PAID, OVERDUE, CANCELLED
    }

    public enum InvoiceType {
        SUBSCRIPTION, ORDER, SERVICE, REFUND
    }
}

