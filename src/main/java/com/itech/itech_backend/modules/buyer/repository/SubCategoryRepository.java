package com.itech.itech_backend.modules.buyer.repository;

import com.itech.itech_backend.modules.buyer.model.SubCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    List<SubCategory> findByCategoryId(Long categoryId);
    boolean existsByName(String name);
    
    Page<SubCategory> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        String name, String description, Pageable pageable);
    
    Page<SubCategory> findByCategoryIdAndNameContainingIgnoreCase(
        Long categoryId, String name, Pageable pageable);
    
    List<SubCategory> findByCategoryIdAndIsActiveTrue(Long categoryId);
    
    @Query("SELECT sc FROM BuyerSubCategory sc WHERE sc.category.id = :categoryId ORDER BY sc.displayOrder ASC, sc.name ASC")
    List<SubCategory> findByCategoryIdOrderByDisplayOrder(@Param("categoryId") Long categoryId);
    
    @Query("SELECT COUNT(mc) FROM BuyerMicroCategory mc WHERE mc.subCategory.id = :subCategoryId")
    long countMicroCategoriesBySubCategoryId(@Param("subCategoryId") Long subCategoryId);
    
    @Query("SELECT COUNT(p) FROM BuyerProduct p JOIN p.microCategory mc WHERE mc.subCategory.id = :subCategoryId")
    long countProductsBySubCategoryId(@Param("subCategoryId") Long subCategoryId);
    
    boolean existsByNameAndCategoryId(String name, Long categoryId);
    
    // Additional count methods needed by DataEntryService
    @Query("SELECT COUNT(sc) FROM BuyerSubCategory sc WHERE sc.category.id = :categoryId")
    long countByCategoryId(@Param("categoryId") Long categoryId);
}

