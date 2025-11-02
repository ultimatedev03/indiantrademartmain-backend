package com.itech.itech_backend.modules.rfq.model;

import com.itech.itech_backend.modules.vendor.model.VendorProfile;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "quotations", indexes = {
    @Index(name = "idx_quotation_inquiry", columnList = "inquiry_id"),
    @Index(name = "idx_quotation_vendor", columnList = "vendor_id"),
    @Index(name = "idx_quotation_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quotation_number", unique = true, nullable = false, length = 50)
    private String quotationNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id")
    private Inquiry inquiry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    private VendorProfile vendor;

    @Column(name = "quoted_price", precision = 15, scale = 2)
    private BigDecimal quotedPrice;

    @Column(name = "quantity_available")
    private Integer quantityAvailable;

    @Column(name = "delivery_time_days")
    private Integer deliveryTimeDays;

    @Column(name = "validity_days")
    @Builder.Default
    private Integer validityDays = 30;

    @Column(name = "terms_conditions", columnDefinition = "TEXT")
    private String termsConditions;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private QuotationStatus status = QuotationStatus.DRAFT;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    // Enums
    public enum QuotationStatus {
        DRAFT, SENT, ACCEPTED, REJECTED, EXPIRED, CANCELLED
    }

    // Helper methods
    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }

    public boolean isValid() {
        return status == QuotationStatus.SENT && !isExpired();
    }

    public boolean canBeAccepted() {
        return status == QuotationStatus.SENT && !isExpired();
    }

    @PrePersist
    public void generateQuotationNumber() {
        if (this.quotationNumber == null) {
            this.quotationNumber = "QUO-" + System.currentTimeMillis();
        }
        if (this.expiresAt == null && this.validityDays != null) {
            this.expiresAt = this.createdAt.plusDays(this.validityDays);
        }
    }

    public BigDecimal getTotalAmount() {
        if (quotedPrice != null && quantityAvailable != null) {
            return quotedPrice.multiply(BigDecimal.valueOf(quantityAvailable));
        }
        return BigDecimal.ZERO;
    }
}
