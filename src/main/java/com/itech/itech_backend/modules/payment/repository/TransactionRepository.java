package com.itech.itech_backend.modules.payment.repository;

import com.itech.itech_backend.modules.payment.model.Transaction;
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
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionId(String transactionId);

    List<Transaction> findByVendorOrderByCreatedAtDesc(Vendors vendor);

    Page<Transaction> findByVendorOrderByCreatedAtDesc(Vendors vendor, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.status = :status ORDER BY t.createdAt DESC")
    List<Transaction> findByStatus(@Param("status") Transaction.TransactionStatus status);

    @Query("SELECT t FROM Transaction t WHERE t.type = :type ORDER BY t.createdAt DESC")
    List<Transaction> findByType(@Param("type") Transaction.TransactionType type);

    @Query("SELECT t FROM Transaction t WHERE t.createdAt BETWEEN :startDate AND :endDate ORDER BY t.createdAt DESC")
    List<Transaction> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.status = 'COMPLETED' AND t.type = :type")
    BigDecimal getTotalAmountByType(@Param("type") Transaction.TransactionType type);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.status = 'COMPLETED' AND t.type = :type AND t.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal getTotalAmountByTypeAndDateRange(@Param("type") Transaction.TransactionType type, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT t.type as type, COUNT(t) as count, SUM(t.amount) as total FROM Transaction t WHERE t.status = 'COMPLETED' GROUP BY t.type")
    List<Object[]> getTransactionSummaryByType();

    @Query("SELECT t.status as status, COUNT(t) as count FROM Transaction t GROUP BY t.status")
    List<Object[]> getTransactionStatusCounts();

    @Query("SELECT DATE(t.createdAt) as date, SUM(t.amount) as amount FROM Transaction t WHERE t.status = 'COMPLETED' AND t.type = 'PAYMENT' AND t.createdAt >= :startDate GROUP BY DATE(t.createdAt) ORDER BY DATE(t.createdAt)")
    List<Object[]> getDailyTransactionVolume(@Param("startDate") LocalDateTime startDate);
}

