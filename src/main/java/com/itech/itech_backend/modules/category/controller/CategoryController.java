package com.itech.itech_backend.modules.category.controller;

import com.itech.itech_backend.modules.category.model.Category;
import com.itech.itech_backend.modules.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Create a new category (Employee only)
     */
    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createCategory(@RequestBody Map<String, Object> requestBody) {
        try {
            log.info("🆕 Create category request: {}", requestBody);

            String name = (String) requestBody.get("name");
            String description = (String) requestBody.get("description");
            Long parentCategoryId = requestBody.get("parentCategoryId") != null ? 
                Long.valueOf(requestBody.get("parentCategoryId").toString()) : null;
            Long employeeId = requestBody.get("employeeId") != null ? 
                Long.valueOf(requestBody.get("employeeId").toString()) : 1L; // Default to admin ID
            String iconUrl = (String) requestBody.get("iconUrl");
            Integer displayOrder = requestBody.get("displayOrder") != null ? 
                Integer.valueOf(requestBody.get("displayOrder").toString()) : 0;
            Double commissionPercentage = requestBody.get("commissionPercentage") != null ? 
                Double.valueOf(requestBody.get("commissionPercentage").toString()) : 0.0;
            Boolean visibleToVendors = requestBody.get("visibleToVendors") != null ? 
                (Boolean) requestBody.get("visibleToVendors") : true;
            Boolean visibleToCustomers = requestBody.get("visibleToCustomers") != null ? 
                (Boolean) requestBody.get("visibleToCustomers") : true;

            Category category = categoryService.createCategory(
                name, description, parentCategoryId, employeeId, iconUrl, 
                displayOrder, commissionPercentage, visibleToVendors, visibleToCustomers
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Category created successfully");
            response.put("data", category);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("❌ Error creating category: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to create category: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Get all categories with pagination and filtering
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Boolean visibleToVendors,
            @RequestParam(required = false) Boolean visibleToCustomers,
            @RequestParam(required = false) Long parentCategoryId) {
        try {
            log.info("📋 Get categories request - Page: {}, Size: {}, Search: {}", page, size, search);

            Pageable pageable = PageRequest.of(page, size);
            Page<Category> categories = categoryService.getFilteredCategories(
                search, isActive, visibleToVendors, visibleToCustomers, parentCategoryId, pageable
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Categories retrieved successfully");
            response.put("data", categories.getContent());
            response.put("pagination", Map.of(
                "currentPage", categories.getNumber(),
                "totalPages", categories.getTotalPages(),
                "totalElements", categories.getTotalElements(),
                "size", categories.getSize()
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting categories: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to get categories: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get active categories (for dropdowns)
     */
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveCategories() {
        try {
            log.info("📋 Get active categories request");

            List<Category> categories = categoryService.getActiveCategories();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Active categories retrieved successfully");
            response.put("data", categories);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting active categories: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to get active categories: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get root categories (top level categories)
     */
    @GetMapping("/root")
    public ResponseEntity<Map<String, Object>> getRootCategories() {
        try {
            log.info("🌳 Get root categories request");

            List<Category> categories = categoryService.getRootCategories();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Root categories retrieved successfully");
            response.put("data", categories);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting root categories: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to get root categories: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get categories visible to vendors
     */
    @GetMapping("/vendor-visible")
    public ResponseEntity<Map<String, Object>> getVendorVisibleCategories() {
        try {
            log.info("🏪 Get vendor-visible categories request");

            List<Category> categories = categoryService.getVendorVisibleCategories();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Vendor-visible categories retrieved successfully");
            response.put("data", categories);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting vendor-visible categories: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to get vendor-visible categories: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get categories visible to customers
     */
    @GetMapping("/customer-visible")
    public ResponseEntity<Map<String, Object>> getCustomerVisibleCategories() {
        try {
            log.info("👥 Get customer-visible categories request");

            List<Category> categories = categoryService.getCustomerVisibleCategories();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Customer-visible categories retrieved successfully");
            response.put("data", categories);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting customer-visible categories: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to get customer-visible categories: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get category hierarchy
     */
    @GetMapping("/hierarchy")
    public ResponseEntity<Map<String, Object>> getCategoryHierarchy() {
        try {
            log.info("🌲 Get category hierarchy request");

            List<Category> categories = categoryService.getCategoryHierarchy();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Category hierarchy retrieved successfully");
            response.put("data", categories);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting category hierarchy: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to get category hierarchy: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get subcategories for a parent category
     */
    @GetMapping("/{parentId}/subcategories")
    public ResponseEntity<Map<String, Object>> getSubcategories(@PathVariable Long parentId) {
        try {
            log.info("📂 Get subcategories request for parent ID: {}", parentId);

            List<Category> subcategories = categoryService.getSubcategories(parentId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Subcategories retrieved successfully");
            response.put("data", subcategories);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting subcategories: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to get subcategories: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get category by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCategoryById(@PathVariable Long id) {
        try {
            log.info("🔍 Get category by ID request: {}", id);

            Category category = categoryService.getCategoryById(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Category retrieved successfully");
            response.put("data", category);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting category by ID: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to get category: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    /**
     * Update category (Employee only)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateCategory(
            @PathVariable Long id, 
            @RequestBody Map<String, Object> updates) {
        try {
            log.info("🔄 Update category request - ID: {}, Updates: {}", id, updates);

            Category category = categoryService.updateCategory(id, updates);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Category updated successfully");
            response.put("data", category);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error updating category: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to update category: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Update category visibility (Employee only)
     */
    @PatchMapping("/{id}/visibility")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateCategoryVisibility(
            @PathVariable Long id,
            @RequestBody Map<String, Object> visibilityUpdates) {
        try {
            log.info("👁️ Update category visibility request - ID: {}, Updates: {}", id, visibilityUpdates);

            Boolean visibleToVendors = (Boolean) visibilityUpdates.get("visibleToVendors");
            Boolean visibleToCustomers = (Boolean) visibilityUpdates.get("visibleToCustomers");

            Category category = categoryService.updateCategoryVisibility(id, visibleToVendors, visibleToCustomers);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Category visibility updated successfully");
            response.put("data", category);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error updating category visibility: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to update category visibility: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Toggle category status (Employee only)
     */
    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> toggleCategoryStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Object> statusUpdate) {
        try {
            log.info("🔄 Toggle category status request - ID: {}", id);

            Boolean isActive = (Boolean) statusUpdate.get("isActive");
            Category category = categoryService.toggleCategoryStatus(id, isActive);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Category status updated successfully");
            response.put("data", category);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error toggling category status: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to update category status: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Delete category (Employee only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteCategory(@PathVariable Long id) {
        try {
            log.info("🗑️ Delete category request - ID: {}", id);

            categoryService.deleteCategory(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Category deleted successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error deleting category: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to delete category: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Search categories
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchCategories(@RequestParam String query) {
        try {
            log.info("🔎 Search categories request - Query: {}", query);

            List<Category> categories = categoryService.searchCategories(query);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Categories search completed");
            response.put("data", categories);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error searching categories: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to search categories: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get category statistics (Employee/Admin only)
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getCategoryStatistics() {
        try {
            log.info("📊 Get category statistics request");

            Map<String, Object> stats = categoryService.getCategoryStatistics();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Category statistics retrieved successfully");
            response.put("data", stats);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting category statistics: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to get category statistics: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get popular categories
     */
    @GetMapping("/popular")
    public ResponseEntity<Map<String, Object>> getPopularCategories(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            log.info("🔥 Get popular categories request - Limit: {}", limit);

            List<Category> categories = categoryService.getPopularCategories(limit);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Popular categories retrieved successfully");
            response.put("data", categories);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting popular categories: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to get popular categories: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get category path (breadcrumb)
     */
    @GetMapping("/{id}/path")
    public ResponseEntity<Map<String, Object>> getCategoryPath(@PathVariable Long id) {
        try {
            log.info("🗂️ Get category path request - ID: {}", id);

            List<Category> path = categoryService.getCategoryPath(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Category path retrieved successfully");
            response.put("data", path);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting category path: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to get category path: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    /**
     * Bulk update category visibility (Employee only)
     */
    @PatchMapping("/bulk/visibility")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> bulkUpdateVisibility(@RequestBody Map<String, Object> requestBody) {
        try {
            log.info("📦 Bulk update visibility request: {}", requestBody);

            @SuppressWarnings("unchecked")
            List<Long> categoryIds = (List<Long>) requestBody.get("categoryIds");
            Boolean visibleToVendors = (Boolean) requestBody.get("visibleToVendors");
            Boolean visibleToCustomers = (Boolean) requestBody.get("visibleToCustomers");

            categoryService.bulkUpdateVisibility(categoryIds, visibleToVendors, visibleToCustomers);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Category visibility updated in bulk successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error bulk updating visibility: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to bulk update visibility: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
