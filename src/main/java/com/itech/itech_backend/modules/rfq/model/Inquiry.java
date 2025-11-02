package com.itech.itech_backend.modules.rfq.model;

import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.vendor.model.VendorProfile;
import com.itech.itech_backend.modules.product.model.Product;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "RfqInquiry")
@Table(name = "inquiries", indexes = {
    @Index(name = "idx_inquiry_buyer", columnList = "buyer_id"),
    @Index(name = "idx_inquiry_vendor", columnList = "vendor_id"),
    @Index(name = "idx_inquiry_status", columnList = "status"),
    @Index(name = "idx_inquiry_date", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "inquiry_number", unique = true, nullable = false, length = 50)
    private String inquiryNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    private VendorProfile vendor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(length = 300)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "quantity_required")
    private Integer quantityRequired;

    @Column(name = "expected_price", precision = 15, scale = 2)
    private BigDecimal expectedPrice;

    @Column(name = "delivery_location", length = 300)
    private String deliveryLocation;

    @Column(name = "expected_delivery_date")
    private LocalDate expectedDeliveryDate;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private InquiryStatus status = InquiryStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Priority priority = Priority.MEDIUM;

    @Column(name = "is_bulk_inquiry")
    @Builder.Default
    private Boolean isBulkInquiry = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // One-to-Many relationship with Quotations
    @OneToMany(mappedBy = "inquiry", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Quotation> quotations;

    // Enums
    public enum InquiryStatus {
        PENDING, RESPONDED, CLOSED
    }

    public enum Priority {
        LOW, MEDIUM, HIGH, URGENT
    }

    // Helper methods
    public boolean isActive() {
        return status == InquiryStatus.PENDING || status == InquiryStatus.RESPONDED;
    }

    public boolean hasQuotations() {
        return quotations != null && !quotations.isEmpty();
    }

    public int getQuotationCount() {
        return quotations != null ? quotations.size() : 0;
    }

    @PrePersist
    public void generateInquiryNumber() {
        if (this.inquiryNumber == null) {
            this.inquiryNumber = "INQ-" + System.currentTimeMillis();
        }
    }
}
