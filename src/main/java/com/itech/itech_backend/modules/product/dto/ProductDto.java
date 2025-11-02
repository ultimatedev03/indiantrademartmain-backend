package com.itech.itech_backend.modules.product.dto;

import com.itech.itech_backend.modules.product.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private Long id;

    // Vendor and Company Association
    private Long vendorId;
    private String vendorName;
    private Long companyId;
    private String companyName;

    // Basic Product Information
    private String sku;
    private String productName;
    private String productTitle;
    private String description;
    private String shortDescription;
    private String brand;
    private String manufacturer;
    private String modelNumber;

    // Categorization
    private String category;
    private String subCategory;
    private List<String> tags;
    private String hsnCode;

    // Pricing Information
    private BigDecimal unitPrice;
    private BigDecimal mrp;
    private BigDecimal wholesalePrice;
    private BigDecimal bulkPrice;
    private String currency;
    private Boolean priceIncludesTax;
    private Product.PricingType pricingType;

    // Inventory Management
    private Integer stockQuantity;
    private Integer reservedQuantity;
    private Integer availableQuantity;
    private Integer minimumOrderQuantity;
    private Integer maximumOrderQuantity;
    private Integer reorderLevel;
    private Product.StockStatus stockStatus;
    private Boolean trackInventory;
    private Integer lowStockThreshold;

    // Product Specifications
    private String unitOfMeasure;
    private BigDecimal weight;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private Map<String, String> attributes;

    // Product Media
    private String primaryImageUrl;
    private List<String> imageUrls;
    private List<String> documentUrls;
    private String videoUrl;

    // Status and Visibility
    private Product.ProductStatus status;
    private Product.ProductVisibility visibility;
    private Boolean isFeatured;
    private Boolean isBestseller;
    private Boolean isNewArrival;
    private Boolean isOnSale;
    private Boolean isDigital;

    // Logistics and Shipping
    private Boolean requiresShipping;
    private Boolean freeShipping;
    private BigDecimal shippingWeight;
    private String shippingClass;
    private Integer processingTimeDays;
    private Integer leadTimeDays;
    private List<String> shippingRegions;

    // Tax and Compliance
    private String taxClass;
    private BigDecimal gstRate;
    private Boolean taxInclusive;
    private Boolean requiresLicense;
    private Boolean restrictedProduct;
    private List<String> certifications;

    // Business Rules
    private Integer bulkDiscountThreshold;
    private BigDecimal bulkDiscountPercentage;
    private Boolean quantityBreakPricing;
    private Map<Integer, BigDecimal> quantityBreaks;
    private Boolean negotiablePrice;
    private Boolean rfqEnabled;

    // Analytics and Metrics
    private Long viewCount;
    private Long orderCount;
    private Long totalSoldQuantity;
    private BigDecimal totalRevenue;
    private BigDecimal averageRating;
    private Integer reviewCount;
    private LocalDateTime lastViewedAt;
    private LocalDateTime lastOrderedAt;

    // SEO and Marketing
    private String seoTitle;
    private String seoDescription;
    private String seoKeywords;
    private String metaTitle;
    private String metaDescription;
    private String urlSlug;

    // Variants and Options
    private Boolean hasVariants;
    private Long parentProductId;
    private String parentProductName;
    private List<ProductVariantDto> variants;
    private String variantType;
    private String variantValue;

    // Subscription and Recurring
    private Boolean subscriptionProduct;
    private Product.SubscriptionPeriod subscriptionPeriod;
    private Integer subscriptionTrialDays;

    // External Integrations
    private String externalId;
    private String barcode;
    private String qrCode;
    private Map<String, String> externalIds;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
    private LocalDateTime discontinuedAt;
    private String createdBy;
    private String updatedBy;

    // Computed Fields
    private Boolean inStock;
    private Boolean lowStock;
    private Boolean published;
    private Boolean visible;
    private BigDecimal effectivePrice;
    private String stockStatusDisplay;
    private BigDecimal discountPercentage;
    private Boolean hasDiscount;
    
    // Related Products (simplified)
    private List<RelatedProductDto> crossSells;
    private List<RelatedProductDto> upsells;
    private List<RelatedProductDto> relatedProducts;

    // Nested DTOs
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductVariantDto {
        private Long id;
        private String sku;
        private String variantType;
        private String variantValue;
        private BigDecimal unitPrice;
        private Integer stockQuantity;
        private String primaryImageUrl;
        private Product.ProductStatus status;
        private Boolean inStock;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelatedProductDto {
        private Long id;
        private String sku;
        private String productName;
        private BigDecimal unitPrice;
        private String primaryImageUrl;
        private String brand;
        private BigDecimal averageRating;
        private Boolean inStock;
        private String urlSlug;
    }

    // Helper methods for computed fields
    public Boolean getInStock() {
        return stockStatus == Product.StockStatus.IN_STOCK && availableQuantity > 0;
    }

    public Boolean getLowStock() {
        return lowStockThreshold != null && availableQuantity <= lowStockThreshold;
    }

    public Boolean getPublished() {
        return status == Product.ProductStatus.PUBLISHED;
    }

    public Boolean getVisible() {
        return visibility == Product.ProductVisibility.PUBLIC && getPublished();
    }

    public String getStockStatusDisplay() {
        if (stockStatus == null) return "Unknown";
        
        switch (stockStatus) {
            case IN_STOCK:
                return availableQuantity > 0 ? "In Stock (" + availableQuantity + ")" : "Out of Stock";
            case OUT_OF_STOCK:
                return "Out of Stock";
            case LOW_STOCK:
                return "Low Stock (" + availableQuantity + ")";
            case BACKORDER:
                return "Available on Backorder";
            case PREORDER:
                return "Available for Preorder";
            case DISCONTINUED:
                return "Discontinued";
            default:
                return stockStatus.name();
        }
    }

    public BigDecimal getDiscountPercentage() {
        if (mrp != null && unitPrice != null && mrp.compareTo(unitPrice) > 0) {
            BigDecimal discount = mrp.subtract(unitPrice);
            return discount.divide(mrp, 2, RoundingMode.HALF_UP)
                          .multiply(BigDecimal.valueOf(100));
        }
        return BigDecimal.ZERO;
    }

    public Boolean getHasDiscount() {
        return getDiscountPercentage().compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getEffectivePrice() {
        // Can be enhanced with bulk pricing, user-specific pricing, etc.
        return unitPrice;
    }
}

