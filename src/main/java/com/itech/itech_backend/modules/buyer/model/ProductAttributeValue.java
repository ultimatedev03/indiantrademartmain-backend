package com.itech.itech_backend.modules.buyer.model;

import com.itech.itech_backend.modules.buyer.model.ProductAttribute;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_attribute_values", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "attribute_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAttributeValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "attribute_id", nullable = false)
    private ProductAttribute attribute;

    @Column(columnDefinition = "TEXT")
    private String value; // The actual value (can be JSON for multi-select)

    // For numeric values
    private Double numericValue;

    // For boolean values
    private Boolean booleanValue;

    // For date values
    private LocalDateTime dateValue;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Helper methods
    public String getDisplayValue() {
        if (attribute == null) return value;

        switch (attribute.getType()) {
            case BOOLEAN:
                return booleanValue != null ? (booleanValue ? "Yes" : "No") : "No";
            case NUMBER:
            case DECIMAL:
                return numericValue != null ? numericValue.toString() : "0";
            case DATE:
                return dateValue != null ? dateValue.toString() : "";
            default:
                return value != null ? value : "";
        }
    }

    public boolean matchesFilter(String filterValue) {
        if (filterValue == null || filterValue.isEmpty()) return true;

        switch (attribute.getType()) {
            case BOOLEAN:
                return booleanValue != null && booleanValue.toString().equalsIgnoreCase(filterValue);
            case NUMBER:
            case DECIMAL:
                if (numericValue == null) return false;
                try {
                    double filter = Double.parseDouble(filterValue);
                    return numericValue.equals(filter);
                } catch (NumberFormatException e) {
                    return false;
                }
            case SELECT:
                return value != null && value.equalsIgnoreCase(filterValue);
            case MULTI_SELECT:
                return value != null && value.toLowerCase().contains(filterValue.toLowerCase());
            default:
                return value != null && value.toLowerCase().contains(filterValue.toLowerCase());
        }
    }

    public boolean matchesRangeFilter(String minValue, String maxValue) {
        if (!attribute.isNumericType() || numericValue == null) return false;

        try {
            boolean matchesMin = minValue == null || numericValue >= Double.parseDouble(minValue);
            boolean matchesMax = maxValue == null || numericValue <= Double.parseDouble(maxValue);
            return matchesMin && matchesMax;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

