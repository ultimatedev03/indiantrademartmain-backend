package com.itech.itech_backend.modules.buyer.service;

import com.itech.itech_backend.modules.buyer.model.Product;
import com.itech.itech_backend.modules.buyer.repository.BuyerProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicSearchService {
    
    private final BuyerProductRepository productRepository;
    
    public Page<Product> searchProducts(String query, String category, String city, 
                                      Double minPrice, Double maxPrice, Pageable pageable) {
        
        log.info("Searching products with query: '{}', category: '{}', city: '{}'", 
                query, category, city);
        
        Specification<Product> spec = createSearchSpecification(query, category, city, minPrice, maxPrice);
        
        return productRepository.findAll(spec, pageable);
    }
    
    private Specification<Product> createSearchSpecification(String query, String category, 
                                                           String city, Double minPrice, Double maxPrice) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Only search active and approved products
            predicates.add(criteriaBuilder.isTrue(root.get("isActive")));
            predicates.add(criteriaBuilder.isTrue(root.get("isApproved")));
            
            // Text search across multiple fields
            if (query != null && !query.trim().isEmpty()) {
                String searchPattern = "%" + query.toLowerCase() + "%";
                
                Predicate namePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")), searchPattern);
                Predicate descriptionPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("description")), searchPattern);
                Predicate brandPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("brand")), searchPattern);
                Predicate tagsPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("tags")), searchPattern);
                
                predicates.add(criteriaBuilder.or(namePredicate, descriptionPredicate, 
                                                 brandPredicate, tagsPredicate));
            }
            
            // Category filter
            if (category != null && !category.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.join("category").get("name")), 
                    "%" + category.toLowerCase() + "%"));
            }
            
            // Price range filter
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            
            // Location filter (through vendor)
            if (city != null && !city.trim().isEmpty()) {
                // This assumes vendors have location information
                // You might need to adjust based on your actual vendor model structure
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.join("vendor").get("city")), 
                    "%" + city.toLowerCase() + "%"));
            }
            
            // Stock filter - only show products in stock
            predicates.add(criteriaBuilder.greaterThan(root.get("stock"), 0));
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    public List<String> getSearchSuggestions(String query) {
        if (query == null || query.trim().length() < 2) {
            return List.of();
        }
        
        try {
            // Get product name suggestions
            return productRepository.findProductNameSuggestions(query.toLowerCase(), 10);
        } catch (Exception e) {
            log.error("Error getting search suggestions", e);
            return List.of();
        }
    }
    
    public List<Product> getFeaturedProducts(int limit) {
        try {
            return productRepository.findFeaturedProducts(limit);
        } catch (Exception e) {
            log.error("Error getting featured products", e);
            return List.of();
        }
    }
    
    public List<Product> getPopularProducts(int limit) {
        try {
            return productRepository.findPopularProducts(limit);
        } catch (Exception e) {
            log.error("Error getting popular products", e);
            return List.of();
        }
    }
}
