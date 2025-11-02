package com.itech.itech_backend.modules.rfq.model;

import com.itech.itech_backend.modules.vendor.model.VendorProfile;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rfq_bids")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RFQBid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfq_id", nullable = false)
    private RFQ rfq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private VendorProfile vendor;

    @Column(name = "price_quote", nullable = false)
    private BigDecimal priceQuote;

    private Integer quantity;

    @Column(name = "delivery_time_days")
    private Integer deliveryTimeDays;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ElementCollection
    @CollectionTable(name = "rfq_bid_attachments")
    @Builder.Default
    private List<String> attachments = new ArrayList<>();

    @Column(name = "sample_provided")
    private boolean sampleProvided;

    @Column(name = "certification_provided")
    private boolean certificationProvided;

    @Column(name = "certification_details")
    private String certificationDetails;

    @Column(name = "warranty_period_months")
    private Integer warrantyPeriodMonths;

    @Column(name = "minimum_order_quantity")
    private Integer minimumOrderQuantity;

    @Column(name = "payment_terms")
    private String paymentTerms;

    @Column(name = "validity_period_days")
    private Integer validityPeriodDays;

    @Enumerated(EnumType.STRING)
    private BidStatus status;

    @Column(name = "shipping_terms")
    private String shippingTerms;

    @Column(name = "shipping_cost")
    private BigDecimal shippingCost;

    @Column(name = "tax_percentage")
    private BigDecimal taxPercentage;

    @Column(name = "tax_amount")
    private BigDecimal taxAmount;

    @Column(name = "discount_percentage")
    private BigDecimal discountPercentage;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount;

    @ElementCollection
    @CollectionTable(name = "rfq_bid_specifications")
    @Column(name = "specification", columnDefinition = "TEXT")
    @Builder.Default
    private List<String> specifications = new ArrayList<>();

    @Column(name = "buyer_notes", columnDefinition = "TEXT")
    private String buyerNotes;

    @Column(name = "negotiation_round")
    private Integer negotiationRound;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum BidStatus {
        PENDING,
        ACCEPTED,
        REJECTED
    }

    @PrePersist
    @PreUpdate
    private void calculateTotals() {
        if (priceQuote != null && quantity != null) {
            // Calculate base price
            BigDecimal basePrice = priceQuote.multiply(BigDecimal.valueOf(quantity));

            // Add shipping cost if present
            if (shippingCost != null) {
                basePrice = basePrice.add(shippingCost);
            }

            // Calculate and apply tax if percentage is present
            if (taxPercentage != null) {
                taxAmount = basePrice.multiply(taxPercentage.divide(BigDecimal.valueOf(100)));
                basePrice = basePrice.add(taxAmount);
            }

            // Calculate and apply discount if percentage is present
            if (discountPercentage != null) {
                discountAmount = basePrice.multiply(discountPercentage.divide(BigDecimal.valueOf(100)));
                basePrice = basePrice.subtract(discountAmount);
            }

            // Store the calculated total price (you may want to add this field back if needed)
            // totalPrice = basePrice;
        }
    }
}
