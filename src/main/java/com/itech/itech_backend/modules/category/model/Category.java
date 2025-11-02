package com.itech.itech_backend.modules.category.model;

import com.itech.itech_backend.modules.core.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "categories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Column(name = "display_order")
    private Integer displayOrder;
    
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
    
    // Parent-child category relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;
    
    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Category> subCategories;
    
    // Employee who added this category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_employee_id")
    private User createdByEmployee;
    
    // Category icon/image
    private String iconUrl;
    
    // SEO fields
    @Column(name = "slug", unique = true)
    private String slug;
    
    @Column(name = "meta_title")
    private String metaTitle;
    
    @Column(name = "meta_description", length = 500)
    private String metaDescription;
    
    // Category level (0 = root category)
    @Column(name = "category_level")
    @Builder.Default
    private Integer categoryLevel = 0;
    
    // Commission percentage for this category
    @Column(name = "commission_percentage")
    @Builder.Default
    private Double commissionPercentage = 0.0;
    
    // Visibility flags
    @Column(name = "visible_to_vendors")
    @Builder.Default
    private Boolean visibleToVendors = true;
    
    @Column(name = "visible_to_customers")
    @Builder.Default
    private Boolean visibleToCustomers = true;
    
    // Timestamps
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        
        if (slug == null || slug.isEmpty()) {
            slug = generateSlugFromName(name);
        }
        
        if (displayOrder == null) {
            displayOrder = 0;
        }
        
        // Calculate category level based on parent
        if (parentCategory != null) {
            categoryLevel = parentCategory.getCategoryLevel() + 1;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    private String generateSlugFromName(String name) {
        return name.toLowerCase()
                  .replaceAll("[^a-z0-9\\s]", "")
                  .replaceAll("\\s+", "-")
                  .trim();
    }
    
    // Utility method to check if category has subcategories
    public boolean hasSubCategories() {
        return subCategories != null && !subCategories.isEmpty();
    }
    
    // Get full category path (e.g., "Electronics > Smartphones > Android")
    public String getFullCategoryPath() {
        StringBuilder path = new StringBuilder();
        Category current = this;
        
        while (current != null) {
            if (path.length() > 0) {
                path.insert(0, " > ");
            }
            path.insert(0, current.getName());
            current = current.getParentCategory();
        }
        
        return path.toString();
    }
    
    // Get root category
    public Category getRootCategory() {
        Category current = this;
        while (current.getParentCategory() != null) {
            current = current.getParentCategory();
        }
        return current;
    }
}
