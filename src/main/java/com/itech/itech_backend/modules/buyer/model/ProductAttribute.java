package com.itech.itech_backend.modules.buyer.model;

import com.itech.itech_backend.enums.AttributeType;
import com.itech.itech_backend.modules.buyer.model.Category;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "product_attributes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g., "Color", "Size", "Material"

    @Column(nullable = false)
    private String displayName; // User-friendly name

    private String description;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AttributeType type = AttributeType.TEXT; // TEXT, NUMBER, BOOLEAN, SELECT, MULTI_SELECT

    // For SELECT and MULTI_SELECT types
    @Column(columnDefinition = "TEXT")
    private String possibleValues; // JSON array of possible values

    // Display properties
    @Builder.Default
    private boolean isFilterable = true; // Can be used for filtering

    @Builder.Default
    private boolean isSearchable = false; // Can be used in search

    @Builder.Default
    private boolean isRequired = false; // Required for products

    @Builder.Default
    private boolean isVariantAttribute = false; // Used for creating variants

    @Builder.Default
    private int displayOrder = 0; // Order in forms and filters

    @Builder.Default
    private boolean isActive = true;

    // Category association
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category; // If null, applies to all categories

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Helper methods
    public String[] getPossibleValuesArray() {
        if (possibleValues == null || possibleValues.isEmpty()) {
            return new String[0];
        }
        
        // Simple parsing for now, in production use proper JSON parsing
        return possibleValues.replace("[", "").replace("]", "")
                .replace("\"", "").split(",");
    }

    public boolean isSelectType() {
        return type == AttributeType.SELECT || type == AttributeType.MULTI_SELECT;
    }

    public boolean isNumericType() {
        return type == AttributeType.NUMBER || type == AttributeType.DECIMAL;
    }
}

