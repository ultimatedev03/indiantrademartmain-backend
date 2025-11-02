package com.itech.itech_backend.modules.buyer.model;

import com.itech.itech_backend.modules.buyer.model.Product;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String variantName; // e.g., "Red - Large", "Blue - Medium"

    // Variant attributes
    private String color;
    private String size;
    private String material;
    private String style;
    private String weight;
    private String dimensions;

    // Custom attributes as JSON
    @Column(columnDefinition = "TEXT")
    private String customAttributes; // JSON string for additional attributes

    // Pricing and inventory specific to this variant
    @Column(nullable = false)
    private Double price;

    private Double originalPrice; // For discounts

    @Column(nullable = false)
    private Integer stock;

    private String sku; // Unique SKU for this variant

    // Variant images
    @Column(length = 1000)
    private String imageUrls; // Comma-separated URLs

    // Variant specifications
    @Column(length = 2000)
    private String specifications;

    // Status
    @Builder.Default
    private boolean isActive = true;

    @Builder.Default
    private boolean isDefault = false; // Default variant to show

    // Statistics
    @Builder.Default
    private int orderCount = 0;

    @Builder.Default
    private int viewCount = 0;

    // Timestamps
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Helper methods
    public boolean hasDiscount() {
        return originalPrice != null && originalPrice > price;
    }

    public double getDiscountPercentage() {
        if (!hasDiscount()) return 0;
        return ((originalPrice - price) / originalPrice) * 100;
    }

    public boolean isInStock() {
        return stock > 0;
    }

    public boolean canOrder(int quantity) {
        return isActive && stock >= quantity;
    }

    public String getDisplayName() {
        StringBuilder displayName = new StringBuilder();
        
        if (color != null && !color.isEmpty()) {
            displayName.append(color);
        }
        
        if (size != null && !size.isEmpty()) {
            if (displayName.length() > 0) displayName.append(" - ");
            displayName.append(size);
        }
        
        if (material != null && !material.isEmpty()) {
            if (displayName.length() > 0) displayName.append(" - ");
            displayName.append(material);
        }
        
        return displayName.length() > 0 ? displayName.toString() : variantName;
    }
}

