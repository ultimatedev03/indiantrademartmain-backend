package com.itech.itech_backend.modules.admin.controller;

import com.itech.itech_backend.modules.shared.model.*;
import com.itech.itech_backend.modules.buyer.repository.BuyerProductRepository;
import com.itech.itech_backend.modules.buyer.repository.BuyerCategoryRepository;
import com.itech.itech_backend.modules.buyer.repository.SubCategoryRepository;
import com.itech.itech_backend.modules.buyer.repository.MicroCategoryRepository;
import com.itech.itech_backend.modules.buyer.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryManagementController {

    private final BuyerCategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final MicroCategoryRepository microCategoryRepository;
    private final BuyerProductRepository productRepository;

    // Category Management
@GetMapping
    public ResponseEntity<Page<Category>> getAllCategories(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Category> categories = categoryRepository.findAll(pageable);
    return ResponseEntity.ok(categories);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> createCategory(@RequestBody CategoryRequest request) {
        try {
            Category category = Category.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .displayOrder(request.getDisplayOrder())
                    .isActive(true)
                    .build();
            
            Category savedCategory = categoryRepository.save(category);
            return ResponseEntity.ok(savedCategory);
        } catch (Exception e) {
            log.error("Error creating category", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> updateCategory(
            @PathVariable Long categoryId, 
            @RequestBody CategoryRequest request) {
        try {
            Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
            if (categoryOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Category category = categoryOpt.get();
            category.setName(request.getName());
            category.setDescription(request.getDescription());
            category.setDisplayOrder(request.getDisplayOrder());
            category.setActive(request.isActive());

            Category savedCategory = categoryRepository.save(category);
            return ResponseEntity.ok(savedCategory);
        } catch (Exception e) {
            log.error("Error updating category", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable Long categoryId) {
        try {
            // Check if category has subcategories
            List<SubCategory> subCategories = subCategoryRepository.findByCategoryIdOrderByDisplayOrder(categoryId);
            if (!subCategories.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "Cannot delete category. It has " + subCategories.size() + " subcategories. Delete them first.")
                );
            }
            
            categoryRepository.deleteById(categoryId);
            return ResponseEntity.ok(Map.of("message", "Category deleted successfully"));
        } catch (Exception e) {
            log.error("Error deleting category", e);
            return ResponseEntity.badRequest().body(
                Map.of("error", "Failed to delete category: " + e.getMessage())
            );
        }
    }

    // SubCategory Management
    @GetMapping("/{categoryId}/subcategories")
    public ResponseEntity<List<SubCategory>> getSubCategories(@PathVariable Long categoryId) {
        List<SubCategory> subCategories = subCategoryRepository.findByCategoryIdOrderByDisplayOrder(categoryId);
        return ResponseEntity.ok(subCategories);
    }

    @PostMapping("/{categoryId}/subcategories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubCategory> createSubCategory(
            @PathVariable Long categoryId,
            @RequestBody SubCategoryRequest request) {
        try {
            Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
            if (categoryOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            SubCategory subCategory = SubCategory.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .displayOrder(request.getDisplayOrder())
                    .category(categoryOpt.get())
                    .isActive(true)
                    .build();

            SubCategory savedSubCategory = subCategoryRepository.save(subCategory);
            return ResponseEntity.ok(savedSubCategory);
        } catch (Exception e) {
            log.error("Error creating subcategory", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/subcategories/{subCategoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubCategory> updateSubCategory(
            @PathVariable Long subCategoryId,
            @RequestBody SubCategoryRequest request) {
        try {
            Optional<SubCategory> subCategoryOpt = subCategoryRepository.findById(subCategoryId);
            if (subCategoryOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            SubCategory subCategory = subCategoryOpt.get();
            subCategory.setName(request.getName());
            subCategory.setDescription(request.getDescription());
            subCategory.setDisplayOrder(request.getDisplayOrder());
            subCategory.setActive(request.isActive());

            SubCategory savedSubCategory = subCategoryRepository.save(subCategory);
            return ResponseEntity.ok(savedSubCategory);
        } catch (Exception e) {
            log.error("Error updating subcategory", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/subcategories/{subCategoryId}")
    public ResponseEntity<Map<String, String>> deleteSubCategory(@PathVariable Long subCategoryId) {
        try {
            // Check if subcategory has microcategories
            List<MicroCategory> microCategories = microCategoryRepository.findBySubCategoryIdOrderByDisplayOrder(subCategoryId);
            if (!microCategories.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "Cannot delete subcategory. It has " + microCategories.size() + " microcategories. Delete them first.")
                );
            }
            
            subCategoryRepository.deleteById(subCategoryId);
            return ResponseEntity.ok(Map.of("message", "Subcategory deleted successfully"));
        } catch (Exception e) {
            log.error("Error deleting subcategory", e);
            return ResponseEntity.badRequest().body(
                Map.of("error", "Failed to delete subcategory: " + e.getMessage())
            );
        }
    }

    // MicroCategory Management
    @GetMapping("/subcategories/{subCategoryId}/microcategories")
    public ResponseEntity<List<MicroCategory>> getMicroCategories(@PathVariable Long subCategoryId) {
        List<MicroCategory> microCategories = microCategoryRepository.findBySubCategoryIdOrderByDisplayOrder(subCategoryId);
        return ResponseEntity.ok(microCategories);
    }

    @PostMapping("/subcategories/{subCategoryId}/microcategories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MicroCategory> createMicroCategory(
            @PathVariable Long subCategoryId,
            @RequestBody MicroCategoryRequest request) {
        try {
            Optional<SubCategory> subCategoryOpt = subCategoryRepository.findById(subCategoryId);
            if (subCategoryOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            MicroCategory microCategory = MicroCategory.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .displayOrder(request.getDisplayOrder())
                    .subCategory(subCategoryOpt.get())
                    .isActive(true)
                    .build();

            MicroCategory savedMicroCategory = microCategoryRepository.save(microCategory);
            return ResponseEntity.ok(savedMicroCategory);
        } catch (Exception e) {
            log.error("Error creating microcategory", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/microcategories/{microCategoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MicroCategory> updateMicroCategory(
            @PathVariable Long microCategoryId,
            @RequestBody MicroCategoryRequest request) {
        try {
            Optional<MicroCategory> microCategoryOpt = microCategoryRepository.findById(microCategoryId);
            if (microCategoryOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            MicroCategory microCategory = microCategoryOpt.get();
            microCategory.setName(request.getName());
            microCategory.setDescription(request.getDescription());
            microCategory.setDisplayOrder(request.getDisplayOrder());
            microCategory.setActive(request.isActive());

            MicroCategory savedMicroCategory = microCategoryRepository.save(microCategory);
            return ResponseEntity.ok(savedMicroCategory);
        } catch (Exception e) {
            log.error("Error updating microcategory", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/microcategories/{microCategoryId}")
    public ResponseEntity<Map<String, String>> deleteMicroCategory(@PathVariable Long microCategoryId) {
        try {
            // Check if microcategory has products
            Long productCount = productRepository.countByMicroCategoryId(microCategoryId);
            if (productCount > 0) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "Cannot delete microcategory. It has " + productCount + " products. Delete them first.")
                );
            }
            
            microCategoryRepository.deleteById(microCategoryId);
            return ResponseEntity.ok(Map.of("message", "Microcategory deleted successfully"));
        } catch (Exception e) {
            log.error("Error deleting microcategory", e);
            return ResponseEntity.badRequest().body(
                Map.of("error", "Failed to delete microcategory: " + e.getMessage())
            );
        }
    }

    // Products by MicroCategory
    @GetMapping("/microcategories/{microCategoryId}/products")
    public ResponseEntity<Page<Product>> getProductsByMicroCategory(
            @PathVariable Long microCategoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> products = productRepository.findByMicroCategoryId(microCategoryId, pageable);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Error getting products by microcategory", e);
            return ResponseEntity.badRequest().build();
        }
    }

    // Product Management
    @PostMapping("/microcategories/{microCategoryId}/products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> createProduct(
            @PathVariable Long microCategoryId,
            @RequestBody ProductRequest request) {
        try {
            Optional<MicroCategory> microCategoryOpt = microCategoryRepository.findById(microCategoryId);
            if (microCategoryOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Product product = Product.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .price(request.getPrice())
                    .stock(request.getStock())
                    .microCategory(microCategoryOpt.get())
                    .isActive(request.isActive())
                    .build();

            Product savedProduct = productRepository.save(product);
            return ResponseEntity.ok(savedProduct);
        } catch (Exception e) {
            log.error("Error creating product", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/products/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductRequest request) {
        try {
            Optional<Product> productOpt = productRepository.findById(productId);
            if (productOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Product product = productOpt.get();
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice());
            product.setStock(request.getStock());
            product.setActive(request.isActive());

            Product savedProduct = productRepository.save(product);
            return ResponseEntity.ok(savedProduct);
        } catch (Exception e) {
            log.error("Error updating product", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/products/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long productId) {
        try {
            Optional<Product> productOpt = productRepository.findById(productId);
            if (productOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            productRepository.deleteById(productId);
            return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
        } catch (Exception e) {
            log.error("Error deleting product", e);
            return ResponseEntity.badRequest().body(
                Map.of("error", "Failed to delete product: " + e.getMessage())
            );
        }
    }

    // Full Category Tree
    @GetMapping("/tree")
    public ResponseEntity<List<CategoryTreeNode>> getCategoryTree() {
        try {
            List<Category> categories = categoryRepository.findAll();
            List<CategoryTreeNode> tree = categories.stream()
                    .map(this::buildCategoryTreeNode)
                    .toList();
            return ResponseEntity.ok(tree);
        } catch (Exception e) {
            log.error("Error building category tree", e);
            return ResponseEntity.badRequest().build();
        }
    }

    private CategoryTreeNode buildCategoryTreeNode(Category category) {
        List<SubCategory> subCategories = subCategoryRepository.findByCategoryIdOrderByDisplayOrder(category.getId());
        
        List<CategoryTreeNode.SubCategoryNode> subNodes = subCategories.stream()
                .map(subCategory -> {
                    List<MicroCategory> microCategories = microCategoryRepository.findBySubCategoryIdOrderByDisplayOrder(subCategory.getId());
                    List<CategoryTreeNode.MicroCategoryNode> microNodes = microCategories.stream()
                            .map(microCategory -> CategoryTreeNode.MicroCategoryNode.builder()
                                    .id(microCategory.getId())
                                    .name(microCategory.getName())
                                    .description(microCategory.getDescription())
                                    .isActive(microCategory.isActive())
                                    .productCount(productRepository.countByMicroCategoryId(microCategory.getId()))
                                    .build())
                            .toList();
                    
                    return CategoryTreeNode.SubCategoryNode.builder()
                            .id(subCategory.getId())
                            .name(subCategory.getName())
                            .description(subCategory.getDescription())
                            .isActive(subCategory.isActive())
                            .microCategories(microNodes)
                            .build();
                })
                .toList();

        return CategoryTreeNode.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .isActive(category.isActive())
                .subCategories(subNodes)
                .build();
    }

    // Request DTOs
    @lombok.Data
    public static class CategoryRequest {
        private String name;
        private String description;
        private int displayOrder;
        private boolean isActive = true;
    }

    @lombok.Data
    public static class SubCategoryRequest {
        private String name;
        private String description;
        private int displayOrder;
        private boolean isActive = true;
    }

    @lombok.Data
    public static class MicroCategoryRequest {
        private String name;
        private String description;
        private int displayOrder;
        private boolean isActive = true;
    }

    @lombok.Data
    public static class ProductRequest {
        private String name;
        private String description;
        private Double price;
        private Integer stock;
        private boolean isActive = true;
    }

    // Tree response DTO
    @lombok.Data
    @lombok.Builder
    public static class CategoryTreeNode {
        private Long id;
        private String name;
        private String description;
        private boolean isActive;
        private List<SubCategoryNode> subCategories;

        @lombok.Data
        @lombok.Builder
        public static class SubCategoryNode {
            private Long id;
            private String name;
            private String description;
            private boolean isActive;
            private List<MicroCategoryNode> microCategories;
        }

        @lombok.Data
        @lombok.Builder
        public static class MicroCategoryNode {
            private Long id;
            private String name;
            private String description;
            private boolean isActive;
            private Long productCount;
        }
    }
}

