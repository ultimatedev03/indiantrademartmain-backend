package com.itech.itech_backend.modules.order.model;

import com.itech.itech_backend.modules.product.model.Product;
import com.itech.itech_backend.modules.vendor.model.Vendor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_items", indexes = {
    @Index(name = "idx_order_item_order", columnList = "order_id"),
    @Index(name = "idx_order_item_product", columnList = "product_id"),
    @Index(name = "idx_order_item_vendor", columnList = "vendor_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===============================
    // ASSOCIATIONS
    // ===============================
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    // ===============================
    // PRODUCT INFORMATION (SNAPSHOT)
    // ===============================
    
    @Column(name = "product_sku", length = 100)
    private String productSku;
    
    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;
    
    @Column(name = "product_description", length = 1000)
    private String productDescription;
    
    @Column(name = "product_brand", length = 100)
    private String productBrand;
    
    @Column(name = "product_category", length = 100)
    private String productCategory;

    // ===============================
    // VARIANT INFORMATION
    // ===============================
    
    @Column(name = "variant_type", length = 50)
    private String variantType; // COLOR, SIZE, etc.
    
    @Column(name = "variant_value", length = 100)
    private String variantValue; // Red, Large, etc.
    
    @Column(name = "variant_attributes", length = 500)
    private String variantAttributes; // JSON string of attributes

    // ===============================
    // QUANTITY AND PRICING
    // ===============================
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Column(name = "unit_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal unitPrice;
    
    @Column(name = "original_unit_price", precision = 15, scale = 2)
    private BigDecimal originalUnitPrice; // Before any discounts
    
    @Column(name = "total_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalPrice;
    
    @Column(name = "currency", length = 3)
    private String currency = "INR";

    // ===============================
    // DISCOUNTS
    // ===============================
    
    @Column(name = "item_discount_amount", precision = 15, scale = 2)
    private BigDecimal itemDiscountAmount = BigDecimal.ZERO;
    
    @Column(name = "item_discount_percentage", precision = 5, scale = 2)
    private BigDecimal itemDiscountPercentage = BigDecimal.ZERO;
    
    @Column(name = "volume_discount_amount", precision = 15, scale = 2)
    private BigDecimal volumeDiscountAmount = BigDecimal.ZERO;
    
    @Column(name = "promotional_discount_amount", precision = 15, scale = 2)
    private BigDecimal promotionalDiscountAmount = BigDecimal.ZERO;

    // ===============================
    // TAX INFORMATION
    // ===============================
    
    @Column(name = "tax_amount", precision = 15, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;
    
    @Column(name = "tax_rate", precision = 5, scale = 2)
    private BigDecimal taxRate = BigDecimal.ZERO;
    
    @Column(name = "tax_type", length = 50)
    private String taxType; // GST, VAT, etc.
    
    @Column(name = "hsn_code", length = 20)
    private String hsnCode;

    // ===============================
    // FULFILLMENT STATUS
    // ===============================
    
    @Enumerated(EnumType.STRING)
    @Column(name = "fulfillment_status")
    private ItemFulfillmentStatus fulfillmentStatus = ItemFulfillmentStatus.PENDING;
    
    @Column(name = "quantity_shipped")
    private Integer quantityShipped = 0;
    
    @Column(name = "quantity_delivered")
    private Integer quantityDelivered = 0;
    
    @Column(name = "quantity_cancelled")
    private Integer quantityCancelled = 0;
    
    @Column(name = "quantity_returned")
    private Integer quantityReturned = 0;

    // ===============================
    // SHIPPING INFORMATION
    // ===============================
    
    @Column(name = "weight", precision = 10, scale = 3)
    private BigDecimal weight;
    
    @Column(name = "dimensions", length = 100)
    private String dimensions;
    
    @Column(name = "requires_shipping")
    private Boolean requiresShipping = true;
    
    @Column(name = "shipping_class", length = 50)
    private String shippingClass;

    // ===============================
    // DATES AND TIMELINE
    // ===============================
    
    @Column(name = "expected_ship_date")
    private LocalDateTime expectedShipDate;
    
    @Column(name = "actual_ship_date")
    private LocalDateTime actualShipDate;
    
    @Column(name = "expected_delivery_date")
    private LocalDateTime expectedDeliveryDate;
    
    @Column(name = "actual_delivery_date")
    private LocalDateTime actualDeliveryDate;

    // ===============================
    // SPECIAL INSTRUCTIONS
    // ===============================
    
    @Column(name = "special_instructions", length = 500)
    private String specialInstructions;
    
    @Column(name = "gift_message", length = 500)
    private String giftMessage;
    
    @Column(name = "is_gift")
    private Boolean isGift = false;

    // ===============================
    // RETURN AND REFUND
    // ===============================
    
    @Column(name = "is_returnable")
    private Boolean isReturnable = true;
    
    @Column(name = "return_window_days")
    private Integer returnWindowDays;
    
    @Column(name = "refunded_amount", precision = 15, scale = 2)
    private BigDecimal refundedAmount = BigDecimal.ZERO;
    
    @Column(name = "return_reason", length = 500)
    private String returnReason;

    // ===============================
    // EXTERNAL REFERENCES
    // ===============================
    
    @Column(name = "vendor_item_id", length = 100)
    private String vendorItemId;
    
    @Column(name = "vendor_sku", length = 100)
    private String vendorSku;
    
    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    // ===============================
    // AUDIT FIELDS
    // ===============================
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ===============================
    // ENUMS
    // ===============================
    
    public enum ItemFulfillmentStatus {
        PENDING,
        CONFIRMED,
        PROCESSING,
        PACKED,
        SHIPPED,
        PARTIALLY_SHIPPED,
        DELIVERED,
        PARTIALLY_DELIVERED,
        CANCELLED,
        RETURNED,
        REFUNDED
    }

    // ===============================
    // HELPER METHODS
    // ===============================
    
    public void calculateTotalPrice() {
        if (unitPrice != null && quantity != null) {
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
            BigDecimal totalDiscounts = BigDecimal.ZERO
                .add(itemDiscountAmount != null ? itemDiscountAmount : BigDecimal.ZERO)
                .add(volumeDiscountAmount != null ? volumeDiscountAmount : BigDecimal.ZERO)
                .add(promotionalDiscountAmount != null ? promotionalDiscountAmount : BigDecimal.ZERO);
            
            this.totalPrice = subtotal.subtract(totalDiscounts);
        }
    }
    
    public BigDecimal getEffectiveUnitPrice() {
        if (totalPrice != null && quantity != null && quantity > 0) {
            return totalPrice.divide(BigDecimal.valueOf(quantity), 2, RoundingMode.HALF_UP);
        }
        return unitPrice;
    }
    
    public BigDecimal getTotalDiscountAmount() {
        return BigDecimal.ZERO
            .add(itemDiscountAmount != null ? itemDiscountAmount : BigDecimal.ZERO)
            .add(volumeDiscountAmount != null ? volumeDiscountAmount : BigDecimal.ZERO)
            .add(promotionalDiscountAmount != null ? promotionalDiscountAmount : BigDecimal.ZERO);
    }
    
    public Integer getQuantityPending() {
        return quantity - quantityShipped - quantityCancelled;
    }
    
    public Integer getQuantityOutstanding() {
        return quantityShipped - quantityDelivered - quantityReturned;
    }
    
    public boolean isFullyShipped() {
        return quantityShipped >= quantity;
    }
    
    public boolean isFullyDelivered() {
        return quantityDelivered >= quantity;
    }
    
    public boolean isPartiallyFulfilled() {
        return quantityShipped > 0 && quantityShipped < quantity;
    }
    
    @PrePersist
    @PreUpdate
    protected void updateCalculations() {
        calculateTotalPrice();
    }
}

