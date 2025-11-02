package com.itech.itech_backend.modules.shared.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.itech.itech_backend.modules.buyer.model.Product;
import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.buyer.repository.BuyerProductRepository;
import com.itech.itech_backend.modules.core.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PerformanceOptimizationService {

    @Autowired
    private BuyerProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Cached method for frequently accessed products
     */
    @Cacheable(value = "popularProducts", key = "#limit")
    public List<Product> getPopularProducts(int limit) {
        log.info("Fetching popular products from database (cache miss)");
        return productRepository.findTopRatedProducts(PageRequest.of(0, limit));
    }

    /**
     * Cached method for user dashboard data
     */
    @Cacheable(value = "userDashboard", key = "#userId")
    public Map<String, Object> getUserDashboardData(Long userId) {
        log.info("Building user dashboard data for user: {}", userId);
        
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return Map.of();
        }

        // Simulate expensive dashboard data aggregation
        return Map.of(
            "user", user,
            "recentOrders", getRecentOrdersCount(userId),
            "wishlistCount", getWishlistCount(userId),
            "notificationCount", getUnreadNotificationCount(userId),
            "lastLoginTime", LocalDateTime.now().minusHours(2)
        );
    }

    /**
     * Clear user dashboard cache when user data changes
     */
    @CacheEvict(value = "userDashboard", key = "#userId")
    public void invalidateUserDashboardCache(Long userId) {
        log.info("Invalidating user dashboard cache for user: {}", userId);
    }

    /**
     * Asynchronous method for heavy background processing
     */
    @Async
    public CompletableFuture<Void> processAnalyticsDataAsync(Long userId) {
        log.info("Starting async analytics processing for user: {}", userId);
        
        try {
            // Simulate heavy analytics processing
            Thread.sleep(2000);
            
            // Update analytics data
            updateUserAnalytics(userId);
            
            log.info("Completed async analytics processing for user: {}", userId);
        } catch (Exception e) {
            log.error("Error in async analytics processing: {}", e.getMessage());
        }
        
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Optimized product search with caching
     */
    @Cacheable(value = "productSearch", key = "#query + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Product> searchProductsOptimized(String query, Pageable pageable) {
        log.info("Performing optimized product search for query: {}", query);
        
        // Use database indexes and optimized queries
        return productRepository.findByNameContainingOrDescriptionContaining(query, pageable);
    }

    /**
     * Batch processing for bulk operations
     */
    public void bulkUpdateProductPrices(List<Long> productIds, double priceMultiplier) {
        log.info("Starting bulk price update for {} products", productIds.size());
        
        // Process in batches to avoid memory issues
        int batchSize = 100;
        for (int i = 0; i < productIds.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, productIds.size());
            List<Long> batch = productIds.subList(i, endIndex);
            
            processPriceBatch(batch, priceMultiplier);
        }
        
        // Clear relevant caches
        clearProductCaches();
    }

    /**
     * Memory-efficient data export
     */
    @Async
    public CompletableFuture<String> exportDataAsync(String dataType, Long userId) {
        log.info("Starting async data export for type: {} by user: {}", dataType, userId);
        
        try {
            switch (dataType.toLowerCase()) {
                case "products":
                    return CompletableFuture.completedFuture(exportProductsData());
                case "orders":
                    return CompletableFuture.completedFuture(exportOrdersData(userId));
                case "analytics":
                    return CompletableFuture.completedFuture(exportAnalyticsData(userId));
                default:
                    return CompletableFuture.completedFuture("Unsupported export type");
            }
        } catch (Exception e) {
            log.error("Error during data export: {}", e.getMessage());
            return CompletableFuture.completedFuture("Export failed: " + e.getMessage());
        }
    }

    /**
     * Database connection pool monitoring
     */
    public Map<String, Object> getDatabaseHealth() {
        // This would integrate with actual database monitoring tools
        return Map.of(
            "activeConnections", 25,
            "maxConnections", 100,
            "avgQueryTime", "45ms",
            "slowQueries", 2,
            "lastBackup", LocalDateTime.now().minusHours(6),
            "status", "healthy"
        );
    }

    /**
     * Cache statistics and monitoring
     */
    public Map<String, Object> getCacheStatistics() {
        return Map.of(
            "popularProducts", getCacheInfo("popularProducts"),
            "userDashboard", getCacheInfo("userDashboard"),
            "productSearch", getCacheInfo("productSearch"),
            "totalMemoryUsage", "125MB",
            "hitRatio", 0.87
        );
    }

    /**
     * Preload frequently accessed data
     */
    public void preloadCaches() {
        log.info("Starting cache preloading");
        
        // Preload popular products
        getPopularProducts(20);
        
        // Preload recent active users' dashboard data
        List<User> activeUsers = userRepository.findRecentActiveUsers(50);
        for (User user : activeUsers) {
            getUserDashboardData(user.getId());
        }
        
        log.info("Cache preloading completed");
    }

    /**
     * Cleanup expired cache entries
     */
    @CacheEvict(value = {"popularProducts", "userDashboard", "productSearch"}, allEntries = true)
    public void clearAllCaches() {
        log.info("Clearing all application caches");
    }

    // Private helper methods
    
    private int getRecentOrdersCount(Long userId) {
        // Simulate database query
        return 5;
    }
    
    private int getWishlistCount(Long userId) {
        // Simulate database query
        return 12;
    }
    
    private int getUnreadNotificationCount(Long userId) {
        // Simulate database query
        return 3;
    }
    
    private void updateUserAnalytics(Long userId) {
        // Simulate analytics update
        log.debug("Updating analytics for user: {}", userId);
    }
    
    private void processPriceBatch(List<Long> productIds, double priceMultiplier) {
        // Simulate batch price update
        log.debug("Processing price batch for {} products", productIds.size());
    }
    
    private void clearProductCaches() {
        if (redisTemplate != null) {
            redisTemplate.delete("popularProducts::*");
            redisTemplate.delete("productSearch::*");
        } else {
            log.warn("RedisTemplate not available, skipping cache clear operation");
        }
    }
    
    private String exportProductsData() {
        // Simulate product data export
        return "products_export_" + System.currentTimeMillis() + ".csv";
    }
    
    private String exportOrdersData(Long userId) {
        // Simulate orders data export
        return "orders_export_user_" + userId + "_" + System.currentTimeMillis() + ".csv";
    }
    
    private String exportAnalyticsData(Long userId) {
        // Simulate analytics data export
        return "analytics_export_user_" + userId + "_" + System.currentTimeMillis() + ".json";
    }
    
    private Map<String, Object> getCacheInfo(String cacheName) {
        // Simulate cache statistics
        return Map.of(
            "hits", 1250,
            "misses", 180,
            "size", 45,
            "evictions", 12
        );
    }
}

