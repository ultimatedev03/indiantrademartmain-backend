package com.itech.itech_backend.modules.category.repository;

import com.itech.itech_backend.modules.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    // Find by name
    Optional<Category> findByName(String name);
    
    // Find by slug
    Optional<Category> findBySlug(String slug);
    
    // Find active categories
    List<Category> findByIsActiveTrueOrderByDisplayOrderAsc();
    
    // Find root categories (no parent)
    List<Category> findByParentCategoryIsNullAndIsActiveTrueOrderByDisplayOrderAsc();
    
    // Find subcategories of a parent
    List<Category> findByParentCategoryIdAndIsActiveTrueOrderByDisplayOrderAsc(Long parentCategoryId);
    
    // Find categories visible to vendors
    List<Category> findByVisibleToVendorsTrueAndIsActiveTrueOrderByDisplayOrderAsc();
    
    // Find categories visible to customers
    List<Category> findByVisibleToCustomersTrueAndIsActiveTrueOrderByDisplayOrderAsc();
    
    // Find categories created by specific employee
    List<Category> findByCreatedByEmployeeIdOrderByCreatedAtDesc(Long employeeId);
    
    // Search categories by name (case-insensitive)
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) AND c.isActive = true ORDER BY c.displayOrder ASC")
    List<Category> findByNameContainingIgnoreCase(@Param("name") String name);
    
    // Get category hierarchy (all categories with their subcategories)
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.subCategories WHERE c.parentCategory IS NULL AND c.isActive = true ORDER BY c.displayOrder ASC")
    List<Category> findCategoryHierarchy();
    
    // Find categories by level
    List<Category> findByCategoryLevelAndIsActiveTrueOrderByDisplayOrderAsc(Integer level);
    
    // Count subcategories for a parent category
    @Query("SELECT COUNT(c) FROM Category c WHERE c.parentCategory.id = :parentId AND c.isActive = true")
    Long countSubcategoriesByParentId(@Param("parentId") Long parentId);
    
    // Check if category name exists (excluding specific ID for updates)
    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE LOWER(c.name) = LOWER(:name) AND c.id != :excludeId")
    boolean existsByNameIgnoreCaseAndIdNot(@Param("name") String name, @Param("excludeId") Long excludeId);
    
    // Check if category name exists
    boolean existsByNameIgnoreCase(String name);
    
    // Find categories for vendor dropdown (visible to vendors)
    @Query("SELECT c FROM Category c WHERE c.visibleToVendors = true AND c.isActive = true ORDER BY c.categoryLevel ASC, c.displayOrder ASC")
    List<Category> findVendorVisibleCategories();
    
    // Get popular categories (based on product count - will need to join with products table)
    @Query(value = "SELECT c.* FROM categories c LEFT JOIN products p ON c.id = p.category_id " +
                   "WHERE c.is_active = true AND c.visible_to_customers = true " +
                   "GROUP BY c.id ORDER BY COUNT(p.id) DESC LIMIT :limit", nativeQuery = true)
    List<Category> findPopularCategories(@Param("limit") int limit);
    
    // Get category path (for breadcrumbs)
    @Query("SELECT c FROM Category c WHERE c.id IN :categoryIds ORDER BY c.categoryLevel ASC")
    List<Category> findCategoryPath(@Param("categoryIds") List<Long> categoryIds);
}
