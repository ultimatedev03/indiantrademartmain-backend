package com.itech.itech_backend.modules.buyer.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "BuyerSubCategory")
@Table(name = "sub_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Builder.Default
    private int displayOrder = 0;
    
    @Builder.Default
    private boolean isActive = true;
    
    private String metaTitle;
    
    private String metaDescription;
    
    private String slug;

    @CreationTimestamp
    @Column(name = "created_at", nullable = true, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "subCategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnore
    private List<MicroCategory> microCategories = new ArrayList<>();
}

