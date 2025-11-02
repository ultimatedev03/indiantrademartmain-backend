package com.itech.itech_backend.modules.buyer.model;

import com.itech.itech_backend.modules.buyer.model.Product;
import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.vendor.model.Vendors;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "BuyerOrder")
@Table(name = "buyer_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String orderNumber;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // Additional fields for compatibility
    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer; // Same as user, for backward compatibility
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product; // Main product for simple orders
    
    private Integer quantity; // Quantity for simple orders
    
    private LocalDateTime deliveryDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false)
    private double totalAmount;

    @Column(nullable = false)
    private double shippingAmount;

    @Column(nullable = false)
    private double taxAmount;

    @Column(nullable = false)
    private double grandTotal;

    // Shipping Address
    private String shippingName;
    private String shippingAddress;
    private String shippingCity;
    private String shippingState;
    private String shippingPincode;
    private String shippingPhone;

    // Payment Details
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private String paymentMethod;
    private String paymentTransactionId;
    private String razorpayOrderId;
    private String razorpayPaymentId;

    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;
    
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum OrderStatus {
        PENDING,
        CONFIRMED,
        PROCESSING,
        SHIPPED,
        OUT_FOR_DELIVERY,
        DELIVERED,
        CANCELLED,
        RETURNED
    }

    public enum PaymentStatus {
        PENDING,
        PAID,
        FAILED,
        REFUNDED
    }
}

