package com.itech.itech_backend.modules.buyer.repository;

import com.itech.itech_backend.modules.buyer.model.Product;
import com.itech.itech_backend.modules.buyer.model.ProductAttribute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {

    // Basic finders
    Optional<ProductAttribute> findByName(String name);
    Optional<ProductAttribute> findByDisplayName(String displayName);
    List<ProductAttribute> findByIsActiveTrueOrderByDisplayOrderAsc();

    // Find attributes for filtering
    List<ProductAttribute> findByIsActiveTrueAndIsFilterableTrueOrderByDisplayOrderAsc();
    List<ProductAttribute> findByCategoryIdAndIsActiveTrue(Long categoryId);

    // Find attributes used for variants
    List<ProductAttribute> findByIsVariantAttributeTrueAndIsActiveTrueOrderByDisplayOrderAsc();

    // Count methods
    long countByIsActiveTrue();
    long countByIsVariantAttributeTrueAndIsActiveTrue();

    // Attribute with possible values (e.g., select/multi-select)
    @Query("SELECT pa FROM ProductAttribute pa WHERE pa.possibleValues IS NOT NULL AND pa.isActive = true")
    List<ProductAttribute> findAttributesWithPossibleValues();

    // Custom queries
    @Query("SELECT pa.name, pa.possibleValues FROM ProductAttribute pa WHERE pa.isActive = true")
    List<Map<String, Object>> findPossibleValuesForActiveAttributes();

    @Query("SELECT pa FROM ProductAttribute pa WHERE pa.isVariantAttribute = true AND pa.category.id = :categoryId AND pa.isActive = true")
    List<ProductAttribute> findVariantAttributesByCategory(@Param("categoryId") Long categoryId);

    @Query("SELECT pa FROM ProductAttribute pa WHERE " +
           "(:categoryId IS NULL OR pa.category.id = :categoryId) AND " +
           "pa.isRequired = true AND pa.isActive = true")
    List<ProductAttribute> findRequiredAttributes(@Param("categoryId") Long categoryId);

    // Pagination
    Page<ProductAttribute> findByIsActiveTrue(Pageable pageable);
}

