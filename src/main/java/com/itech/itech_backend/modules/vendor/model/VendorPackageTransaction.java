package com.itech.itech_backend.modules.vendor.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendor_package_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorPackageTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String transactionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendors vendor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_package_id", nullable = false)
    private VendorPackage vendorPackage;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal discountAmount;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal taxAmount;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.PENDING;
    
    private String paymentGatewayTransactionId;
    
    private String paymentGatewayResponse;
    
    private String couponCode;
    
    private String billingAddress;
    
    private String billingCity;
    
    private String billingState;
    
    private String billingPincode;
    
    private String gstNumber;
    
    private String invoiceNumber;
    
    private String receiptUrl;
    
    // For installment payments
    private Integer installmentCount;
    
    private BigDecimal installmentAmount;
    
    private Integer currentInstallment;
    
    private String notes;
    
    @Builder.Default
    private boolean generateInvoice = true;
    
    private LocalDateTime paymentDate;
    
    private LocalDateTime expiryDate;
    
    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt;
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public enum PaymentMethod {
        RAZORPAY, UPI, NET_BANKING, DEBIT_CARD, CREDIT_CARD, BANK_TRANSFER, WALLET, EMI
    }
    
    public enum TransactionStatus {
        PENDING, SUCCESS, FAILED, CANCELLED, REFUNDED, PARTIALLY_REFUNDED
    }
}
