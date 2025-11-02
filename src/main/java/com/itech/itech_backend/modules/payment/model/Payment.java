package com.itech.itech_backend.modules.payment.model;

import com.itech.itech_backend.modules.buyer.model.Order;
import com.itech.itech_backend.modules.buyer.model.Buyer;
import com.itech.itech_backend.modules.company.model.Company;
import com.itech.itech_backend.modules.vendor.model.Vendors;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_payment_reference", columnList = "payment_reference", unique = true),
    @Index(name = "idx_payment_order", columnList = "order_id"),
    @Index(name = "idx_payment_buyer", columnList = "buyer_id"),
    @Index(name = "idx_payment_vendor", columnList = "vendor_id"),
    @Index(name = "idx_payment_status", columnList = "payment_status"),
    @Index(name = "idx_payment_method", columnList = "payment_method"),
    @Index(name = "idx_payment_created", columnList = "created_at"),
    @Index(name = "idx_payment_amount", columnList = "amount"),
    @Index(name = "idx_payment_gateway", columnList = "payment_gateway")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===============================
    // PAYMENT IDENTIFICATION
    // ===============================
    
    @Column(name = "payment_reference", nullable = false, unique = true, length = 100)
    private String paymentReference;
    
    @Column(name = "transaction_id", length = 100)
    private String transactionId;
    
    @Column(name = "gateway_payment_id", length = 100)
    private String gatewayPaymentId;
    
    @Column(name = "external_reference", length = 100)
    private String externalReference;

    // ===============================
    // ASSOCIATIONS
    // ===============================
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private Buyer buyer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    private Vendors vendor;

    // ===============================
    // PAYMENT DETAILS
    // ===============================
    
    @Column(name = "amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;
    
    @Column(name = "currency", length = 3, nullable = false)
    private String currency = "INR";
    
    @Column(name = "exchange_rate", precision = 10, scale = 4)
    private BigDecimal exchangeRate = BigDecimal.ONE;
    
    @Column(name = "amount_in_base_currency", precision = 15, scale = 2)
    private BigDecimal amountInBaseCurrency;

    // ===============================
    // PAYMENT METHOD AND GATEWAY
    // ===============================
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_gateway", nullable = false)
    private PaymentGateway paymentGateway;
    
    @Column(name = "payment_instrument", length = 100)
    private String paymentInstrument; // Card ending, UPI ID, etc.
    
    @Column(name = "issuer_bank", length = 100)
    private String issuerBank;

    // ===============================
    // PAYMENT STATUS
    // ===============================
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    private PaymentType paymentType = PaymentType.PAYMENT;

    // ===============================
    // FEES AND CHARGES
    // ===============================
    
    @Column(name = "gateway_fee", precision = 15, scale = 2)
    private BigDecimal gatewayFee = BigDecimal.ZERO;
    
    @Column(name = "processing_fee", precision = 15, scale = 2)
    private BigDecimal processingFee = BigDecimal.ZERO;
    
    @Column(name = "convenience_fee", precision = 15, scale = 2)
    private BigDecimal convenienceFee = BigDecimal.ZERO;
    
    @Column(name = "total_fees", precision = 15, scale = 2)
    private BigDecimal totalFees = BigDecimal.ZERO;
    
    @Column(name = "net_amount", precision = 15, scale = 2)
    private BigDecimal netAmount;

    // ===============================
    // GATEWAY RESPONSE
    // ===============================
    
    @Column(name = "gateway_response_code", length = 20)
    private String gatewayResponseCode;
    
    @Column(name = "gateway_response_message", length = 500)
    private String gatewayResponseMessage;
    
    @Column(name = "gateway_status", length = 50)
    private String gatewayStatus;
    
    @Column(name = "authorization_code", length = 20)
    private String authorizationCode;
    
    @Column(name = "rrn", length = 50) // Retrieval Reference Number
    private String rrn;

    // ===============================
    // DATES AND TIMELINE
    // ===============================
    
    @Column(name = "initiated_at")
    private LocalDateTime initiatedAt;
    
    @Column(name = "authorized_at")
    private LocalDateTime authorizedAt;
    
    @Column(name = "captured_at")
    private LocalDateTime capturedAt;
    
    @Column(name = "settled_at")
    private LocalDateTime settledAt;
    
    @Column(name = "failed_at")
    private LocalDateTime failedAt;
    
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    // ===============================
    // BILLING INFORMATION
    // ===============================
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "billing_name")),
        @AttributeOverride(name = "email", column = @Column(name = "billing_email")),
        @AttributeOverride(name = "phone", column = @Column(name = "billing_phone")),
        @AttributeOverride(name = "addressLine1", column = @Column(name = "billing_address_line1")),
        @AttributeOverride(name = "addressLine2", column = @Column(name = "billing_address_line2")),
        @AttributeOverride(name = "city", column = @Column(name = "billing_city")),
        @AttributeOverride(name = "state", column = @Column(name = "billing_state")),
        @AttributeOverride(name = "postalCode", column = @Column(name = "billing_postal_code")),
        @AttributeOverride(name = "country", column = @Column(name = "billing_country"))
    })
    private BillingInfo billingInfo;

    // ===============================
    // RISK AND FRAUD
    // ===============================
    
    @Column(name = "risk_score")
    private Integer riskScore;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level")
    private RiskLevel riskLevel = RiskLevel.LOW;
    
    @Column(name = "fraud_check_status", length = 50)
    private String fraudCheckStatus;
    
    @Column(name = "is_flagged")
    private Boolean isFlagged = false;

    // ===============================
    // REFUND INFORMATION
    // ===============================
    
    @Column(name = "refunded_amount", precision = 15, scale = 2)
    private BigDecimal refundedAmount = BigDecimal.ZERO;
    
    @Column(name = "refundable_amount", precision = 15, scale = 2)
    private BigDecimal refundableAmount;
    
    @Column(name = "is_refundable")
    private Boolean isRefundable = true;
    
    @OneToMany(mappedBy = "originalPayment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PaymentRefund> refunds;

    // ===============================
    // PAYMENT LINKS AND RETRIES
    // ===============================
    
    @Column(name = "payment_link", length = 500)
    private String paymentLink;
    
    @Column(name = "retry_count")
    private Integer retryCount = 0;
    
    @Column(name = "max_retries")
    private Integer maxRetries = 3;
    
    @Column(name = "last_retry_at")
    private LocalDateTime lastRetryAt;

    // ===============================
    // WEBHOOK AND NOTIFICATIONS
    // ===============================
    
    @Column(name = "webhook_received")
    private Boolean webhookReceived = false;
    
    @Column(name = "webhook_verified")
    private Boolean webhookVerified = false;
    
    @Column(name = "notification_sent")
    private Boolean notificationSent = false;

    // ===============================
    // METADATA AND CUSTOM FIELDS
    // ===============================
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "custom_field1", length = 100)
    private String customField1;
    
    @Column(name = "custom_field2", length = 100)
    private String customField2;
    
    @Column(name = "internal_notes", length = 1000)
    private String internalNotes;

    // ===============================
    // SETTLEMENT INFORMATION
    // ===============================
    
    @Column(name = "settlement_batch_id", length = 100)
    private String settlementBatchId;
    
    @Column(name = "settlement_reference", length = 100)
    private String settlementReference;
    
    @Column(name = "settlement_date")
    private LocalDateTime settlementDate;
    
    @Column(name = "settlement_amount", precision = 15, scale = 2)
    private BigDecimal settlementAmount;

    // ===============================
    // AUDIT AND TIMESTAMPS
    // ===============================
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by", length = 100)
    private String createdBy;
    
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    // ===============================
    // ENUMS
    // ===============================
    
    public enum PaymentStatus {
        PENDING,
        INITIATED,
        AUTHORIZED,
        CAPTURED,
        SETTLED,
        FAILED,
        CANCELLED,
        EXPIRED,
        PARTIALLY_REFUNDED,
        FULLY_REFUNDED,
        DISPUTED,
        CHARGEBACK
    }
    
    public enum PaymentMethod {
        CARD,
        NET_BANKING,
        UPI,
        WALLET,
        BANK_TRANSFER,
        EMI,
        COD,
        CHEQUE,
        DEMAND_DRAFT,
        NEFT,
        RTGS,
        IMPS
    }
    
    public enum PaymentGateway {
        RAZORPAY,
        PAYU,
        PAYTM,
        CASHFREE,
        INSTAMOJO,
        PHONEPE,
        GPAY,
        AMAZON_PAY,
        PAYPAL,
        STRIPE
    }
    
    public enum PaymentType {
        PAYMENT,
        REFUND,
        PARTIAL_REFUND,
        ADJUSTMENT,
        REVERSAL
    }
    
    public enum RiskLevel {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }

    // ===============================
    // EMBEDDED BILLING INFO
    // ===============================
    
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillingInfo {
        private String name;
        private String email;
        private String phone;
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String state;
        private String postalCode;
        private String country;
    }

    // ===============================
    // HELPER METHODS
    // ===============================
    
    public boolean isPending() {
        return paymentStatus == PaymentStatus.PENDING || paymentStatus == PaymentStatus.INITIATED;
    }
    
    public boolean isSuccess() {
        return paymentStatus == PaymentStatus.CAPTURED || paymentStatus == PaymentStatus.SETTLED;
    }
    
    public boolean isFailed() {
        return paymentStatus == PaymentStatus.FAILED || 
               paymentStatus == PaymentStatus.CANCELLED || 
               paymentStatus == PaymentStatus.EXPIRED;
    }
    
    public boolean isRefunded() {
        return paymentStatus == PaymentStatus.PARTIALLY_REFUNDED || 
               paymentStatus == PaymentStatus.FULLY_REFUNDED;
    }
    
    public boolean canBeRefunded() {
        return isRefundable && isSuccess() && 
               refundableAmount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    public BigDecimal getRemainingRefundableAmount() {
        if (refundableAmount == null) {
            return amount.subtract(refundedAmount != null ? refundedAmount : BigDecimal.ZERO);
        }
        return refundableAmount.subtract(refundedAmount != null ? refundedAmount : BigDecimal.ZERO);
    }
    
    public void calculateNetAmount() {
        this.netAmount = amount.subtract(totalFees != null ? totalFees : BigDecimal.ZERO);
    }
    
    public void calculateTotalFees() {
        BigDecimal gateway = gatewayFee != null ? gatewayFee : BigDecimal.ZERO;
        BigDecimal processing = processingFee != null ? processingFee : BigDecimal.ZERO;
        BigDecimal convenience = convenienceFee != null ? convenienceFee : BigDecimal.ZERO;
        
        this.totalFees = gateway.add(processing).add(convenience);
    }
    
    @PrePersist
    @PreUpdate
    protected void updateCalculations() {
        calculateTotalFees();
        calculateNetAmount();
        
        if (refundableAmount == null && amount != null) {
            refundableAmount = amount;
        }
        
        if (amountInBaseCurrency == null && amount != null && exchangeRate != null) {
            amountInBaseCurrency = amount.multiply(exchangeRate);
        }
    }
}

