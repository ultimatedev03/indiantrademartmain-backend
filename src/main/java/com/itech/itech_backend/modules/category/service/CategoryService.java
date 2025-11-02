package com.itech.itech_backend.modules.category.service;

import com.itech.itech_backend.modules.category.model.Category;
import com.itech.itech_backend.modules.category.repository.CategoryRepository;
import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    /**
     * Create a new category
     */
    @Transactional
    public Category createCategory(String name, String description, Long parentCategoryId, 
                                   Long employeeId, String iconUrl, Integer displayOrder,
                                   Double commissionPercentage, Boolean visibleToVendors,
                                   Boolean visibleToCustomers) {
        try {
            log.info("🆕 Creating new category: {}", name);

            // Validate category name uniqueness
            if (categoryRepository.existsByNameIgnoreCase(name)) {
                throw new IllegalArgumentException("Category with name '" + name + "' already exists");
            }

            // Get employee
            User employee = userRepository.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

            // Get parent category if specified
            Category parentCategory = null;
            if (parentCategoryId != null) {
                parentCategory = categoryRepository.findById(parentCategoryId)
                        .orElseThrow(() -> new RuntimeException("Parent category not found"));
            }

            // Create category
            Category category = Category.builder()
                    .name(name)
                    .description(description)
                    .parentCategory(parentCategory)
                    .createdByEmployee(employee)
                    .iconUrl(iconUrl)
                    .displayOrder(displayOrder != null ? displayOrder : 0)
                    .commissionPercentage(commissionPercentage != null ? commissionPercentage : 0.0)
                    .visibleToVendors(visibleToVendors != null ? visibleToVendors : true)
                    .visibleToCustomers(visibleToCustomers != null ? visibleToCustomers : true)
                    .isActive(true)
                    .build();

            Category savedCategory = categoryRepository.save(category);
            log.info("✅ Category created successfully: {} (ID: {})", savedCategory.getName(), savedCategory.getId());
            
            return savedCategory;

        } catch (Exception e) {
            log.error("❌ Error creating category: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create category: " + e.getMessage());
        }
    }

    /**
     * Get all categories with pagination
     */
    public Page<Category> getAllCategories(Pageable pageable) {
        log.info("📋 Getting all categories with pagination");
        return categoryRepository.findAll(pageable);
    }

    /**
     * Get active categories only
     */
    public List<Category> getActiveCategories() {
        log.info("📋 Getting active categories");
        return categoryRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
    }

    /**
     * Get root categories (top level categories)
     */
    public List<Category> getRootCategories() {
        log.info("🌳 Getting root categories");
        return categoryRepository.findByParentCategoryIsNullAndIsActiveTrueOrderByDisplayOrderAsc();
    }

    /**
     * Get subcategories for a parent category
     */
    public List<Category> getSubcategories(Long parentCategoryId) {
        log.info("📂 Getting subcategories for parent ID: {}", parentCategoryId);
        return categoryRepository.findByParentCategoryIdAndIsActiveTrueOrderByDisplayOrderAsc(parentCategoryId);
    }

    /**
     * Get categories visible to vendors
     */
    public List<Category> getVendorVisibleCategories() {
        log.info("🏪 Getting vendor-visible categories");
        return categoryRepository.findByVisibleToVendorsTrueAndIsActiveTrueOrderByDisplayOrderAsc();
    }

    /**
     * Get categories visible to customers
     */
    public List<Category> getCustomerVisibleCategories() {
        log.info("👥 Getting customer-visible categories");
        return categoryRepository.findByVisibleToCustomersTrueAndIsActiveTrueOrderByDisplayOrderAsc();
    }

    /**
     * Get category hierarchy with nested structure
     */
    public List<Category> getCategoryHierarchy() {
        log.info("🌲 Building category hierarchy");
        return categoryRepository.findCategoryHierarchy();
    }

    /**
     * Get category by ID
     */
    public Category getCategoryById(Long id) {
        log.info("🔍 Getting category by ID: {}", id);
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + id));
    }

    /**
     * Get category by slug
     */
    public Category getCategoryBySlug(String slug) {
        log.info("🔍 Getting category by slug: {}", slug);
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Category not found with slug: " + slug));
    }

    /**
     * Search categories by name
     */
    public List<Category> searchCategories(String searchTerm) {
        log.info("🔎 Searching categories with term: {}", searchTerm);
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getActiveCategories();
        }
        return categoryRepository.findByNameContainingIgnoreCase(searchTerm.trim());
    }

    /**
     * Get categories created by specific employee
     */
    public List<Category> getCategoriesByEmployee(Long employeeId) {
        log.info("👤 Getting categories created by employee ID: {}", employeeId);
        return categoryRepository.findByCreatedByEmployeeIdOrderByCreatedAtDesc(employeeId);
    }

    /**
     * Update category
     */
    @Transactional
    public Category updateCategory(Long id, Map<String, Object> updates) {
        try {
            log.info("🔄 Updating category ID: {} with updates: {}", id, updates);

            Category category = getCategoryById(id);
            
            // Update name (with uniqueness check)
            if (updates.containsKey("name")) {
                String newName = (String) updates.get("name");
                if (!category.getName().equalsIgnoreCase(newName) && 
                    categoryRepository.existsByNameIgnoreCaseAndIdNot(newName, id)) {
                    throw new IllegalArgumentException("Category with name '" + newName + "' already exists");
                }
                category.setName(newName);
            }

            // Update description
            if (updates.containsKey("description")) {
                category.setDescription((String) updates.get("description"));
            }

            // Update display order
            if (updates.containsKey("displayOrder")) {
                Integer displayOrder = (Integer) updates.get("displayOrder");
                category.setDisplayOrder(displayOrder);
            }

            // Update icon URL
            if (updates.containsKey("iconUrl")) {
                category.setIconUrl((String) updates.get("iconUrl"));
            }

            // Update commission percentage
            if (updates.containsKey("commissionPercentage")) {
                Double commission = (Double) updates.get("commissionPercentage");
                category.setCommissionPercentage(commission);
            }

            // Update visibility flags
            if (updates.containsKey("visibleToVendors")) {
                Boolean visibleToVendors = (Boolean) updates.get("visibleToVendors");
                category.setVisibleToVendors(visibleToVendors);
            }

            if (updates.containsKey("visibleToCustomers")) {
                Boolean visibleToCustomers = (Boolean) updates.get("visibleToCustomers");
                category.setVisibleToCustomers(visibleToCustomers);
            }

            // Update active status
            if (updates.containsKey("isActive")) {
                Boolean isActive = (Boolean) updates.get("isActive");
                category.setIsActive(isActive);
            }

            // Update parent category
            if (updates.containsKey("parentCategoryId")) {
                Object parentIdObj = updates.get("parentCategoryId");
                if (parentIdObj == null) {
                    category.setParentCategory(null);
                } else {
                    Long parentId = Long.valueOf(parentIdObj.toString());
                    if (parentId.equals(id)) {
                        throw new IllegalArgumentException("Category cannot be its own parent");
                    }
                    Category parentCategory = getCategoryById(parentId);
                    category.setParentCategory(parentCategory);
                }
            }

            Category updatedCategory = categoryRepository.save(category);
            log.info("✅ Category updated successfully: {}", updatedCategory.getName());
            
            return updatedCategory;

        } catch (Exception e) {
            log.error("❌ Error updating category: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update category: " + e.getMessage());
        }
    }

    /**
     * Update category visibility
     */
    @Transactional
    public Category updateCategoryVisibility(Long id, Boolean visibleToVendors, Boolean visibleToCustomers) {
        log.info("👁️ Updating category visibility - ID: {}, Vendors: {}, Customers: {}", 
                id, visibleToVendors, visibleToCustomers);
        
        Map<String, Object> updates = new HashMap<>();
        if (visibleToVendors != null) {
            updates.put("visibleToVendors", visibleToVendors);
        }
        if (visibleToCustomers != null) {
            updates.put("visibleToCustomers", visibleToCustomers);
        }
        
        return updateCategory(id, updates);
    }

    /**
     * Activate/Deactivate category
     */
    @Transactional
    public Category toggleCategoryStatus(Long id, Boolean isActive) {
        log.info("🔄 Toggling category status - ID: {}, Active: {}", id, isActive);
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("isActive", isActive);
        
        return updateCategory(id, updates);
    }

    /**
     * Delete category (soft delete by setting isActive to false)
     */
    @Transactional
    public void deleteCategory(Long id) {
        try {
            log.info("🗑️ Soft deleting category ID: {}", id);

            Category category = getCategoryById(id);
            
            // Check if category has subcategories
            Long subcategoryCount = categoryRepository.countSubcategoriesByParentId(id);
            if (subcategoryCount > 0) {
                throw new IllegalStateException("Cannot delete category with subcategories. Please remove or reassign subcategories first.");
            }

            // TODO: Check if category has products associated
            // This would require a ProductRepository query

            category.setIsActive(false);
            categoryRepository.save(category);
            
            log.info("✅ Category soft deleted successfully: {}", category.getName());

        } catch (Exception e) {
            log.error("❌ Error deleting category: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete category: " + e.getMessage());
        }
    }

    /**
     * Get category statistics
     */
    public Map<String, Object> getCategoryStatistics() {
        log.info("📊 Generating category statistics");
        
        Map<String, Object> stats = new HashMap<>();
        
        long totalCategories = categoryRepository.count();
        long activeCategories = categoryRepository.findByIsActiveTrueOrderByDisplayOrderAsc().size();
        long rootCategories = categoryRepository.findByParentCategoryIsNullAndIsActiveTrueOrderByDisplayOrderAsc().size();
        long vendorVisibleCategories = categoryRepository.findByVisibleToVendorsTrueAndIsActiveTrueOrderByDisplayOrderAsc().size();
        long customerVisibleCategories = categoryRepository.findByVisibleToCustomersTrueAndIsActiveTrueOrderByDisplayOrderAsc().size();
        
        stats.put("totalCategories", totalCategories);
        stats.put("activeCategories", activeCategories);
        stats.put("inactiveCategories", totalCategories - activeCategories);
        stats.put("rootCategories", rootCategories);
        stats.put("subcategories", activeCategories - rootCategories);
        stats.put("vendorVisibleCategories", vendorVisibleCategories);
        stats.put("customerVisibleCategories", customerVisibleCategories);
        
        return stats;
    }

    /**
     * Get popular categories (top categories by product count)
     */
    public List<Category> getPopularCategories(int limit) {
        log.info("🔥 Getting popular categories (limit: {})", limit);
        return categoryRepository.findPopularCategories(limit);
    }

    /**
     * Get category path (breadcrumb trail)
     */
    public List<Category> getCategoryPath(Long categoryId) {
        log.info("🗂️ Getting category path for ID: {}", categoryId);
        
        List<Long> pathIds = new ArrayList<>();
        Category current = getCategoryById(categoryId);
        
        while (current != null) {
            pathIds.add(0, current.getId());
            current = current.getParentCategory();
        }
        
        return categoryRepository.findCategoryPath(pathIds);
    }

    /**
     * Bulk update category visibility
     */
    @Transactional
    public void bulkUpdateVisibility(List<Long> categoryIds, Boolean visibleToVendors, Boolean visibleToCustomers) {
        log.info("📦 Bulk updating category visibility for {} categories", categoryIds.size());
        
        for (Long categoryId : categoryIds) {
            try {
                updateCategoryVisibility(categoryId, visibleToVendors, visibleToCustomers);
            } catch (Exception e) {
                log.error("Failed to update visibility for category {}: {}", categoryId, e.getMessage());
            }
        }
    }

    /**
     * Get filtered categories with search and pagination
     */
    public Page<Category> getFilteredCategories(String search, Boolean isActive, Boolean visibleToVendors, 
                                                Boolean visibleToCustomers, Long parentCategoryId, Pageable pageable) {
        log.info("🔍 Getting filtered categories - Search: {}, Active: {}, VendorVisible: {}, CustomerVisible: {}", 
                search, isActive, visibleToVendors, visibleToCustomers);
        
        List<Category> allCategories = categoryRepository.findAll();
        
        // Apply filters
        List<Category> filteredCategories = allCategories.stream()
                .filter(category -> {
                    // Search filter
                    if (search != null && !search.trim().isEmpty()) {
                        String searchLower = search.toLowerCase();
                        if (!category.getName().toLowerCase().contains(searchLower) &&
                            (category.getDescription() == null || 
                             !category.getDescription().toLowerCase().contains(searchLower))) {
                            return false;
                        }
                    }
                    
                    // Active filter
                    if (isActive != null && !category.getIsActive().equals(isActive)) {
                        return false;
                    }
                    
                    // Vendor visibility filter
                    if (visibleToVendors != null && !category.getVisibleToVendors().equals(visibleToVendors)) {
                        return false;
                    }
                    
                    // Customer visibility filter
                    if (visibleToCustomers != null && !category.getVisibleToCustomers().equals(visibleToCustomers)) {
                        return false;
                    }
                    
                    // Parent category filter
                    if (parentCategoryId != null) {
                        if (category.getParentCategory() == null) {
                            return parentCategoryId == 0; // 0 means root categories
                        } else {
                            return category.getParentCategory().getId().equals(parentCategoryId);
                        }
                    }
                    
                    return true;
                })
                .sorted(Comparator.comparing(Category::getDisplayOrder)
                        .thenComparing(Category::getName))
                .collect(Collectors.toList());
        
        // Apply pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredCategories.size());
        
        List<Category> pageContent = filteredCategories.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, filteredCategories.size());
    }
}
