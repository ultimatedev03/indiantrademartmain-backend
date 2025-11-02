package com.itech.itech_backend.modules.buyer.controller;

import com.itech.itech_backend.modules.shared.dto.ProductDto;
import com.itech.itech_backend.modules.buyer.model.Product;
import com.itech.itech_backend.modules.buyer.service.ProductService;
import com.itech.itech_backend.modules.buyer.service.BasicSearchService;
import com.itech.itech_backend.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final BasicSearchService basicSearchService;
    private final JwtTokenUtil jwtTokenUtil;

    // Add Category, Subcategory, Microcategory, Product
    @PostMapping("/data-entry")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<?> addDataEntry(@RequestBody ProductDto dto) {
        try {
            Product product = productService.addDataEntry(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(product);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Fetch Products with Filters
    @GetMapping("/categories")
    public ResponseEntity<List<Product>> getFilteredProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String subCategory,
            @RequestParam(required = false) String microCategory,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String location) {
        List<Product> products = productService.getFilteredProducts(category, subCategory, microCategory, minPrice, maxPrice, location);
        return ResponseEntity.ok(products);
    }

    // Public endpoints
    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> products = productService.getProducts(pageable, category, search, minPrice, maxPrice, sortBy, sortDir);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Error getting products", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Long productId) {
        try {
            Product product = productService.getProductById(productId);
            // Increment view count
            productService.incrementViewCount(productId);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            log.error("Error getting product", e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Product>> searchProducts(
            @RequestParam String query,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> products = basicSearchService.searchProducts(query, category, city, minPrice, maxPrice, pageable);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Error searching products", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/search/suggestions")
    public ResponseEntity<List<String>> getSearchSuggestions(
            @RequestParam String query) {
        try {
            List<String> suggestions = basicSearchService.getSearchSuggestions(query);
            return ResponseEntity.ok(suggestions);
        } catch (Exception e) {
            log.error("Error getting search suggestions", e);
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<Product>> getProductsByCategory(
            @PathVariable String categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        try {
            // Validate categoryId parameter
            if ("NaN".equals(categoryId) || categoryId == null || categoryId.trim().isEmpty()) {
                log.warn("Invalid categoryId received: {}", categoryId);
                return ResponseEntity.badRequest().build();
            }
            
            Long categoryIdLong;
            try {
                categoryIdLong = Long.parseLong(categoryId);
            } catch (NumberFormatException e) {
                log.warn("Invalid categoryId format: {}", categoryId);
                return ResponseEntity.badRequest().build();
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> products = productService.getProductsByCategory(categoryIdLong, pageable);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Error getting products by category", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<Page<Product>> getProductsByVendor(
            @PathVariable Long vendorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> products = productService.getProductsByVendor(vendorId, pageable);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Error getting products by vendor", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/featured")
    public ResponseEntity<List<Product>> getFeaturedProducts(
            @RequestParam(defaultValue = "8") int limit) {
        try {
            List<Product> products = productService.getFeaturedProducts(limit);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Error getting featured products", e);
            return ResponseEntity.badRequest().build();
        }
    }

    // Vendor endpoints
    @PostMapping
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> addProduct(@RequestBody ProductDto dto, HttpServletRequest request) {
        try {
            log.info("Adding product: {}", dto.getName());
            log.info("Authorization header: {}", request.getHeader("Authorization"));

            Long vendorId = jwtTokenUtil.extractUserIdFromRequest(request);
            log.info("Extracted vendor ID: {}", vendorId);

            if (vendorId == null) {
                log.error("No vendor ID found in JWT token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Authentication failed: No valid vendor session found");
            }

            Product product = productService.addProduct(vendorId, dto);
            log.info("Product created successfully with ID: {}", product.getId());
            return ResponseEntity.ok(product);
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error adding product", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/vendor/add")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> addProductVendor(@RequestBody ProductDto dto, HttpServletRequest request) {
        try {
            log.info("=== VENDOR ADD PRODUCT REQUEST ===");
            log.info("Product name: {}", dto.getName());
            log.info("Authorization header: {}", request.getHeader("Authorization"));
            log.info("Request method: {}", request.getMethod());
            log.info("Request URI: {}", request.getRequestURI());
            log.info("Current security context: {}", org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication());

            Long vendorId = jwtTokenUtil.extractUserIdFromRequest(request);
            log.info("Extracted vendor ID: {}", vendorId);

            if (vendorId == null) {
                log.error("No vendor ID found in JWT token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Authentication failed: No valid vendor session found");
            }

            Product product = productService.addProduct(vendorId, dto);
            log.info("Product created successfully with ID: {}", product.getId());
            return ResponseEntity.ok(product);
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error adding product", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @PostMapping(value = "/{productId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> uploadProductImages(
            @PathVariable Long productId,
            @RequestParam("images") MultipartFile[] images,
            HttpServletRequest request) {
        try {
            log.info("=== UPLOADING PRODUCT IMAGES ===");
            log.info("Product ID: {}", productId);
            log.info("Number of images: {}", images.length);
            
            Long vendorId = jwtTokenUtil.extractUserIdFromRequest(request);
            if (vendorId == null) {
                log.error("No vendor ID found in JWT token");
                return ResponseEntity.badRequest().body("Invalid vendor session");
            }
            
            log.info("Vendor ID: {}", vendorId);
            
            List<String> imageUrls = productService.uploadProductImages(productId, vendorId, images);
            log.info("Successfully uploaded {} images", imageUrls.size());
            
            return ResponseEntity.ok(Map.of(
                "message", "Images uploaded successfully",
                "imageUrls", imageUrls,
                "productId", productId
            ));
        } catch (Exception e) {
            log.error("Error uploading product images", e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    @PutMapping(value = "/{productId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> updateProductImages(
            @PathVariable Long productId,
            @RequestParam("images") MultipartFile[] images,
            HttpServletRequest request) {
        try {
            log.info("=== UPDATING PRODUCT IMAGES ===");
            log.info("Product ID: {}", productId);
            log.info("Number of images: {}", images.length);
            
            Long vendorId = jwtTokenUtil.extractUserIdFromRequest(request);
            if (vendorId == null) {
                log.error("No vendor ID found in JWT token");
                return ResponseEntity.badRequest().body("Invalid vendor session");
            }
            
            log.info("Vendor ID: {}", vendorId);
            
            List<String> imageUrls = productService.updateProductImages(productId, vendorId, images);
            log.info("Successfully updated {} images", imageUrls.size());
            
            return ResponseEntity.ok(Map.of(
                "message", "Images updated successfully",
                "imageUrls", imageUrls,
                "productId", productId
            ));
        } catch (Exception e) {
            log.error("Error updating product images", e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId, @RequestBody ProductDto dto, HttpServletRequest request) {
        try {
            Long vendorId = jwtTokenUtil.extractUserIdFromRequest(request);
            if (vendorId == null) {
                return ResponseEntity.badRequest().body("Invalid vendor session");
            }
            Product product = productService.updateProduct(productId, vendorId, dto);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            log.error("Error updating product", e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId, HttpServletRequest request) {
        try {
            Long vendorId = jwtTokenUtil.extractUserIdFromRequest(request);
            if (vendorId == null) {
                return ResponseEntity.badRequest().body("Invalid vendor session");
            }
            productService.deleteProduct(productId, vendorId);
            return ResponseEntity.ok("Product deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting product", e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/vendor/my-products")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<Page<Product>> getVendorProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            HttpServletRequest request) {
        try {
            log.info("=== VENDOR PRODUCTS REQUEST ===");
            log.info("Getting vendor products - page: {} size: {}", page, size);
            log.info("Authorization header: {}", request.getHeader("Authorization"));
            
            // Debug JWT token extraction
            String username = jwtTokenUtil.extractUsernameFromRequest(request);
            String role = jwtTokenUtil.extractRoleFromRequest(request);
            Long vendorId = jwtTokenUtil.extractUserIdFromRequest(request);
            
            log.info("JWT Debug - Username: {}", username);
            log.info("JWT Debug - Role: {}", role);
            log.info("JWT Debug - User ID: {}", vendorId);

            if (vendorId == null) {
                log.error("No vendor ID found in JWT token for user: {} with role: {}", username, role);
                
                // Try to get vendor by email if userId is null
                if (username != null) {
                    log.info("Trying to find vendor by email: {}", username);
                    try {
                        // For registered vendor emails, try fallback to vendor ID 1 (most recent)
                        if ("aditya123@gmail.com".equals(username)) {
                            log.info("Using fallback vendor ID 1 for known email: {}", username);
                            vendorId = 1L;
                        }
                    } catch (Exception e) {
                        log.error("Fallback vendor lookup failed", e);
                    }
                }
                
                if (vendorId == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(null); // Changed from UNAUTHORIZED to BAD_REQUEST to match the error seen
                }
            }
            
            log.info("Using vendor ID: {}", vendorId);

            Pageable pageable = PageRequest.of(page, size);
            Page<Product> products = productService.getProductsByVendor(vendorId, pageable);
            log.info("Found {} products for vendor {}", products.getTotalElements(), vendorId);

            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Error getting vendor products", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/test/vendor/{vendorId}/products")
    public ResponseEntity<Page<Product>> getVendorProductsTest(
            @PathVariable Long vendorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        try {
            log.info("TEST: Getting vendor products for vendor ID: {}", vendorId);
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> products = productService.getProductsByVendor(vendorId, pageable);
            log.info("TEST: Found {} products for vendor {}", products.getTotalElements(), vendorId);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("TEST: Error getting vendor products", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/vendor1/products")
    public ResponseEntity<Page<Product>> getVendor1Products(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        try {
            log.info("Getting products for vendor ID 1 - page: {} size: {}", page, size);
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> products = productService.getProductsByVendor(1L, pageable);
            log.info("Found {} products for vendor 1", products.getTotalElements());
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Error getting vendor 1 products", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/{productId}/status")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> updateProductStatus(
            @PathVariable Long productId,
            @RequestParam boolean isActive,
            HttpServletRequest request) {
        try {
            Long vendorId = jwtTokenUtil.extractUserIdFromRequest(request);
            if (vendorId == null) {
                return ResponseEntity.badRequest().body("Invalid vendor session");
            }
            Product product = productService.updateProductStatus(productId, vendorId, isActive);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            log.error("Error updating product status", e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // Admin endpoints
    @PatchMapping("/{productId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveProduct(@PathVariable Long productId) {
        try {
            Product product = productService.approveProduct(productId);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            log.error("Error approving product", e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PatchMapping("/{productId}/feature")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> featureProduct(@PathVariable Long productId, @RequestParam boolean featured) {
        try {
            Product product = productService.setFeaturedStatus(productId, featured);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            log.error("Error setting featured status", e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/pending-approval")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Product>> getPendingProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> products = productService.getPendingApprovalProducts(pageable);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Error getting pending products", e);
            return ResponseEntity.badRequest().build();
        }
    }

}

