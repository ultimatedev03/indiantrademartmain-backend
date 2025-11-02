package com.itech.itech_backend.modules.buyer.controller;

import com.itech.itech_backend.modules.shared.dto.ProductSearchDto;
import com.itech.itech_backend.modules.shared.dto.ProductSearchResponseDto;
import com.itech.itech_backend.modules.buyer.model.Product;
import com.itech.itech_backend.modules.buyer.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = {"http://localhost:3000", "https://your-frontend-domain.com"})
@Slf4j
public class ProductSearchController {

    @Autowired
    private ProductSearchService productSearchService;

    @GetMapping("/advanced-search-products")
    public ResponseEntity<Map<String, Object>> searchProducts(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);

        ProductSearchDto searchDto = ProductSearchDto.builder()
            .query(query)
            .categoryId(categoryId)
            .city(city)
            .state(state)
            .minPrice(minPrice)
            .maxPrice(maxPrice)
            .build();

        Page<Product> products = productSearchService.searchProducts(searchDto, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("products", products.getContent());
        response.put("currentPage", products.getNumber());
        response.put("totalPages", products.getTotalPages());
        response.put("totalElements", products.getTotalElements());
        response.put("hasNext", products.hasNext());
        response.put("hasPrevious", products.hasPrevious());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/featured")
    public ResponseEntity<List<Product>> getFeaturedProducts(
            @RequestParam(defaultValue = "10") int limit) {
        List<Product> featuredProducts = productSearchService.getFeaturedProducts(limit);
        return ResponseEntity.ok(featuredProducts);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Product>> getRecentProducts(
            @RequestParam(defaultValue = "10") int limit) {
        List<Product> recentProducts = productSearchService.getRecentProducts(limit);
        return ResponseEntity.ok(recentProducts);
    }

    @GetMapping("/search/category/{categoryId}")
    public ResponseEntity<Page<Product>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> products = productSearchService.getProductsByCategory(categoryId, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search/vendor/{vendorId}")
    public ResponseEntity<Page<Product>> getProductsByVendor(
            @PathVariable Long vendorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> products = productSearchService.getProductsByVendor(vendorId, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> getSearchSuggestions(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int limit) {
        List<String> suggestions = productSearchService.getSearchSuggestions(query, limit);
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/advanced-search")
    public ResponseEntity<Map<String, Object>> advancedSearchProducts(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long subCategoryId,
            @RequestParam(required = false) Long microCategoryId,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) Boolean freeShipping,
            @RequestParam(required = false) String vendorType,
            @RequestParam(required = false) Double minRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            log.info("Advanced search - Query: {}, Category: {}, Brand: {}", query, categoryId, brand);
            
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);

            ProductSearchDto searchDto = ProductSearchDto.builder()
                .query(query)
                .categoryId(categoryId)
                .subCategoryId(subCategoryId)
                .microCategoryId(microCategoryId)
                .brand(brand)
                .city(city)
                .state(state)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .inStock(inStock)
                .freeShipping(freeShipping)
                .vendorType(vendorType)
                .minRating(minRating)
                .build();

            Page<Product> products = productSearchService.searchProducts(searchDto, pageable);
            Map<String, Object> filters = productSearchService.getAvailableFilters(searchDto);

            Map<String, Object> response = new HashMap<>();
            response.put("products", products.getContent());
            response.put("currentPage", products.getNumber());
            response.put("totalPages", products.getTotalPages());
            response.put("totalElements", products.getTotalElements());
            response.put("hasNext", products.hasNext());
            response.put("hasPrevious", products.hasPrevious());
            response.put("filters", filters);

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error in advanced product search", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/filters")
    public ResponseEntity<Map<String, Object>> getSearchFilters(
            @RequestParam(required = false) Long categoryId) {
        try {
            Map<String, Object> filters = productSearchService.getSearchFilters(categoryId);
            return ResponseEntity.ok(filters);
        } catch (Exception e) {
            log.error("Error getting search filters", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/trending")
    public ResponseEntity<List<Product>> getTrendingProducts(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Product> trendingProducts = productSearchService.getTrendingProducts(limit);
            return ResponseEntity.ok(trendingProducts);
        } catch (Exception e) {
            log.error("Error getting trending products", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/similar/{productId}")
    public ResponseEntity<List<Product>> getSimilarProducts(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "8") int limit) {
        try {
            List<Product> similarProducts = productSearchService.getSimilarProducts(productId, limit);
            return ResponseEntity.ok(similarProducts);
        } catch (Exception e) {
            log.error("Error getting similar products", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/track-view/{productId}")
    public ResponseEntity<Void> trackProductView(@PathVariable Long productId) {
        try {
            productSearchService.trackProductView(productId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error tracking product view", e);
            return ResponseEntity.badRequest().build();
        }
    }
}

