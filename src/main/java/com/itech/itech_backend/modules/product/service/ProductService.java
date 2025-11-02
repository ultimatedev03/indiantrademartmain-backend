package com.itech.itech_backend.modules.product.service;

import com.itech.itech_backend.modules.product.dto.CreateProductDto;
import com.itech.itech_backend.modules.product.dto.ProductDto;
import com.itech.itech_backend.modules.product.dto.UpdateProductDto;
import com.itech.itech_backend.modules.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductService {

    // ===============================
    // CORE CRUD OPERATIONS
    // ===============================
    
    /**
     * Create a new product with validation
     */
    ProductDto createProduct(CreateProductDto createProductDto);
    
    /**
     * Get product by ID
     */
    Optional<ProductDto> getProductById(Long productId);
    
    /**
     * Get product by SKU
     */
    Optional<ProductDto> getProductBySku(String sku);
    
    /**
     * Get product by URL slug
     */
    Optional<ProductDto> getProductByUrlSlug(String urlSlug);
    
    /**
     * Update product information
     */
    ProductDto updateProduct(Long productId, UpdateProductDto updateProductDto);
    
    /**
     * Delete product (soft delete)
     */
    void deleteProduct(Long productId);
    
    /**
     * Hard delete product (permanent removal)
     */
    void hardDeleteProduct(Long productId);

    // ===============================
    // PRODUCT STATUS MANAGEMENT
    // ===============================
    
    /**
     * Publish product
     */
    void publishProduct(Long productId);
    
    /**
     * Unpublish product
     */
    void unpublishProduct(Long productId);
    
    /**
     * Archive product
     */
    void archiveProduct(Long productId);
    
    /**
     * Update product status
     */
    void updateProductStatus(Long productId, Product.ProductStatus status, String reason);
    
    /**
     * Update product visibility
     */
    void updateProductVisibility(Long productId, Product.ProductVisibility visibility);

    // ===============================
    // INVENTORY MANAGEMENT
    // ===============================
    
    /**
     * Update stock quantity
     */
    void updateStock(Long productId, Integer quantity);
    
    /**
     * Reserve stock for order
     */
    void reserveStock(Long productId, Integer quantity);
    
    /**
     * Release reserved stock
     */
    void releaseReservedStock(Long productId, Integer quantity);
    
    /**
     * Update stock status
     */
    void updateStockStatus(Long productId, Product.StockStatus stockStatus);
    
    /**
     * Get low stock products
     */
    List<ProductDto> getLowStockProducts();
    
    /**
     * Get products needing reorder
     */
    List<ProductDto> getProductsNeedingReorder();

    // ===============================
    // PRICING MANAGEMENT
    // ===============================
    
    /**
     * Update product price
     */
    void updatePrice(Long productId, BigDecimal unitPrice);
    
    /**
     * Update bulk pricing
     */
    void updateBulkPricing(Long productId, Map<Integer, BigDecimal> quantityBreaks);
    
    /**
     * Calculate effective price for quantity
     */
    BigDecimal calculateEffectivePrice(Long productId, Integer quantity);
    
    /**
     * Get price history
     */
    List<Map<String, Object>> getPriceHistory(Long productId);

    // ===============================
    // CATALOG OPERATIONS
    // ===============================
    
    /**
     * Get all published products
     */
    Page<ProductDto> getPublishedProducts(Pageable pageable);
    
    /**
     * Get products by category
     */
    Page<ProductDto> getProductsByCategory(String category, Pageable pageable);
    
    /**
     * Get products by brand
     */
    Page<ProductDto> getProductsByBrand(String brand, Pageable pageable);
    
    /**
     * Get products by vendor
     */
    Page<ProductDto> getProductsByVendor(Long vendorId, Pageable pageable);
    
    /**
     * Get featured products
     */
    Page<ProductDto> getFeaturedProducts(Pageable pageable);
    
    /**
     * Get bestseller products
     */
    Page<ProductDto> getBestsellerProducts(Pageable pageable);
    
    /**
     * Get new arrival products
     */
    Page<ProductDto> getNewArrivals(Pageable pageable);
    
    /**
     * Get products on sale
     */
    Page<ProductDto> getSaleProducts(Pageable pageable);
    
    /**
     * Get discounted products
     */
    Page<ProductDto> getDiscountedProducts(Pageable pageable);

    // ===============================
    // SEARCH AND FILTERING
    // ===============================
    
    /**
     * Search products by text
     */
    Page<ProductDto> searchProducts(String searchTerm, Pageable pageable);
    
    /**
     * Filter products with multiple criteria
     */
    Page<ProductDto> filterProducts(Long vendorId, String category, String subCategory,
                                   String brand, BigDecimal minPrice, BigDecimal maxPrice,
                                   Product.StockStatus stockStatus, Boolean isFeatured,
                                   Product.ProductStatus status, Pageable pageable);
    
    /**
     * Get products in price range
     */
    Page<ProductDto> getProductsInPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    /**
     * Get products by stock status
     */
    Page<ProductDto> getProductsByStockStatus(Product.StockStatus stockStatus, Pageable pageable);

    // ===============================
    // VARIANT MANAGEMENT
    // ===============================
    
    /**
     * Create product variant
     */
    ProductDto createProductVariant(Long parentProductId, CreateProductDto variantDto);
    
    /**
     * Get product variants
     */
    List<ProductDto> getProductVariants(Long parentProductId);
    
    /**
     * Update variant information
     */
    ProductDto updateVariant(Long variantId, UpdateProductDto updateDto);
    
    /**
     * Delete variant
     */
    void deleteVariant(Long variantId);

    // ===============================
    // MEDIA MANAGEMENT
    // ===============================
    
    /**
     * Update primary image
     */
    void updatePrimaryImage(Long productId, String imageUrl);
    
    /**
     * Add product images
     */
    void addProductImages(Long productId, List<String> imageUrls);
    
    /**
     * Remove product image
     */
    void removeProductImage(Long productId, String imageUrl);
    
    /**
     * Add product documents
     */
    void addProductDocuments(Long productId, List<String> documentUrls);
    
    /**
     * Update video URL
     */
    void updateVideoUrl(Long productId, String videoUrl);

    // ===============================
    // ANALYTICS AND METRICS
    // ===============================
    
    /**
     * Increment product view count
     */
    void incrementViewCount(Long productId);
    
    /**
     * Update sales metrics
     */
    void updateSalesMetrics(Long productId, Integer quantitySold, BigDecimal revenue);
    
    /**
     * Update review metrics
     */
    void updateReviewMetrics(Long productId, BigDecimal averageRating, Integer reviewCount);
    
    /**
     * Get product analytics
     */
    Map<String, Object> getProductAnalytics(Long productId);
    
    /**
     * Get vendor product analytics
     */
    Map<String, Object> getVendorProductAnalytics(Long vendorId);
    
    /**
     * Get category performance
     */
    Map<String, Object> getCategoryPerformance();

    // ===============================
    // BUSINESS INTELLIGENCE
    // ===============================
    
    /**
     * Get most viewed products
     */
    Page<ProductDto> getMostViewedProducts(Pageable pageable);
    
    /**
     * Get most sold products
     */
    Page<ProductDto> getMostSoldProducts(Pageable pageable);
    
    /**
     * Get highest revenue products
     */
    Page<ProductDto> getHighestRevenueProducts(Pageable pageable);
    
    /**
     * Get highest rated products
     */
    Page<ProductDto> getHighestRatedProducts(Pageable pageable);
    
    /**
     * Get trending products
     */
    List<ProductDto> getTrendingProducts(Integer days, Long minOrders);
    
    /**
     * Get slow moving products
     */
    List<ProductDto> getSlowMovingProducts(Integer daysSinceLastOrder);
    
    /**
     * Get high view low order products
     */
    List<ProductDto> getHighViewLowOrderProducts(Long minViews, Long maxOrders);

    // ===============================
    // RECOMMENDATIONS
    // ===============================
    
    /**
     * Get recommended products by category
     */
    Page<ProductDto> getRecommendedProductsByCategory(String category, Long excludeProductId, Pageable pageable);
    
    /**
     * Get recommended products by brand
     */
    Page<ProductDto> getRecommendedProductsByBrand(String brand, Long excludeProductId, Pageable pageable);
    
    /**
     * Get cross-sell products
     */
    List<ProductDto> getCrossSellProducts(Long productId);
    
    /**
     * Get upsell products
     */
    List<ProductDto> getUpsellProducts(Long productId);
    
    /**
     * Get related products
     */
    List<ProductDto> getRelatedProducts(Long productId);
    
    /**
     * Update related products
     */
    void updateRelatedProducts(Long productId, List<Long> crossSellIds, 
                              List<Long> upsellIds, List<Long> relatedIds);

    // ===============================
    // CATALOG MANAGEMENT
    // ===============================
    
    /**
     * Get all categories
     */
    List<String> getAllCategories();
    
    /**
     * Get subcategories by category
     */
    List<String> getSubCategories(String category);
    
    /**
     * Get all brands
     */
    List<String> getAllBrands();
    
    /**
     * Get category statistics
     */
    Map<String, Long> getCategoryStatistics();
    
    /**
     * Get brand statistics
     */
    Map<String, Long> getBrandStatistics();

    // ===============================
    // BULK OPERATIONS
    // ===============================
    
    /**
     * Bulk update product status
     */
    void bulkUpdateStatus(List<Long> productIds, Product.ProductStatus status, String reason);
    
    /**
     * Bulk update prices
     */
    void bulkUpdatePrices(Map<Long, BigDecimal> productPrices);
    
    /**
     * Bulk update stock
     */
    void bulkUpdateStock(Map<Long, Integer> productStock);
    
    /**
     * Bulk export products
     */
    byte[] bulkExportProducts(List<Long> productIds, String format);
    
    /**
     * Import products from file
     */
    Map<String, Object> importProducts(Long vendorId, byte[] fileData, String format);

    // ===============================
    // VALIDATION AND UTILITIES
    // ===============================
    
    /**
     * Check if SKU is available
     */
    boolean isSkuAvailable(String sku);
    
    /**
     * Check if URL slug is available
     */
    boolean isUrlSlugAvailable(String urlSlug);
    
    /**
     * Generate unique SKU
     */
    String generateUniqueSku(String prefix);
    
    /**
     * Generate URL slug from product name
     */
    String generateUrlSlug(String productName);
    
    /**
     * Validate product data
     */
    Map<String, String> validateProductData(CreateProductDto createProductDto);

    // ===============================
    // DASHBOARD AND REPORTING
    // ===============================
    
    /**
     * Get product dashboard statistics
     */
    Map<String, Object> getProductDashboardStats();
    
    /**
     * Get vendor dashboard statistics
     */
    Map<String, Object> getVendorDashboardStats(Long vendorId);
    
    /**
     * Get product business metrics
     */
    Map<String, Object> getProductBusinessMetrics();
    
    /**
     * Generate product report
     */
    byte[] generateProductReport(LocalDateTime startDate, LocalDateTime endDate, String format);

    // ===============================
    // SUBSCRIPTION MANAGEMENT
    // ===============================
    
    /**
     * Create subscription product
     */
    ProductDto createSubscriptionProduct(CreateProductDto createDto, 
                                        Product.SubscriptionPeriod period, 
                                        Integer trialDays);
    
    /**
     * Update subscription settings
     */
    void updateSubscriptionSettings(Long productId, Product.SubscriptionPeriod period, 
                                   Integer trialDays);
    
    /**
     * Get subscription products
     */
    Page<ProductDto> getSubscriptionProducts(Pageable pageable);

    // ===============================
    // EXTERNAL INTEGRATIONS
    // ===============================
    
    /**
     * Sync with external inventory system
     */
    void syncWithExternalInventory(Long productId);
    
    /**
     * Update from external catalog
     */
    void updateFromExternalCatalog(Long productId, Map<String, Object> externalData);
    
    /**
     * Generate barcode
     */
    String generateBarcode(Long productId);
    
    /**
     * Generate QR code
     */
    String generateQrCode(Long productId);
}

