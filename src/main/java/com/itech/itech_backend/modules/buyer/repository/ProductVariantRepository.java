package com.itech.itech_backend.modules.buyer.repository;

import com.itech.itech_backend.modules.buyer.model.Product;
import com.itech.itech_backend.modules.buyer.model.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    
    // Find variants for a product
    List<ProductVariant> findByProductOrderByIsDefaultDescCreatedAtAsc(Product product);
    List<ProductVariant> findByProductIdOrderByIsDefaultDescCreatedAtAsc(Long productId);
    
    // Find active variants
    List<ProductVariant> findByProductIdAndIsActiveTrueOrderByIsDefaultDescCreatedAtAsc(Long productId);
    
    // Find default variant
    Optional<ProductVariant> findByProductIdAndIsDefaultTrue(Long productId);
    
    // Find variants by attributes
    List<ProductVariant> findByProductIdAndColor(Long productId, String color);
    List<ProductVariant> findByProductIdAndSize(Long productId, String size);
    List<ProductVariant> findByProductIdAndColorAndSize(Long productId, String color, String size);
    
    // Find variants with stock
    @Query("SELECT v FROM ProductVariant v WHERE v.product.id = :productId AND v.stock > 0 AND v.isActive = true")
    List<ProductVariant> findInStockVariantsByProductId(@Param("productId") Long productId);
    
    // Count variants
    long countByProductId(Long productId);
    long countByProductIdAndIsActiveTrue(Long productId);
    
    // Find by SKU
    Optional<ProductVariant> findBySku(String sku);
    boolean existsBySku(String sku);
    
    // Price queries
    @Query("SELECT MIN(v.price), MAX(v.price) FROM ProductVariant v WHERE v.product.id = :productId AND v.isActive = true")
    Object[] findPriceRangeByProductId(@Param("productId") Long productId);
    
    // Advanced search with attributes
    @Query("SELECT v FROM ProductVariant v WHERE " +
           "(:productId IS NULL OR v.product.id = :productId) AND " +
           "(:color IS NULL OR v.color ILIKE %:color%) AND " +
           "(:size IS NULL OR v.size ILIKE %:size%) AND " +
           "(:material IS NULL OR v.material ILIKE %:material%) AND " +
           "(:minPrice IS NULL OR v.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR v.price <= :maxPrice) AND " +
           "(:inStock IS NULL OR (:inStock = true AND v.stock > 0) OR (:inStock = false AND v.stock <= 0)) AND " +
           "v.isActive = true")
    Page<ProductVariant> searchVariants(@Param("productId") Long productId,
                                       @Param("color") String color,
                                       @Param("size") String size,
                                       @Param("material") String material,
                                       @Param("minPrice") Double minPrice,
                                       @Param("maxPrice") Double maxPrice,
                                       @Param("inStock") Boolean inStock,
                                       Pageable pageable);
    
    // Get distinct attribute values for filtering
    @Query("SELECT DISTINCT v.color FROM ProductVariant v WHERE v.color IS NOT NULL AND v.isActive = true ORDER BY v.color")
    List<String> findDistinctColors();
    
    @Query("SELECT DISTINCT v.size FROM ProductVariant v WHERE v.size IS NOT NULL AND v.isActive = true ORDER BY v.size")
    List<String> findDistinctSizes();
    
    @Query("SELECT DISTINCT v.material FROM ProductVariant v WHERE v.material IS NOT NULL AND v.isActive = true ORDER BY v.material")
    List<String> findDistinctMaterials();
    
    @Query("SELECT DISTINCT v.style FROM ProductVariant v WHERE v.style IS NOT NULL AND v.isActive = true ORDER BY v.style")
    List<String> findDistinctStyles();
    
    // Analytics queries
    @Query("SELECT v FROM ProductVariant v WHERE v.product.id = :productId ORDER BY v.orderCount DESC")
    List<ProductVariant> findTopSellingVariantsByProduct(@Param("productId") Long productId, Pageable pageable);
    
    @Query("SELECT v FROM ProductVariant v WHERE v.product.id = :productId ORDER BY v.viewCount DESC")
    List<ProductVariant> findMostViewedVariantsByProduct(@Param("productId") Long productId, Pageable pageable);
}

