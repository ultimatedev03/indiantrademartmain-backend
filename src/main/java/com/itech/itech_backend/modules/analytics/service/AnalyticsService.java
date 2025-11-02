package com.itech.itech_backend.modules.analytics.service;

import com.itech.itech_backend.modules.vendor.repository.VendorsRepository;
import com.itech.itech_backend.modules.core.repository.UserRepository;
import com.itech.itech_backend.modules.buyer.repository.BuyerProductRepository;
import com.itech.itech_backend.modules.buyer.repository.OrderRepository;
import com.itech.itech_backend.modules.buyer.repository.InquiryRepository;
import com.itech.itech_backend.modules.buyer.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final UserRepository userRepository;
    private final VendorsRepository vendorsRepository;
    private final BuyerProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final InquiryRepository inquiryRepository;
    private final ReviewRepository reviewRepository;

    public Map<String, Object> getDashboardAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        // User statistics
        analytics.put("totalUsers", userRepository.count());
        analytics.put("totalVendors", vendorsRepository.count());
        analytics.put("verifiedVendors", vendorsRepository.countByVerifiedTrue());
        
        // Product statistics
        analytics.put("totalProducts", productRepository.count());
        analytics.put("activeProducts", productRepository.countByIsActiveTrue());
        analytics.put("approvedProducts", productRepository.countByIsApprovedTrue());
        
        // Order statistics
        analytics.put("totalOrders", orderRepository.count());
        
        // Inquiry statistics
        analytics.put("totalInquiries", inquiryRepository.count());
        analytics.put("resolvedInquiries", inquiryRepository.countByIsResolvedTrue());
        
        // Review statistics
        analytics.put("totalReviews", reviewRepository.count());
        analytics.put("approvedReviews", reviewRepository.countByIsApprovedTrue());
        
        return analytics;
    }

    public Map<String, Object> getVendorAnalytics(Long vendorId) {
        Map<String, Object> analytics = new HashMap<>();
        
        // Vendor-specific statistics
        analytics.put("totalProducts", productRepository.countByVendorId(vendorId));
        analytics.put("activeProducts", productRepository.countByVendorIdAndIsActiveTrue(vendorId));
        analytics.put("totalOrders", orderRepository.countByVendorId(vendorId));
        analytics.put("totalReviews", reviewRepository.countByVendorId(vendorId));
        
        return analytics;
    }

    public Map<String, Object> getSystemMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // System health metrics
        Runtime runtime = Runtime.getRuntime();
        metrics.put("totalMemory", runtime.totalMemory());
        metrics.put("freeMemory", runtime.freeMemory());
        metrics.put("usedMemory", runtime.totalMemory() - runtime.freeMemory());
        metrics.put("availableProcessors", runtime.availableProcessors());
        
        return metrics;
    }
}

