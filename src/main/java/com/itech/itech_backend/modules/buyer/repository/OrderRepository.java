package com.itech.itech_backend.modules.buyer.repository;

import com.itech.itech_backend.modules.buyer.model.Order;
import com.itech.itech_backend.modules.buyer.model.Review;
import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.vendor.model.Vendors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT COUNT(DISTINCT o) FROM BuyerOrder o JOIN o.items oi WHERE oi.vendor.id = :vendorId")
    long countByVendorId(@Param("vendorId") Long vendorId);
    
    Optional<Order> findByOrderNumber(String orderNumber);
    
    List<Order> findByUser(User user);
    
    List<Order> findByUserId(Long userId);
    
    Page<Order> findByUserId(Long userId, Pageable pageable);
    
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    List<Order> findByStatus(Order.OrderStatus status);
    
    List<Order> findByPaymentStatus(Order.PaymentStatus paymentStatus);
    
    @Query("SELECT o FROM BuyerOrder o WHERE o.user.id = :userId AND o.status = :status")
    List<Order> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Order.OrderStatus status);
    
    @Query("SELECT o FROM BuyerOrder o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(o) FROM BuyerOrder o WHERE o.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT SUM(o.grandTotal) FROM BuyerOrder o WHERE o.user.id = :userId AND o.paymentStatus = 'PAID'")
    Double getTotalAmountByUserId(@Param("userId") Long userId);
    
    // Vendor specific queries
    @Query("SELECT DISTINCT o FROM BuyerOrder o JOIN o.items oi WHERE oi.vendor.id = :vendorId")
    List<Order> findOrdersByVendorId(@Param("vendorId") Long vendorId);
    
    @Query("SELECT DISTINCT o FROM BuyerOrder o JOIN o.items oi WHERE oi.vendor.id = :vendorId ORDER BY o.createdAt DESC")
    Page<Order> findOrdersByVendorId(@Param("vendorId") Long vendorId, Pageable pageable);
    
    // Additional vendor analytics methods
    @Query("SELECT COUNT(DISTINCT o) FROM BuyerOrder o JOIN o.items oi WHERE oi.vendor.id = :vendorId AND o.status = :status")
    long countByVendorIdAndStatus(@Param("vendorId") Long vendorId, @Param("status") Order.OrderStatus status);
    
    @Query("SELECT SUM(oi.price * oi.quantity) FROM BuyerOrder o JOIN o.items oi WHERE oi.vendor.id = :vendorId AND o.createdAt BETWEEN :startDate AND :endDate")
    Double sumRevenueByVendorIdAndDateRange(@Param("vendorId") Long vendorId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(DISTINCT o) FROM BuyerOrder o JOIN o.items oi WHERE oi.vendor.id = :vendorId AND o.createdAt BETWEEN :startDate AND :endDate")
    Long countByVendorIdAndDateRange(@Param("vendorId") Long vendorId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT AVG(r.rating) FROM BuyerReview r WHERE r.vendor.id = :vendorId AND r.isApproved = true")
    Double getAverageRatingByVendorId(@Param("vendorId") Long vendorId);
    
    @Query("SELECT COUNT(DISTINCT o.user.id) FROM BuyerOrder o JOIN o.items oi WHERE oi.vendor.id = :vendorId")
    long countDistinctCustomersByVendorId(@Param("vendorId") Long vendorId);
    
    @Query("SELECT COUNT(DISTINCT o.user.id) FROM BuyerOrder o JOIN o.items oi WHERE oi.vendor.id = :vendorId AND o.createdAt BETWEEN :startDate AND :endDate")
    long countNewCustomersByVendorIdAndDateRange(@Param("vendorId") Long vendorId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT DISTINCT o FROM BuyerOrder o JOIN o.items oi WHERE oi.vendor.id = :vendorId ORDER BY o.createdAt DESC")
    List<Order> findTop10ByVendorIdOrderByCreatedAtDesc(@Param("vendorId") Long vendorId);
    
    @Query("SELECT SUM(oi.price * oi.quantity) FROM BuyerOrder o JOIN o.items oi WHERE o.paymentStatus = 'PAID'")
    Double sumTotalRevenue();
}

