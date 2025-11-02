package com.itech.itech_backend.modules.payment.repository;

import com.itech.itech_backend.modules.payment.model.Invoice;
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
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    List<Invoice> findByVendorOrderByCreatedAtDesc(Vendors vendor);

    Page<Invoice> findByVendorOrderByCreatedAtDesc(Vendors vendor, Pageable pageable);

    @Query("SELECT i FROM Invoice i WHERE i.status = :status ORDER BY i.createdAt DESC")
    List<Invoice> findByStatus(@Param("status") Invoice.InvoiceStatus status);

    @Query("SELECT i FROM Invoice i WHERE i.createdAt BETWEEN :startDate AND :endDate ORDER BY i.createdAt DESC")
    List<Invoice> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT i FROM Invoice i WHERE i.vendor = :vendor AND i.status = :status ORDER BY i.createdAt DESC")
    List<Invoice> findByVendorAndStatus(@Param("vendor") Vendors vendor, @Param("status") Invoice.InvoiceStatus status);

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.status = 'PAID'")
    BigDecimal getTotalPaidAmount();

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.status = 'PAID' AND i.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal getTotalPaidAmountByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT i.status as status, COUNT(i) as count FROM Invoice i GROUP BY i.status")
    List<Object[]> getInvoiceStatusCounts();

    @Query("SELECT CAST(i.createdAt as date) as date, SUM(i.totalAmount) as amount FROM Invoice i WHERE i.status = 'PAID' AND i.createdAt >= :startDate GROUP BY CAST(i.createdAt as date) ORDER BY CAST(i.createdAt as date)")
    List<Object[]> getDailyRevenue(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT i.type as type, COUNT(i) as count, SUM(i.totalAmount) as total FROM Invoice i WHERE i.status = 'PAID' GROUP BY i.type")
    List<Object[]> getRevenueByType();
}

