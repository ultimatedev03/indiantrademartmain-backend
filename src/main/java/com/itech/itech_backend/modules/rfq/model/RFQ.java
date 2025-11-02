package com.itech.itech_backend.modules.rfq.model;

import com.itech.itech_backend.modules.core.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rfqs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RFQ {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_type")
    private String unitType;

    @Column(name = "target_price")
    private BigDecimal targetPrice;

    @Column(name = "delivery_location")
    private String deliveryLocation;

    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;

    @Column(nullable = false)
    private String category;

    @ElementCollection
    @CollectionTable(name = "rfq_specifications")
    @Builder.Default
    private List<String> specifications = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "rfq_attachments")
    @Builder.Default
    private List<String> attachments = new ArrayList<>();

    @Column(name = "sample_required")
    private boolean sampleRequired;

    @Column(name = "certification_required")
    private boolean certificationRequired;

    @Column(name = "certification_type")
    private String certificationType;

    @Enumerated(EnumType.STRING)
    private PaymentTerms paymentTerms;

    @Column(name = "payment_terms_details")
    private String paymentTermsDetails;

    @Enumerated(EnumType.STRING)
    private RFQStatus status;

    @Column(name = "valid_until")
    private LocalDateTime validUntil;

    @Column(name = "min_supplier_rating")
    private Double minSupplierRating;

    @Column(name = "supplier_location_preference")
    private String supplierLocationPreference;

    @Column(name = "bid_count")
    private Integer bidCount;

    @Column(name = "view_count")
    private Integer viewCount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum RFQStatus {
        OPEN,
        CLOSED,
        FULFILLED
    }

    public enum PaymentTerms {
        ADVANCE,
        NET_30,
        NET_60,
        NET_90,
        LETTER_OF_CREDIT,
        CUSTOM
    }
}
