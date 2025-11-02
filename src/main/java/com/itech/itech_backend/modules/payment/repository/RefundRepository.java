package com.itech.itech_backend.modules.payment.repository;

import com.itech.itech_backend.modules.payment.model.Refund;
import com.itech.itech_backend.modules.vendor.model.Vendors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {

    Optional<Refund> findByRefundId(String refundId);

    Optional<Refund> findByRazorpayRefundId(String razorpayRefundId);

    List<Refund> findByVendorOrderByRequestedAtDesc(Vendors vendor);

    Page<Refund> findByVendorOrderByRequestedAtDesc(Vendors vendor, Pageable pageable);

    @Query("SELECT r FROM Refund r WHERE r.status = :status ORDER BY r.requestedAt DESC")
    List<Refund> findByStatus(@Param("status") Refund.RefundStatus status);

    @Query("SELECT r FROM Refund r WHERE r.requestedAt BETWEEN :startDate AND :endDate ORDER BY r.requestedAt DESC")
    List<Refund> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT r FROM Refund r WHERE r.vendor = :vendor AND r.status = :status ORDER BY r.requestedAt DESC")
    List<Refund> findByVendorAndStatus(@Param("vendor") Vendors vendor, @Param("status") Refund.RefundStatus status);

    @Query("SELECT SUM(r.refundAmount) FROM Refund r WHERE r.status = 'COMPLETED'")
    BigDecimal getTotalRefundedAmount();

    @Query("SELECT SUM(r.refundAmount) FROM Refund r WHERE r.status = 'COMPLETED' AND r.processedAt BETWEEN :startDate AND :endDate")
    BigDecimal getTotalRefundedAmountByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT r.status as status, COUNT(r) as count FROM Refund r GROUP BY r.status")
    List<Object[]> getRefundStatusCounts();

    @Query("SELECT DATE(r.processedAt) as date, SUM(r.refundAmount) as amount FROM Refund r WHERE r.status = 'COMPLETED' AND r.processedAt >= :startDate GROUP BY DATE(r.processedAt) ORDER BY DATE(r.processedAt)")
    List<Object[]> getDailyRefunds(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT COUNT(r) FROM Refund r WHERE r.status = 'REQUESTED'")
    Long getPendingRefundCount();
}

