package com.itech.itech_backend.modules.product.model;

import com.itech.itech_backend.modules.vendor.model.VendorProfile;
import com.itech.itech_backend.modules.company.model.Company;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_product_sku", columnList = "sku"),
    @Index(name = "idx_product_vendor", columnList = "vendor_id"),
    @Index(name = "idx_product_category", columnList = "category"),
    @Index(name = "idx_product_status", columnList = "status"),
    @Index(name = "idx_product_visibility", columnList = "visibility"),
    @Index(name = "idx_product_featured", columnList = "is_featured"),
    @Index(name = "idx_product_price", columnList = "unit_price")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===============================
    // VENDOR ASSOCIATION
    // ===============================
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private VendorProfile vendor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company; // For company-specific products

    // ===============================
    // BASIC PRODUCT INFORMATION
    // ===============================
    
    @Column(name = "sku", nullable = false, unique = true, length = 100)
    private String sku; // Stock Keeping Unit
    
    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;
    
    @Column(name = "product_title", length = 300)
    private String productTitle;
    
    @Lob
    @Column(name = "description")
    private String description;
    
    @Lob
    @Column(name = "short_description")
    private String shortDescription;
    
    @Column(name = "brand", length = 100)
    private String brand;
    
    @Column(name = "manufacturer", length = 150)
    private String manufacturer;
    
    @Column(name = "model_number", length = 100)
    private String modelNumber;

    // ===============================
    // CATEGORIZATION
    // ===============================
    
    @Column(name = "category", nullable = false, length = 100)
    private String category;
    
    @Column(name = "sub_category", length = 100)
    private String subCategory;
    
    @ElementCollection
    @CollectionTable(name = "product_tags", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "tag")
    private List<String> tags;
    
    @Column(name = "hsn_code", length = 20) // Harmonized System of Nomenclature for India
    private String hsnCode;

    // ===============================
    // PRICING INFORMATION
    // ===============================
    
    @Column(name = "unit_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal unitPrice;
    
    @Column(name = "mrp", precision = 15, scale = 2) // Maximum Retail Price
    private BigDecimal mrp;
    
    @Column(name = "wholesale_price", precision = 15, scale = 2)
    private BigDecimal wholesalePrice;
    
    @Column(name = "bulk_price", precision = 15, scale = 2)
    private BigDecimal bulkPrice;
    
    @Column(name = "cost_price", precision = 15, scale = 2)
    private BigDecimal costPrice; // Vendor's cost
    
    @Column(name = "currency", length = 3)
    private String currency = "INR";
    
    @Column(name = "price_includes_tax")
    private Boolean priceIncludesTax = false;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "pricing_type")
    private PricingType pricingType = PricingType.FIXED;

    // ===============================
    // INVENTORY MANAGEMENT
    // ===============================
    
    @Column(name = "stock_quantity")
    private Integer stockQuantity = 0;
    
    @Column(name = "reserved_quantity")
    private Integer reservedQuantity = 0;
    
    @Column(name = "available_quantity")
    private Integer availableQuantity = 0;
    
    @Column(name = "minimum_order_quantity")
    private Integer minimumOrderQuantity = 1;
    
    @Column(name = "maximum_order_quantity")
    private Integer maximumOrderQuantity;
    
    @Column(name = "reorder_level")
    private Integer reorderLevel;
    
    @Column(name = "stock_status")
    @Enumerated(EnumType.STRING)
    private StockStatus stockStatus = StockStatus.IN_STOCK;
    
    @Column(name = "track_inventory")
    private Boolean trackInventory = true;
    
    @Column(name = "low_stock_threshold")
    private Integer lowStockThreshold;

    // ===============================
    // PRODUCT SPECIFICATIONS
    // ===============================
    
    @Column(name = "unit_of_measure", length = 50)
    private String unitOfMeasure;
    
    @Column(name = "weight", precision = 10, scale = 3)
    private BigDecimal weight; // in kg
    
    @Column(name = "length", precision = 10, scale = 2)
    private BigDecimal length; // in cm
    
    @Column(name = "width", precision = 10, scale = 2)
    private BigDecimal width; // in cm
    
    @Column(name = "height", precision = 10, scale = 2)
    private BigDecimal height; // in cm
    
    @ElementCollection
    @CollectionTable(name = "product_custom_attributes", joinColumns = @JoinColumn(name = "product_id"))
    @MapKeyColumn(name = "attribute_name")
    @Column(name = "attribute_value")
    private Map<String, String> attributes; // Custom attributes like color, size, material, etc.

    // ===============================
    // PRODUCT MEDIA
    // ===============================
    
    @Column(name = "primary_image_url", length = 500)
    private String primaryImageUrl;
    
    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url", length = 500)
    private List<String> imageUrls;
    
    @ElementCollection
    @CollectionTable(name = "product_documents", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "document_url", length = 500)
    private List<String> documentUrls; // Spec sheets, catalogs, manuals
    
    @Column(name = "video_url", length = 500)
    private String videoUrl;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductVideo> videos;

    // ===============================
    // STATUS AND VISIBILITY
    // ===============================
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProductStatus status = ProductStatus.DRAFT;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility")
    private ProductVisibility visibility = ProductVisibility.PUBLIC;
    
    @Column(name = "is_featured")
    private Boolean isFeatured = false;
    
    @Column(name = "is_bestseller")
    private Boolean isBestseller = false;
    
    @Column(name = "is_new_arrival")
    private Boolean isNewArrival = false;
    
    @Column(name = "is_on_sale")
    private Boolean isOnSale = false;
    
    @Column(name = "is_digital")
    private Boolean isDigital = false;

    // ===============================
    // LOGISTICS AND SHIPPING
    // ===============================
    
    @Column(name = "requires_shipping")
    private Boolean requiresShipping = true;
    
    @Column(name = "free_shipping")
    private Boolean freeShipping = false;
    
    @Column(name = "shipping_weight", precision = 10, scale = 3)
    private BigDecimal shippingWeight;
    
    @Column(name = "shipping_class", length = 50)
    private String shippingClass;
    
    @Column(name = "processing_time_days")
    private Integer processingTimeDays;
    
    @Column(name = "lead_time_days")
    private Integer leadTimeDays;
    
    @ElementCollection
    @CollectionTable(name = "product_shipping_regions", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "region")
    private List<String> shippingRegions;

    // ===============================
    // TAX AND COMPLIANCE
    // ===============================
    
    @Column(name = "tax_class", length = 50)
    private String taxClass;
    
    @Column(name = "gst_rate", precision = 5, scale = 2)
    private BigDecimal gstRate;
    
    @Column(name = "tax_inclusive")
    private Boolean taxInclusive = false;
    
    @Column(name = "requires_license")
    private Boolean requiresLicense = false;
    
    @Column(name = "restricted_product")
    private Boolean restrictedProduct = false;
    
    @ElementCollection
    @CollectionTable(name = "product_certifications", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "certification")
    private List<String> certifications;

    // ===============================
    // BUSINESS RULES
    // ===============================
    
    @Column(name = "bulk_discount_threshold")
    private Integer bulkDiscountThreshold;
    
    @Column(name = "bulk_discount_percentage", precision = 5, scale = 2)
    private BigDecimal bulkDiscountPercentage;
    
    @Column(name = "quantity_break_pricing")
    private Boolean quantityBreakPricing = false;
    
    @ElementCollection
    @CollectionTable(name = "product_quantity_breaks", joinColumns = @JoinColumn(name = "product_id"))
    @MapKeyColumn(name = "quantity")
    @Column(name = "price", precision = 15, scale = 2)
    private Map<Integer, BigDecimal> quantityBreaks; // quantity -> price
    
    @Column(name = "negotiable_price")
    private Boolean negotiablePrice = false;
    
    @Column(name = "rfq_enabled") // Request for Quote
    private Boolean rfqEnabled = false;

    // ===============================
    // ANALYTICS AND METRICS
    // ===============================
    
    @Column(name = "view_count")
    private Long viewCount = 0L;
    
    @Column(name = "order_count")
    private Long orderCount = 0L;
    
    @Column(name = "total_sold_quantity")
    private Long totalSoldQuantity = 0L;
    
    @Column(name = "total_revenue", precision = 15, scale = 2)
    private BigDecimal totalRevenue = BigDecimal.ZERO;
    
    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;
    
    @Column(name = "review_count")
    private Integer reviewCount = 0;
    
    @Column(name = "last_viewed_at")
    private LocalDateTime lastViewedAt;
    
    @Column(name = "last_ordered_at")
    private LocalDateTime lastOrderedAt;

    // ===============================
    // SEO AND MARKETING
    // ===============================
    
    @Column(name = "seo_title", length = 200)
    private String seoTitle;
    
    @Column(name = "seo_description", length = 500)
    private String seoDescription;
    
    @Column(name = "seo_keywords", length = 300)
    private String seoKeywords;
    
    @Column(name = "meta_title", length = 200)
    private String metaTitle;
    
    @Column(name = "meta_description", length = 500)
    private String metaDescription;
    
    @Column(name = "url_slug", length = 300)
    private String urlSlug;

    // ===============================
    // RELATED PRODUCTS
    // ===============================
    
    @ManyToMany
    @JoinTable(
        name = "product_cross_sells",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "cross_sell_product_id")
    )
    private List<Product> crossSells;
    
    @ManyToMany
    @JoinTable(
        name = "product_upsells",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "upsell_product_id")
    )
    private List<Product> upsells;
    
    @ManyToMany
    @JoinTable(
        name = "product_related",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "related_product_id")
    )
    private List<Product> relatedProducts;

    // ===============================
    // VARIANTS AND OPTIONS
    // ===============================
    
    @Column(name = "has_variants")
    private Boolean hasVariants = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_product_id")
    private Product parentProduct; // For product variants
    
    @OneToMany(mappedBy = "parentProduct", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> variants;
    
    @Column(name = "variant_type", length = 50)
    private String variantType; // COLOR, SIZE, MATERIAL, etc.
    
    @Column(name = "variant_value", length = 100)
    private String variantValue;

    // ===============================
    // SUBSCRIPTION AND RECURRING
    // ===============================
    
    @Column(name = "subscription_product")
    private Boolean subscriptionProduct = false;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_period")
    private SubscriptionPeriod subscriptionPeriod;
    
    @Column(name = "subscription_trial_days")
    private Integer subscriptionTrialDays;

    // ===============================
    // EXTERNAL INTEGRATIONS
    // ===============================
    
    @Column(name = "external_id", length = 100)
    private String externalId; // For ERP integration
    
    @Column(name = "barcode", length = 50)
    private String barcode;
    
    @Column(name = "qr_code", length = 500)
    private String qrCode;
    
    @ElementCollection
    @CollectionTable(name = "product_external_ids", joinColumns = @JoinColumn(name = "product_id"))
    @MapKeyColumn(name = "system_name")
    @Column(name = "external_id")
    private Map<String, String> externalIds; // system -> external_id

    // ===============================
    // AUDIT AND TIMESTAMPS
    // ===============================
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    
    @Column(name = "discontinued_at")
    private LocalDateTime discontinuedAt;
    
    @Column(name = "created_by", length = 100)
    private String createdBy;
    
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    // ===============================
    // ENUMS
    // ===============================
    
    public enum ProductStatus {
        DRAFT,
        PENDING_REVIEW,
        APPROVED,
        PUBLISHED,
        OUT_OF_STOCK,
        DISCONTINUED,
        REJECTED,
        ARCHIVED
    }
    
    public enum ProductVisibility {
        PUBLIC,
        PRIVATE,
        CATALOG_ONLY,
        HIDDEN,
        B2B_ONLY,
        PREMIUM_ONLY
    }
    
    public enum StockStatus {
        IN_STOCK,
        OUT_OF_STOCK,
        LOW_STOCK,
        BACKORDER,
        PREORDER,
        DISCONTINUED
    }
    
    public enum PricingType {
        FIXED,
        DYNAMIC,
        NEGOTIABLE,
        QUOTE_BASED,
        AUCTION,
        TIERED
    }
    
    public enum SubscriptionPeriod {
        WEEKLY,
        MONTHLY,
        QUARTERLY,
        ANNUALLY
    }

    // ===============================
    // HELPER METHODS
    // ===============================
    
    public Boolean isInStock() {
        return stockStatus == StockStatus.IN_STOCK && availableQuantity > 0;
    }
    
    public Boolean isLowStock() {
        return lowStockThreshold != null && availableQuantity <= lowStockThreshold;
    }
    
    public Boolean isPublished() {
        return status == ProductStatus.PUBLISHED;
    }
    
    public Boolean isVisible() {
        return visibility == ProductVisibility.PUBLIC && isPublished();
    }
    
    public BigDecimal getEffectivePrice() {
        return unitPrice;
    }
    
    public void updateAvailableQuantity() {
        this.availableQuantity = Math.max(0, this.stockQuantity - this.reservedQuantity);
    }
    
    public void incrementViewCount() {
        this.viewCount = (this.viewCount != null ? this.viewCount : 0L) + 1;
        this.lastViewedAt = LocalDateTime.now();
    }
    
    @PrePersist
    protected void onCreate() {
        updateAvailableQuantity();
        if (urlSlug == null && productName != null) {
            urlSlug = generateUrlSlug(productName);
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updateAvailableQuantity();
    }
    
    private String generateUrlSlug(String name) {
        return name.toLowerCase()
                   .replaceAll("[^a-z0-9\\s]", "")
                   .replaceAll("\\s+", "-")
                   .trim();
    }
}

