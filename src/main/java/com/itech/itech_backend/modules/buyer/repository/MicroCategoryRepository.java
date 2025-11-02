package com.itech.itech_backend.modules.buyer.repository;

import com.itech.itech_backend.modules.buyer.model.MicroCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MicroCategoryRepository extends JpaRepository<MicroCategory, Long> {
    List<MicroCategory> findBySubCategoryId(Long subCategoryId);
    boolean existsByName(String name);
    
    Page<MicroCategory> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        String name, String description, Pageable pageable);
    
    Page<MicroCategory> findBySubCategoryIdAndNameContainingIgnoreCase(
        Long subCategoryId, String name, Pageable pageable);
    
    List<MicroCategory> findBySubCategoryIdAndIsActiveTrue(Long subCategoryId);
    
    @Query("SELECT mc FROM BuyerMicroCategory mc WHERE mc.subCategory.id = :subCategoryId ORDER BY mc.displayOrder ASC, mc.name ASC")
    List<MicroCategory> findBySubCategoryIdOrderByDisplayOrder(@Param("subCategoryId") Long subCategoryId);
    
    @Query("SELECT COUNT(p) FROM BuyerProduct p WHERE p.microCategory.id = :microCategoryId")
    long countProductsByMicroCategoryId(@Param("microCategoryId") Long microCategoryId);
    
    boolean existsByNameAndSubCategoryId(String name, Long subCategoryId);
    
    @Query("SELECT mc FROM BuyerMicroCategory mc WHERE mc.subCategory.category.id = :categoryId")
    List<MicroCategory> findByCategoryId(@Param("categoryId") Long categoryId);
    
    // Additional count methods needed by DataEntryService
    @Query("SELECT COUNT(mc) FROM BuyerMicroCategory mc WHERE mc.subCategory.category.id = :categoryId")
    long countByCategoryId(@Param("categoryId") Long categoryId);
}

