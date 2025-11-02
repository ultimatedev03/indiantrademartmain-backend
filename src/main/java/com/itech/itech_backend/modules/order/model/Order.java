package com.itech.itech_backend.modules.order.model;

import com.itech.itech_backend.modules.buyer.model.Buyer;
import com.itech.itech_backend.modules.vendor.model.Vendor;
import com.itech.itech_backend.modules.company.model.Company;
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
@Table(name = "orders", indexes = {
    @Index(name = "idx_order_number", columnList = "order_number", unique = true),
    @Index(name = "idx_order_buyer", columnList = "buyer_id"),
    @Index(name = "idx_order_status", columnList = "order_status"),
    @Index(name = "idx_order_created", columnList = "created_at"),
    @Index(name = "idx_order_amount", columnList = "total_amount"),
    @Index(name = "idx_order_payment_status", columnList = "payment_status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===============================
    // ORDER IDENTIFICATION
    // ===============================
    
    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;
    
    @Column(name = "reference_number", length = 100)
    private String referenceNumber; // Customer reference
    
    @Column(name = "po_number", length = 100)
    private String poNumber; // Purchase Order Number

    // ===============================
    // PARTIES INVOLVED
    // ===============================
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private Buyer buyer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company; // Buying company
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_vendor_id")
    private Vendor primaryVendor; // Main vendor for multi-vendor orders

    // ===============================
    // ORDER STATUS
    // ===============================
    
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus = OrderStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "fulfillment_status")
    private FulfillmentStatus fulfillmentStatus = FulfillmentStatus.UNFULFILLED;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    // ===============================
    // FINANCIAL INFORMATION
    // ===============================
    
    @Column(name = "subtotal_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal subtotalAmount = BigDecimal.ZERO;
    
    @Column(name = "tax_amount", precision = 15, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;
    
    @Column(name = "shipping_amount", precision = 15, scale = 2)
    private BigDecimal shippingAmount = BigDecimal.ZERO;
    
    @Column(name = "discount_amount", precision = 15, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    @Column(name = "total_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;
    
    @Column(name = "currency", length = 3)
    private String currency = "INR";
    
    @Column(name = "exchange_rate", precision = 10, scale = 4)
    private BigDecimal exchangeRate = BigDecimal.ONE;

    // ===============================
    // SHIPPING INFORMATION
    // ===============================
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "addressLine1", column = @Column(name = "shipping_address_line1")),
        @AttributeOverride(name = "addressLine2", column = @Column(name = "shipping_address_line2")),
        @AttributeOverride(name = "city", column = @Column(name = "shipping_city")),
        @AttributeOverride(name = "state", column = @Column(name = "shipping_state")),
        @AttributeOverride(name = "postalCode", column = @Column(name = "shipping_postal_code")),
        @AttributeOverride(name = "country", column = @Column(name = "shipping_country")),
        @AttributeOverride(name = "contactName", column = @Column(name = "shipping_contact_name")),
        @AttributeOverride(name = "contactPhone", column = @Column(name = "shipping_contact_phone"))
    })
    private Address shippingAddress;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "addressLine1", column = @Column(name = "billing_address_line1")),
        @AttributeOverride(name = "addressLine2", column = @Column(name = "billing_address_line2")),
        @AttributeOverride(name = "city", column = @Column(name = "billing_city")),
        @AttributeOverride(name = "state", column = @Column(name = "billing_state")),
        @AttributeOverride(name = "postalCode", column = @Column(name = "billing_postal_code")),
        @AttributeOverride(name = "country", column = @Column(name = "billing_country")),
        @AttributeOverride(name = "contactName", column = @Column(name = "billing_contact_name")),
        @AttributeOverride(name = "contactPhone", column = @Column(name = "billing_contact_phone"))
    })
    private Address billingAddress;
    
    @Column(name = "shipping_method", length = 100)
    private String shippingMethod;
    
    @Column(name = "shipping_carrier", length = 100)
    private String shippingCarrier;
    
    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;
    
    @Column(name = "expected_delivery_date")
    private LocalDateTime expectedDeliveryDate;
    
    @Column(name = "actual_delivery_date")
    private LocalDateTime actualDeliveryDate;

    // ===============================
    // ORDER ITEMS
    // ===============================
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;
    
    @Column(name = "total_items")
    private Integer totalItems = 0;
    
    @Column(name = "total_quantity")
    private Integer totalQuantity = 0;

    // ===============================
    // DISCOUNTS AND PROMOTIONS
    // ===============================
    
    @Column(name = "coupon_code", length = 50)
    private String couponCode;
    
    @Column(name = "coupon_discount_amount", precision = 15, scale = 2)
    private BigDecimal couponDiscountAmount = BigDecimal.ZERO;
    
    @Column(name = "promotional_discount_amount", precision = 15, scale = 2)
    private BigDecimal promotionalDiscountAmount = BigDecimal.ZERO;
    
    @Column(name = "volume_discount_amount", precision = 15, scale = 2)
    private BigDecimal volumeDiscountAmount = BigDecimal.ZERO;

    // ===============================
    // PAYMENT INFORMATION
    // ===============================
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;
    
    @Column(name = "payment_reference", length = 200)
    private String paymentReference;
    
    @Column(name = "paid_amount", precision = 15, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;
    
    @Column(name = "outstanding_amount", precision = 15, scale = 2)
    private BigDecimal outstandingAmount = BigDecimal.ZERO;
    
    @Column(name = "payment_due_date")
    private LocalDateTime paymentDueDate;
    
    @Column(name = "payment_terms", length = 100)
    private String paymentTerms;

    // ===============================
    // TAX INFORMATION
    // ===============================
    
    @Column(name = "tax_type", length = 50)
    private String taxType; // GST, VAT, etc.
    
    @Column(name = "tax_rate", precision = 5, scale = 2)
    private BigDecimal taxRate;
    
    @Column(name = "tax_inclusive")
    private Boolean taxInclusive = false;
    
    @Column(name = "tax_number", length = 50)
    private String taxNumber; // GST Number, etc.

    // ===============================
    // ORDER WORKFLOW
    // ===============================
    
    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;
    
    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;
    
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
    
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    
    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;
    
    @Column(name = "approved_by", length = 100)
    private String approvedBy;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    // ===============================
    // SPECIAL INSTRUCTIONS AND NOTES
    // ===============================
    
    @Column(name = "special_instructions", length = 1000)
    private String specialInstructions;
    
    @Column(name = "internal_notes", length = 1000)
    private String internalNotes;
    
    @Column(name = "customer_notes", length = 1000)
    private String customerNotes;

    // ===============================
    // PRIORITY AND URGENCY
    // ===============================
    
    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private OrderPriority priority = OrderPriority.NORMAL;
    
    @Column(name = "is_urgent")
    private Boolean isUrgent = false;
    
    @Column(name = "required_by_date")
    private LocalDateTime requiredByDate;

    // ===============================
    // B2B SPECIFIC FIELDS
    // ===============================
    
    @Enumerated(EnumType.STRING)
    @Column(name = "order_type")
    private OrderType orderType = OrderType.STANDARD;
    
    @Column(name = "quote_valid_until")
    private LocalDateTime quoteValidUntil;
    
    @Column(name = "credit_approved")
    private Boolean creditApproved = false;
    
    @Column(name = "credit_limit_checked")
    private Boolean creditLimitChecked = false;

    // ===============================
    // COMMUNICATION HISTORY
    // ===============================
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderCommunication> communications;

    // ===============================
    // ANALYTICS AND METRICS
    // ===============================
    
    @Column(name = "processing_time_hours")
    private Integer processingTimeHours;
    
    @Column(name = "fulfillment_time_hours")
    private Integer fulfillmentTimeHours;
    
    @Column(name = "customer_rating")
    private Integer customerRating; // 1-5 stars
    
    @Column(name = "customer_feedback", length = 1000)
    private String customerFeedback;

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
    
    public enum OrderStatus {
        DRAFT,
        PENDING,
        CONFIRMED,
        PROCESSING,
        SHIPPED,
        PARTIALLY_SHIPPED,
        DELIVERED,
        PARTIALLY_DELIVERED,
        COMPLETED,
        CANCELLED,
        REFUNDED,
        RETURNED,
        FAILED
    }
    
    public enum FulfillmentStatus {
        UNFULFILLED,
        PARTIALLY_FULFILLED,
        FULFILLED,
        SHIPPED,
        DELIVERED,
        RETURNED,
        CANCELLED
    }
    
    public enum PaymentStatus {
        PENDING,
        AUTHORIZED,
        PAID,
        PARTIALLY_PAID,
        FAILED,
        CANCELLED,
        REFUNDED,
        PARTIALLY_REFUNDED,
        DISPUTED
    }
    
    public enum PaymentMethod {
        CASH,
        CARD,
        BANK_TRANSFER,
        CHECK,
        CREDIT,
        UPI,
        WALLET,
        NET_BANKING,
        COD
    }
    
    public enum OrderPriority {
        LOW,
        NORMAL,
        HIGH,
        URGENT,
        CRITICAL
    }
    
    public enum OrderType {
        STANDARD,
        QUOTE,
        RECURRING,
        BULK,
        SAMPLE,
        RETURN,
        EXCHANGE
    }

    // ===============================
    // EMBEDDED ADDRESS CLASS
    // ===============================
    
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String state;
        private String postalCode;
        private String country;
        private String contactName;
        private String contactPhone;
    }

    // ===============================
    // HELPER METHODS
    // ===============================
    
    public boolean isEditable() {
        return orderStatus == OrderStatus.DRAFT || orderStatus == OrderStatus.PENDING;
    }
    
    public boolean isCancellable() {
        return orderStatus != OrderStatus.COMPLETED && 
               orderStatus != OrderStatus.CANCELLED && 
               orderStatus != OrderStatus.REFUNDED;
    }
    
    public boolean isPaymentPending() {
        return paymentStatus == PaymentStatus.PENDING || 
               paymentStatus == PaymentStatus.PARTIALLY_PAID;
    }
    
    public boolean isFullyPaid() {
        return paymentStatus == PaymentStatus.PAID;
    }
    
    public boolean isDelivered() {
        return fulfillmentStatus == FulfillmentStatus.DELIVERED;
    }
    
    public BigDecimal getRemainingAmount() {
        return totalAmount.subtract(paidAmount);
    }
    
    public void calculateTotals() {
        if (orderItems != null && !orderItems.isEmpty()) {
            subtotalAmount = orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            totalItems = orderItems.size();
            totalQuantity = orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
        }
        
        totalAmount = subtotalAmount
            .add(taxAmount != null ? taxAmount : BigDecimal.ZERO)
            .add(shippingAmount != null ? shippingAmount : BigDecimal.ZERO)
            .subtract(discountAmount != null ? discountAmount : BigDecimal.ZERO);
        
        outstandingAmount = totalAmount.subtract(paidAmount != null ? paidAmount : BigDecimal.ZERO);
    }
    
    @PrePersist
    @PreUpdate
    protected void updateCalculations() {
        calculateTotals();
    }
}

