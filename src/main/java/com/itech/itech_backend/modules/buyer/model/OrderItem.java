package com.itech.itech_backend.modules.buyer.model;

import com.itech.itech_backend.modules.buyer.model.Order;
import com.itech.itech_backend.modules.buyer.model.Product;
import com.itech.itech_backend.modules.vendor.model.Vendors;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "BuyerOrderItem")
@Table(name = "buyer_order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendors vendor;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private double price; // Price at the time of purchase

    @Column(nullable = false)
    private double totalPrice; // quantity * price

    // Product details at time of purchase (for record keeping)
    private String productName;
    private String productDescription;

    // Helper method
    public double calculateTotalPrice() {
        return quantity * price;
    }
}

