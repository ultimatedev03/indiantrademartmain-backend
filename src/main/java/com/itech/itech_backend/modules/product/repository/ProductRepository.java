package com.itech.itech_backend.modules.product.repository;

import com.itech.itech_backend.modules.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ===============================
    // BASIC FINDER METHODS
    // ===============================
    
    Optional<Product> findBySku(String sku);
    boolean existsBySku(String sku);
    Optional<Product> findByUrlSlug(String urlSlug);
    boolean existsByUrlSlug(String urlSlug);
    
    List<Product> findByVendorId(Long vendorId);
    Page<Product> findByVendorId(Long vendorId, Pageable pageable);
    
    List<Product> findByCompanyId(Long companyId);
    Page<Product> findByCompanyId(Long companyId, Pageable pageable);

    // ===============================
    // STATUS AND VISIBILITY QUERIES
    // ===============================
    
    List<Product> findByStatus(Product.ProductStatus status);
    Page<Product> findByStatus(Product.ProductStatus status, Pageable pageable);
    
    List<Product> findByVisibility(Product.ProductVisibility visibility);
    Page<Product> findByVisibility(Product.ProductVisibility visibility, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.status = 'PUBLISHED' AND p.visibility = 'PUBLIC'")
    Page<Product> findPublishedProducts(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.vendor.id = :vendorId AND p.status = :status")
    Page<Product> findByVendorIdAndStatus(@Param("vendorId") Long vendorId, 
                                          @Param("status") Product.ProductStatus status, 
                                          Pageable pageable);

    // ===============================
    // CATEGORY AND BRAND QUERIES
    // ===============================
    
    List<Product> findByCategory(String category);
    Page<Product> findByCategory(String category, Pageable pageable);
    
    List<Product> findByCategoryAndSubCategory(String category, String subCategory);
    Page<Product> findByCategoryAndSubCategory(String category, String subCategory, Pageable pageable);
    
    List<Product> findByBrand(String brand);
    Page<Product> findByBrand(String brand, Pageable pageable);
    
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.status = 'PUBLISHED' ORDER BY p.category")
    List<String> findAllCategories();
    
    @Query("SELECT DISTINCT p.subCategory FROM Product p WHERE p.category = :category AND p.status = 'PUBLISHED' ORDER BY p.subCategory")
    List<String> findSubCategoriesByCategory(@Param("category") String category);
    
    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.status = 'PUBLISHED' ORDER BY p.brand")
    List<String> findAllBrands();

    // ===============================
    // PRICING QUERIES
    // ===============================
    
    @Query("SELECT p FROM Product p WHERE p.unitPrice BETWEEN :minPrice AND :maxPrice AND p.status = 'PUBLISHED'")
    Page<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, 
                                   @Param("maxPrice") BigDecimal maxPrice, 
                                   Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.unitPrice <= :maxPrice AND p.status = 'PUBLISHED'")
    Page<Product> findByMaxPrice(@Param("maxPrice") BigDecimal maxPrice, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.unitPrice >= :minPrice AND p.status = 'PUBLISHED'")
    Page<Product> findByMinPrice(@Param("minPrice") BigDecimal minPrice, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.pricingType = :pricingType AND p.status = 'PUBLISHED'")
    Page<Product> findByPricingType(@Param("pricingType") Product.PricingType pricingType, Pageable pageable);

    // ===============================
    // INVENTORY QUERIES
    // ===============================
    
    List<Product> findByStockStatus(Product.StockStatus stockStatus);
    Page<Product> findByStockStatus(Product.StockStatus stockStatus, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.stockStatus = 'IN_STOCK' AND p.availableQuantity > 0")
    Page<Product> findInStockProducts(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.availableQuantity <= p.lowStockThreshold AND p.lowStockThreshold IS NOT NULL")
    List<Product> findLowStockProducts();
    
    @Query("SELECT p FROM Product p WHERE p.availableQuantity <= p.reorderLevel AND p.reorderLevel IS NOT NULL")
    List<Product> findProductsNeedingReorder();
    
    @Query("SELECT p FROM Product p WHERE p.availableQuantity >= :minQuantity")
    Page<Product> findByMinAvailableQuantity(@Param("minQuantity") Integer minQuantity, Pageable pageable);

    // ===============================
    // SEARCH QUERIES
    // ===============================
    
    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.productName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "p.description LIKE CONCAT('%', :searchTerm, '%') OR " +
           "LOWER(p.brand) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.sku) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Product> searchProducts(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE " +
           "p.status = 'PUBLISHED' AND p.visibility = 'PUBLIC' AND (" +
           "LOWER(p.productName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "p.description LIKE CONCAT('%', :searchTerm, '%') OR " +
           "LOWER(p.brand) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.sku) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Product> searchPublishedProducts(@Param("searchTerm") String searchTerm, Pageable pageable);

    // ===============================
    // ADVANCED FILTERING
    // ===============================
    
    @Query("SELECT p FROM Product p WHERE " +
           "(:vendorId IS NULL OR p.vendor.id = :vendorId) AND " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:subCategory IS NULL OR p.subCategory = :subCategory) AND " +
           "(:brand IS NULL OR p.brand = :brand) AND " +
           "(:minPrice IS NULL OR p.unitPrice >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.unitPrice <= :maxPrice) AND " +
           "(:stockStatus IS NULL OR p.stockStatus = :stockStatus) AND " +
           "(:isFeatured IS NULL OR p.isFeatured = :isFeatured) AND " +
           "(:status IS NULL OR p.status = :status)")
    Page<Product> findProductsWithFilters(@Param("vendorId") Long vendorId,
                                          @Param("category") String category,
                                          @Param("subCategory") String subCategory,
                                          @Param("brand") String brand,
                                          @Param("minPrice") BigDecimal minPrice,
                                          @Param("maxPrice") BigDecimal maxPrice,
                                          @Param("stockStatus") Product.StockStatus stockStatus,
                                          @Param("isFeatured") Boolean isFeatured,
                                          @Param("status") Product.ProductStatus status,
                                          Pageable pageable);

    // ===============================
    // FEATURED AND SPECIAL PRODUCTS
    // ===============================
    
    @Query("SELECT p FROM Product p WHERE p.isFeatured = true AND p.status = 'PUBLISHED'")
    Page<Product> findFeaturedProducts(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.isBestseller = true AND p.status = 'PUBLISHED'")
    Page<Product> findBestsellerProducts(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.isNewArrival = true AND p.status = 'PUBLISHED'")
    Page<Product> findNewArrivals(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.isOnSale = true AND p.status = 'PUBLISHED'")
    Page<Product> findSaleProducts(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.mrp > p.unitPrice AND p.status = 'PUBLISHED'")
    Page<Product> findDiscountedProducts(Pageable pageable);

    // ===============================
    // ANALYTICS QUERIES
    // ===============================
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.status = :status")
    long countByStatus(@Param("status") Product.ProductStatus status);
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.vendor.id = :vendorId")
    long countByVendorId(@Param("vendorId") Long vendorId);
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category = :category")
    long countByCategory(@Param("category") String category);
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockStatus = :stockStatus")
    long countByStockStatus(@Param("stockStatus") Product.StockStatus stockStatus);
    
    @Query("SELECT p.category, COUNT(p) FROM Product p WHERE p.status = 'PUBLISHED' GROUP BY p.category ORDER BY COUNT(p) DESC")
    List<Object[]> getProductCountByCategory();
    
    @Query("SELECT p.brand, COUNT(p) FROM Product p WHERE p.status = 'PUBLISHED' GROUP BY p.brand ORDER BY COUNT(p) DESC")
    List<Object[]> getProductCountByBrand();
    
    @Query("SELECT p.stockStatus, COUNT(p) FROM Product p GROUP BY p.stockStatus")
    List<Object[]> getProductCountByStockStatus();
    
    // Top products by various metrics
    @Query("SELECT p FROM Product p WHERE p.status = 'PUBLISHED' ORDER BY p.viewCount DESC")
    Page<Product> findMostViewedProducts(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.status = 'PUBLISHED' ORDER BY p.totalSoldQuantity DESC")
    Page<Product> findMostSoldProducts(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.status = 'PUBLISHED' ORDER BY p.totalRevenue DESC")
    Page<Product> findHighestRevenueProducts(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.status = 'PUBLISHED' AND p.reviewCount > 0 ORDER BY p.averageRating DESC")
    Page<Product> findHighestRatedProducts(Pageable pageable);

    // ===============================
    // VENDOR SPECIFIC QUERIES
    // ===============================
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.vendor.id = :vendorId AND p.status = :status")
    long countByVendorIdAndStatus(@Param("vendorId") Long vendorId, @Param("status") Product.ProductStatus status);
    
    @Query("SELECT p FROM Product p WHERE p.vendor.id = :vendorId AND p.stockStatus = 'LOW_STOCK'")
    List<Product> findVendorLowStockProducts(@Param("vendorId") Long vendorId);
    
    @Query("SELECT SUM(p.availableQuantity * p.unitPrice) FROM Product p WHERE p.vendor.id = :vendorId AND p.status = 'PUBLISHED'")
    BigDecimal calculateVendorInventoryValue(@Param("vendorId") Long vendorId);

    // ===============================
    // VARIANT QUERIES
    // ===============================
    
    List<Product> findByParentProductId(Long parentProductId);
    
    @Query("SELECT p FROM Product p WHERE p.hasVariants = true AND p.parentProduct IS NULL")
    Page<Product> findParentProducts(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.parentProduct.id = :parentId AND p.status = 'PUBLISHED'")
    List<Product> findVariantsByParentId(@Param("parentId") Long parentId);

    // ===============================
    // DATE RANGE QUERIES
    // ===============================
    
    List<Product> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT p FROM Product p WHERE p.createdAt >= :startOfDay")
    List<Product> findProductsCreatedToday(@Param("startOfDay") LocalDateTime startOfDay);
    
    @Query("SELECT p FROM Product p WHERE p.createdAt >= :startOfWeek")
    List<Product> findProductsCreatedThisWeek(@Param("startOfWeek") LocalDateTime startOfWeek);
    
    @Query("SELECT p FROM Product p WHERE p.publishedAt BETWEEN :startDate AND :endDate")
    List<Product> findProductsPublishedBetween(@Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate);

    // ===============================
    // BULK OPERATIONS SUPPORT
    // ===============================
    
    @Query("SELECT p FROM Product p WHERE p.id IN :productIds")
    List<Product> findByIdIn(@Param("productIds") List<Long> productIds);
    
    @Query("SELECT p FROM Product p WHERE p.sku IN :skus")
    List<Product> findBySkuIn(@Param("skus") List<String> skus);

    // ===============================
    // BUSINESS INTELLIGENCE QUERIES
    // ===============================
    
    @Query("SELECT p FROM Product p WHERE " +
           "p.status = 'PUBLISHED' AND " +
           "p.stockStatus = 'IN_STOCK' AND " +
           "p.viewCount > :minViews AND " +
           "p.orderCount < :maxOrders")
    List<Product> findHighViewLowOrderProducts(@Param("minViews") Long minViews, 
                                               @Param("maxOrders") Long maxOrders);
    
    @Query("SELECT p FROM Product p WHERE " +
           "p.status = 'PUBLISHED' AND " +
           "p.lastOrderedAt < :cutoffDate")
    List<Product> findSlowMovingProducts(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    @Query("SELECT p FROM Product p WHERE " +
           "p.createdAt >= :recentDate AND " +
           "p.status = 'PUBLISHED' AND " +
           "p.orderCount > :minOrders")
    List<Product> findTrendingProducts(@Param("recentDate") LocalDateTime recentDate, 
                                       @Param("minOrders") Long minOrders);

    // ===============================
    // DASHBOARD QUERIES
    // ===============================
    
    @Query("SELECT " +
           "COUNT(p), " +
           "COUNT(CASE WHEN p.status = 'PUBLISHED' THEN 1 END), " +
           "COUNT(CASE WHEN p.stockStatus = 'IN_STOCK' THEN 1 END), " +
           "COUNT(CASE WHEN p.stockStatus = 'LOW_STOCK' THEN 1 END), " +
           "COUNT(CASE WHEN p.stockStatus = 'OUT_OF_STOCK' THEN 1 END) " +
           "FROM Product p")
    Object[] getProductDashboardStats();
    
    @Query("SELECT " +
           "SUM(p.availableQuantity * p.unitPrice), " +
           "AVG(p.unitPrice), " +
           "SUM(p.totalRevenue), " +
           "AVG(p.averageRating) " +
           "FROM Product p WHERE p.status = 'PUBLISHED'")
    Object[] getProductBusinessMetrics();

    // ===============================
    // RECOMMENDATIONS QUERIES
    // ===============================
    
    @Query("SELECT p FROM Product p WHERE " +
           "p.category = :category AND " +
           "p.id != :excludeId AND " +
           "p.status = 'PUBLISHED' " +
           "ORDER BY p.averageRating DESC, p.totalSoldQuantity DESC")
    Page<Product> findRecommendedProductsByCategory(@Param("category") String category, 
                                                    @Param("excludeId") Long excludeId, 
                                                    Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE " +
           "p.brand = :brand AND " +
           "p.id != :excludeId AND " +
           "p.status = 'PUBLISHED' " +
           "ORDER BY p.averageRating DESC")
    Page<Product> findRecommendedProductsByBrand(@Param("brand") String brand, 
                                                 @Param("excludeId") Long excludeId, 
                                                 Pageable pageable);

    // ===============================
    // SORTING AND ORDERING
    // ===============================
    
    Page<Product> findByStatusOrderByCreatedAtDesc(Product.ProductStatus status, Pageable pageable);
    Page<Product> findByStatusOrderByUnitPriceAsc(Product.ProductStatus status, Pageable pageable);
    Page<Product> findByStatusOrderByUnitPriceDesc(Product.ProductStatus status, Pageable pageable);
    Page<Product> findByStatusOrderByAverageRatingDesc(Product.ProductStatus status, Pageable pageable);
    Page<Product> findByStatusOrderByTotalSoldQuantityDesc(Product.ProductStatus status, Pageable pageable);
    Page<Product> findByStatusOrderByViewCountDesc(Product.ProductStatus status, Pageable pageable);
}

