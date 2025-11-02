package com.itech.itech_backend.modules.vendor.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vendor_payment_methods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorPaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private VendorProfile vendor;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType;

    @Column(name = "payment_details", length = 200)
    private String paymentDetails;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "processing_fee_percentage", precision = 5, scale = 2)
    private java.math.BigDecimal processingFeePercentage;

    public enum PaymentType {
        CASH_ON_DELIVERY,
        BANK_TRANSFER,
        UPI,
        CREDIT_CARD,
        DEBIT_CARD,
        NET_BANKING,
        WALLET,
        CHEQUE,
        DEMAND_DRAFT,
        LETTER_OF_CREDIT
    }
}
