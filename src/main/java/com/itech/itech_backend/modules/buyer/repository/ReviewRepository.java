package com.itech.itech_backend.modules.buyer.repository;

import com.itech.itech_backend.modules.buyer.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(Long productId);
    List<Review> findByProductIdAndIsApprovedTrue(Long productId);
    Page<Review> findByProductIdAndIsApprovedTrueOrderByCreatedAtDesc(Long productId, Pageable pageable);
    
    List<Review> findByVendorId(Long vendorId);
    List<Review> findByVendorIdAndIsApprovedTrue(Long vendorId);
    Page<Review> findByVendorIdAndIsApprovedTrueOrderByCreatedAtDesc(Long vendorId, Pageable pageable);
    
    List<Review> findByUserId(Long userId);
    Page<Review> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    Optional<Review> findByUserIdAndProductId(Long userId, Long productId);
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    
    List<Review> findByIsApprovedFalse();
    Page<Review> findByIsApprovedFalseOrderByCreatedAtDesc(Pageable pageable);
    
    long countByIsApprovedTrue();
    long countByVendorId(Long vendorId);
    long countByVendorIdAndIsApprovedTrue(Long vendorId);
    long countByProductId(Long productId);
    long countByProductIdAndIsApprovedTrue(Long productId);
    
    @Query("SELECT AVG(r.rating) FROM BuyerReview r WHERE r.product.id = :productId AND r.isApproved = true")
    Double getAverageRatingByProductId(@Param("productId") Long productId);
    
    @Query("SELECT AVG(r.rating) FROM BuyerReview r WHERE r.vendor.id = :vendorId AND r.isApproved = true")
    Double getAverageRatingByVendorId(@Param("vendorId") Long vendorId);
    
    @Query("SELECT r.rating, COUNT(r) FROM BuyerReview r WHERE r.product.id = :productId AND r.isApproved = true GROUP BY r.rating ORDER BY r.rating")
    List<Object[]> getRatingDistributionByProductId(@Param("productId") Long productId);
    
    @Query("SELECT r.rating, COUNT(r) FROM BuyerReview r WHERE r.vendor.id = :vendorId AND r.isApproved = true GROUP BY r.rating ORDER BY r.rating")
    List<Object[]> getRatingDistributionByVendorId(@Param("vendorId") Long vendorId);
}

