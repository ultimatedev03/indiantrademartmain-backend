package com.itech.itech_backend.modules.buyer.service;

import com.itech.itech_backend.modules.buyer.model.Review;
import com.itech.itech_backend.modules.vendor.model.VendorReview;
import com.itech.itech_backend.modules.buyer.model.Product;
import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.vendor.model.Vendors;
import com.itech.itech_backend.modules.buyer.repository.ReviewRepository;
import com.itech.itech_backend.modules.vendor.repository.VendorReviewRepository;
import com.itech.itech_backend.modules.buyer.repository.BuyerProductRepository;
import com.itech.itech_backend.modules.core.repository.UserRepository;
import com.itech.itech_backend.modules.vendor.repository.VendorsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final VendorReviewRepository vendorReviewRepository;
    private final BuyerProductRepository productRepository;
    private final UserRepository userRepository;
    private final VendorsRepository vendorsRepository;

    public Review createProductReview(Long userId, Long productId, int rating, String comment, String title) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
            
            if (reviewRepository.existsByUserIdAndProductId(userId, productId)) {
                throw new RuntimeException("You have already reviewed this product");
            }

            Review review = Review.builder()
                .user(user)
                .product(product)
                .vendor(product.getVendor())
                .rating(rating)
                .comment(comment)
                .createdAt(LocalDateTime.now())
                .build();
            
            return reviewRepository.save(review);
        } catch (Exception e) {
            log.error("Error creating product review", e);
            throw new RuntimeException("Failed to create review: " + e.getMessage());
        }
    }

    public Review createProductReview(Review review) {
        return reviewRepository.save(review);
    }

    public VendorReview createVendorReview(VendorReview vendorReview) {
        return vendorReviewRepository.save(vendorReview);
    }

    public List<Review> getProductReviews(Long productId) {
        return reviewRepository.findByProductIdAndIsApprovedTrue(productId);
    }

    public Page<Review> getProductReviewsPaginated(Long productId, Pageable pageable) {
        return reviewRepository.findByProductIdAndIsApprovedTrueOrderByCreatedAtDesc(productId, pageable);
    }

    public List<VendorReview> getVendorReviews(Long vendorId) {
        return vendorReviewRepository.findByVendorId(vendorId);
    }

    public Page<Review> getVendorReviewsPaginated(Long vendorId, Pageable pageable) {
        return reviewRepository.findByVendorIdAndIsApprovedTrueOrderByCreatedAtDesc(vendorId, pageable);
    }

    public Page<Review> getUserReviews(Long userId, Pageable pageable) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public Review approveReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setApproved(true);
        review.setUpdatedAt(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    public Map<String, Object> getProductRatingStats(Long productId) {
        Map<String, Object> stats = new HashMap<>();
        
        Double avgRating = reviewRepository.getAverageRatingByProductId(productId);
        Long totalReviews = reviewRepository.countByProductIdAndIsApprovedTrue(productId);
        List<Object[]> ratingDistribution = reviewRepository.getRatingDistributionByProductId(productId);
        
        stats.put("averageRating", avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0);
        stats.put("totalReviews", totalReviews);
        stats.put("ratingDistribution", ratingDistribution);
        
        return stats;
    }

    public Map<String, Object> getVendorRatingStats(Long vendorId) {
        Map<String, Object> stats = new HashMap<>();
        
        Double avgRating = reviewRepository.getAverageRatingByVendorId(vendorId);
        Long totalReviews = reviewRepository.countByVendorIdAndIsApprovedTrue(vendorId);
        List<Object[]> ratingDistribution = reviewRepository.getRatingDistributionByVendorId(vendorId);
        
        stats.put("averageRating", avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0);
        stats.put("totalReviews", totalReviews);
        stats.put("ratingDistribution", ratingDistribution);
        
        return stats;
    }

    public Page<Review> getPendingReviews(Pageable pageable) {
        return reviewRepository.findByIsApprovedFalseOrderByCreatedAtDesc(pageable);
    }

    public boolean hasUserReviewedProduct(Long userId, Long productId) {
        return reviewRepository.existsByUserIdAndProductId(userId, productId);
    }
}

