package com.itech.itech_backend.modules.buyer.service;

import com.itech.itech_backend.modules.shared.dto.ProductSearchDto;
import com.itech.itech_backend.modules.shared.dto.ProductSearchResponseDto;
import com.itech.itech_backend.modules.buyer.model.Product;
import com.itech.itech_backend.modules.buyer.repository.BuyerProductRepository;
import com.itech.itech_backend.modules.buyer.repository.BuyerCategoryRepository;
import com.itech.itech_backend.modules.vendor.repository.VendorsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductSearchService {

    @Autowired
    private BuyerProductRepository productRepository;
    
    @Autowired
    private BuyerCategoryRepository categoryRepository;
    
    @Autowired
    private VendorsRepository vendorsRepository;

    public Page<Product> searchProducts(ProductSearchDto searchDto, Pageable pageable) {
        return productRepository.searchProducts(
            searchDto.getQuery(),
            searchDto.getCategoryId(),
            searchDto.getSubCategoryId(),
            searchDto.getMicroCategoryId(),
            searchDto.getCity(),
            searchDto.getState(),
            searchDto.getMinPrice(),
            searchDto.getMaxPrice(),
            searchDto.getVendorId(),
            searchDto.getIsActive(),
            pageable
        );
    }

    public List<Product> getFeaturedProducts(int limit) {
        return productRepository.findTopFeaturedProducts(Pageable.ofSize(limit)).getContent();
    }

    public List<Product> getRecentProducts(int limit) {
        return productRepository.findTopRecentProducts(Pageable.ofSize(limit)).getContent();
    }

    public Page<Product> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable);
    }

    public Page<Product> getProductsByVendor(Long vendorId, Pageable pageable) {
        return productRepository.findByVendorId(vendorId, pageable);
    }

    public List<String> getSearchSuggestions(String query, int limit) {
        return productRepository.findDistinctByNameContainingIgnoreCase(query, Pageable.ofSize(limit));
    }
    
    /**
     * Get search filters based on current search results
     */
    public Map<String, Object> getSearchFilters(Long categoryId) {
        Map<String, Object> filters = new HashMap<>();
        
        try {
            // Get price range
            Object[] priceRange = productRepository.findPriceRange();
            if (priceRange != null && priceRange.length >= 2) {
                Map<String, Double> priceFilter = new HashMap<>();
                priceFilter.put("min", (Double) priceRange[0]);
                priceFilter.put("max", (Double) priceRange[1]);
                filters.put("priceRange", priceFilter);
            }
            
            // Get available brands
            List<String> brands = productRepository.findDistinctBrands();
            filters.put("brands", brands);
            
            // Get available cities
            List<String> cities = vendorsRepository.findDistinctCities();
            filters.put("cities", cities);
            
            // Get available states
            List<String> states = vendorsRepository.findDistinctStates();
            filters.put("states", states);
            
            // Get vendor types
            List<String> vendorTypes = Arrays.asList("BASIC", "PREMIUM", "ENTERPRISE");
            filters.put("vendorTypes", vendorTypes);
            
            // Get categories
            if (categoryId == null) {
                filters.put("categories", categoryRepository.findAllActive());
            }
            
            log.info("Generated search filters with {} brands, {} cities", 
                    brands.size(), cities.size());
            
        } catch (Exception e) {
            log.error("Error generating search filters", e);
        }
        
        return filters;
    }
    
    /**
     * Get available filters based on current search criteria
     */
    public Map<String, Object> getAvailableFilters(ProductSearchDto searchDto) {
        Map<String, Object> filters = new HashMap<>();
        
        try {
            // This would typically query the database based on current filters
            // to show only available options
            filters.put("brands", productRepository.findDistinctBrands());
            filters.put("cities", vendorsRepository.findDistinctCities());
            filters.put("states", vendorsRepository.findDistinctStates());
            
        } catch (Exception e) {
            log.error("Error getting available filters", e);
        }
        
        return filters;
    }
    
    /**
     * Get trending products based on view count and recent activity
     */
    public List<Product> getTrendingProducts(int limit) {
        try {
            Pageable pageable = PageRequest.of(0, limit, 
                Sort.by(Sort.Direction.DESC, "viewCount", "createdAt"));
            return productRepository.findByIsActiveTrueAndIsApprovedTrue(pageable).getContent();
        } catch (Exception e) {
            log.error("Error getting trending products", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Get products similar to a given product
     */
    public List<Product> getSimilarProducts(Long productId, int limit) {
        try {
            Optional<Product> productOpt = productRepository.findById(productId);
            if (!productOpt.isPresent()) {
                return new ArrayList<>();
            }
            
            Product product = productOpt.get();
            Pageable pageable = PageRequest.of(0, limit);
            
            // Find products in same category, excluding the current product
            List<Product> similarProducts = productRepository
                .findByCategoryIdAndIdNotAndIsActiveTrueAndIsApprovedTrue(
                    product.getCategory().getId(), productId, pageable)
                .getContent();
            
            // If not enough similar products, get from same vendor
            if (similarProducts.size() < limit) {
                List<Product> vendorProducts = productRepository
                    .findByVendorIdAndIdNotAndIsActiveTrueAndIsApprovedTrue(
                        product.getVendor().getId(), productId, 
                        PageRequest.of(0, limit - similarProducts.size()))
                    .getContent();
                
                similarProducts.addAll(vendorProducts);
            }
            
            return similarProducts.stream()
                .distinct()
                .limit(limit)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Error getting similar products", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Track product view for analytics
     */
    @Transactional
    public void trackProductView(Long productId) {
        try {
            Optional<Product> productOpt = productRepository.findById(productId);
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                product.setViewCount(product.getViewCount() + 1);
                productRepository.save(product);
                
                log.debug("Tracked view for product ID: {}, new count: {}", 
                        productId, product.getViewCount());
            }
        } catch (Exception e) {
            log.error("Error tracking product view", e);
        }
    }
}

