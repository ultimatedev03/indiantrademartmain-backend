package com.itech.itech_backend.modules.product.dto;

import com.itech.itech_backend.modules.product.model.Product;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductDto {

    // Basic Product Information
    @Size(max = 255, message = "Product name must not exceed 255 characters")
    private String productName;

    @Size(max = 300, message = "Product title must not exceed 300 characters")
    private String productTitle;

    @Size(min = 10, message = "Description must be at least 10 characters")
    private String description;

    @Size(max = 500, message = "Short description must not exceed 500 characters")
    private String shortDescription;

    @Size(max = 100, message = "Brand must not exceed 100 characters")
    private String brand;

    @Size(max = 150, message = "Manufacturer must not exceed 150 characters")
    private String manufacturer;

    @Size(max = 100, message = "Model number must not exceed 100 characters")
    private String modelNumber;

    // Categorization
    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;

    @Size(max = 100, message = "Sub-category must not exceed 100 characters")
    private String subCategory;

    private List<String> tags;

    @Pattern(regexp = "^[0-9]{4,8}$", message = "HSN code must be 4-8 digits")
    private String hsnCode;

    // Pricing Information
    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than 0")
    @Digits(integer = 13, fraction = 2, message = "Invalid price format")
    private BigDecimal unitPrice;

    @DecimalMin(value = "0.0", message = "MRP must be non-negative")
    @Digits(integer = 13, fraction = 2, message = "Invalid MRP format")
    private BigDecimal mrp;

    @DecimalMin(value = "0.0", message = "Wholesale price must be non-negative")
    @Digits(integer = 13, fraction = 2, message = "Invalid wholesale price format")
    private BigDecimal wholesalePrice;

    @DecimalMin(value = "0.0", message = "Bulk price must be non-negative")
    @Digits(integer = 13, fraction = 2, message = "Invalid bulk price format")
    private BigDecimal bulkPrice;

    @DecimalMin(value = "0.0", message = "Cost price must be non-negative")
    @Digits(integer = 13, fraction = 2, message = "Invalid cost price format")
    private BigDecimal costPrice;

    @Size(max = 3, message = "Currency must be 3 characters")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a valid 3-letter code")
    private String currency;

    private Boolean priceIncludesTax;
    private Product.PricingType pricingType;

    // Inventory Management
    @Min(value = 0, message = "Stock quantity must be non-negative")
    private Integer stockQuantity;

    @Min(value = 0, message = "Reserved quantity must be non-negative")
    private Integer reservedQuantity;

    @Min(value = 1, message = "Minimum order quantity must be at least 1")
    private Integer minimumOrderQuantity;

    @Min(value = 1, message = "Maximum order quantity must be at least 1")
    private Integer maximumOrderQuantity;

    @Min(value = 0, message = "Reorder level must be non-negative")
    private Integer reorderLevel;

    private Product.StockStatus stockStatus;
    private Boolean trackInventory;

    @Min(value = 0, message = "Low stock threshold must be non-negative")
    private Integer lowStockThreshold;

    // Product Specifications
    @Size(max = 50, message = "Unit of measure must not exceed 50 characters")
    private String unitOfMeasure;

    @DecimalMin(value = "0.0", message = "Weight must be non-negative")
    @Digits(integer = 7, fraction = 3, message = "Invalid weight format")
    private BigDecimal weight;

    @DecimalMin(value = "0.0", message = "Length must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid length format")
    private BigDecimal length;

    @DecimalMin(value = "0.0", message = "Width must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid width format")
    private BigDecimal width;

    @DecimalMin(value = "0.0", message = "Height must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid height format")
    private BigDecimal height;

    private Map<String, String> attributes;

    // Product Media
    @Pattern(regexp = "^(https?://).*\\.(jpg|jpeg|png|gif|webp)$", message = "Invalid image URL format")
    private String primaryImageUrl;

    private List<String> imageUrls;
    private List<String> documentUrls;

    @Pattern(regexp = "^(https?://).*$", message = "Invalid video URL format")
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

    @DecimalMin(value = "0.0", message = "Shipping weight must be non-negative")
    @Digits(integer = 7, fraction = 3, message = "Invalid shipping weight format")
    private BigDecimal shippingWeight;

    @Size(max = 50, message = "Shipping class must not exceed 50 characters")
    private String shippingClass;

    @Min(value = 0, message = "Processing time must be non-negative")
    @Max(value = 365, message = "Processing time cannot exceed 365 days")
    private Integer processingTimeDays;

    @Min(value = 0, message = "Lead time must be non-negative")
    @Max(value = 365, message = "Lead time cannot exceed 365 days")
    private Integer leadTimeDays;

    private List<String> shippingRegions;

    // Tax and Compliance
    @Size(max = 50, message = "Tax class must not exceed 50 characters")
    private String taxClass;

    @DecimalMin(value = "0.0", message = "GST rate must be non-negative")
    @DecimalMax(value = "100.0", message = "GST rate cannot exceed 100%")
    @Digits(integer = 2, fraction = 2, message = "Invalid GST rate format")
    private BigDecimal gstRate;

    private Boolean taxInclusive;
    private Boolean requiresLicense;
    private Boolean restrictedProduct;
    private List<String> certifications;

    // Business Rules
    @Min(value = 1, message = "Bulk discount threshold must be at least 1")
    private Integer bulkDiscountThreshold;

    @DecimalMin(value = "0.0", message = "Bulk discount percentage must be non-negative")
    @DecimalMax(value = "100.0", message = "Bulk discount percentage cannot exceed 100%")
    @Digits(integer = 2, fraction = 2, message = "Invalid bulk discount percentage format")
    private BigDecimal bulkDiscountPercentage;

    private Boolean quantityBreakPricing;
    private Map<Integer, BigDecimal> quantityBreaks;
    private Boolean negotiablePrice;
    private Boolean rfqEnabled;

    // SEO and Marketing
    @Size(max = 200, message = "SEO title must not exceed 200 characters")
    private String seoTitle;

    @Size(max = 500, message = "SEO description must not exceed 500 characters")
    private String seoDescription;

    @Size(max = 300, message = "SEO keywords must not exceed 300 characters")
    private String seoKeywords;

    @Size(max = 200, message = "Meta title must not exceed 200 characters")
    private String metaTitle;

    @Size(max = 500, message = "Meta description must not exceed 500 characters")
    private String metaDescription;

    @Size(max = 300, message = "URL slug must not exceed 300 characters")
    @Pattern(regexp = "^[a-z0-9-]*$", message = "URL slug must contain only lowercase letters, numbers, and hyphens")
    private String urlSlug;

    // Variants and Options
    private Boolean hasVariants;

    @Size(max = 50, message = "Variant type must not exceed 50 characters")
    private String variantType;

    @Size(max = 100, message = "Variant value must not exceed 100 characters")
    private String variantValue;

    // Subscription and Recurring
    private Boolean subscriptionProduct;
    private Product.SubscriptionPeriod subscriptionPeriod;

    @Min(value = 0, message = "Subscription trial days must be non-negative")
    @Max(value = 365, message = "Subscription trial days cannot exceed 365")
    private Integer subscriptionTrialDays;

    // External Integrations
    @Size(max = 100, message = "External ID must not exceed 100 characters")
    private String externalId;

    @Size(max = 50, message = "Barcode must not exceed 50 characters")
    private String barcode;

    private Map<String, String> externalIds;

    // Audit
    @Size(max = 100, message = "Updated by must not exceed 100 characters")
    private String updatedBy;

    // Related Products
    private List<Long> crossSellProductIds;
    private List<Long> upsellProductIds;
    private List<Long> relatedProductIds;
}

