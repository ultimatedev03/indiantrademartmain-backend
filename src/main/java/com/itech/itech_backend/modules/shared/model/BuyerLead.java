package com.itech.itech_backend.modules.shared.model;

import com.itech.itech_backend.modules.core.model.User;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "buyer_leads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyerLead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    private String phone;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "company")
    private String company;
    
    @Column(name = "message", length = 2000)
    private String message;

    @Column(name = "search_query", length = 1000)
    private String searchQuery;

    @Column(name = "viewed_products", length = 2000)
    private String viewedProducts; // Comma-separated product IDs

    @Column(name = "time_spent")
    private Long timeSpent; // in milliseconds

    @Column(name = "exit_intent")
    @Builder.Default
    private Boolean exitIntent = false;

    @Column(name = "contact_attempts")
    @Builder.Default
    private Integer contactAttempts = 0;

    @Column(name = "interested_categories", length = 1000)
    private String interestedCategories; // Comma-separated categories

    @Column(name = "price_range_min")
    private Double priceRangeMin;

    @Column(name = "price_range_max")
    private Double priceRangeMax;

    @Enumerated(EnumType.STRING)
    @Column(name = "urgency")
    @Builder.Default
    private LeadUrgency urgency = LeadUrgency.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(name = "source")
    @Builder.Default
    private LeadSource source = LeadSource.SEARCH;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private LeadStatus status = LeadStatus.NEW;

    @Column(name = "lead_score")
    @Builder.Default
    private Integer leadScore = 0;

    @Column(name = "assigned_sales_rep")
    private String assignedSalesRep;

    @Column(name = "notes", length = 2000)
    private String notes;

    @Column(name = "follow_up_date")
    private LocalDateTime followUpDate;

    @Column(name = "converted")
    @Builder.Default
    private Boolean converted = false;

    @Column(name = "conversion_value")
    private Double conversionValue;

    @Column(name = "last_activity")
    private LocalDateTime lastActivity;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "referral_source")
    private String referralSource;

    // User reference (if logged in)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.lastActivity = LocalDateTime.now();
    }

    // Helper methods
    public List<String> getViewedProductsList() {
        if (viewedProducts == null || viewedProducts.isEmpty()) {
            return List.of();
        }
        return List.of(viewedProducts.split(","));
    }

    public void setViewedProductsList(List<String> products) {
        this.viewedProducts = String.join(",", products);
    }

    public List<String> getInterestedCategoriesList() {
        if (interestedCategories == null || interestedCategories.isEmpty()) {
            return List.of();
        }
        return List.of(interestedCategories.split(","));
    }

    public void setInterestedCategoriesList(List<String> categories) {
        this.interestedCategories = String.join(",", categories);
    }

    // Calculate lead score based on various factors
    public void calculateLeadScore() {
        int score = 0;
        
        // Time spent scoring
        if (timeSpent != null) {
            if (timeSpent > 300000) score += 25; // 5+ minutes
            else if (timeSpent > 120000) score += 15; // 2-5 minutes
            else if (timeSpent > 60000) score += 10; // 1-2 minutes
        }
        
        // Product views scoring
        List<String> products = getViewedProductsList();
        if (products.size() >= 5) score += 20;
        else if (products.size() >= 3) score += 15;
        else if (products.size() >= 1) score += 10;
        
        // Search activity scoring
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String[] queries = searchQuery.split(",");
            score += Math.min(queries.length * 5, 20); // Max 20 points for searches
        }
        
        // Urgency scoring
        switch (urgency) {
            case HIGH -> score += 30;
            case MEDIUM -> score += 20;
            case LOW -> score += 10;
        }
        
        // Contact info scoring
        if (phone != null && !phone.trim().isEmpty()) score += 15;
        if (email != null && !email.trim().isEmpty()) score += 10;
        
        // Exit intent penalty
        if (exitIntent) score += 5; // Still valuable but less committed
        
        this.leadScore = Math.min(score, 100); // Cap at 100
    }

    public enum LeadUrgency {
        LOW, MEDIUM, HIGH
    }

    public enum LeadSource {
        SEARCH, CATEGORY_BROWSE, PRODUCT_VIEW, CART_ABANDON, REFERRAL, DIRECT, WEBSITE
    }

    public enum LeadStatus {
        NEW, CONTACTED, QUALIFIED, PROPOSAL_SENT, FOLLOW_UP, CONVERTED, CLOSED_LOST
    }
}

