package com.itech.itech_backend.modules.vendor.repository;

import com.itech.itech_backend.modules.vendor.model.VendorPackage;
import com.itech.itech_backend.modules.vendor.model.VendorPackageTransaction;
import com.itech.itech_backend.modules.vendor.model.Vendors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VendorPackageTransactionRepository extends JpaRepository<VendorPackageTransaction, Long> {
    
    // Find by transaction ID
    Optional<VendorPackageTransaction> findByTransactionId(String transactionId);
    
    // Find by vendor
    List<VendorPackageTransaction> findByVendorOrderByCreatedAtDesc(Vendors vendor);
    
    // Find by vendor and status
    List<VendorPackageTransaction> findByVendorAndStatusOrderByCreatedAtDesc(
            Vendors vendor, VendorPackageTransaction.TransactionStatus status);
    
    // Find successful transactions by vendor
    List<VendorPackageTransaction> findByVendorAndStatusOrderByPaymentDateDesc(
            Vendors vendor, VendorPackageTransaction.TransactionStatus status);
    
    // Find by package
    List<VendorPackageTransaction> findByVendorPackageOrderByCreatedAtDesc(VendorPackage vendorPackage);
    
    // Find pending transactions that expired
    @Query("SELECT vpt FROM VendorPackageTransaction vpt WHERE vpt.status = :status AND vpt.expiryDate < :currentTime")
    List<VendorPackageTransaction> findExpiredTransactions(
            @Param("status") VendorPackageTransaction.TransactionStatus status, 
            @Param("currentTime") LocalDateTime currentTime);
    
    // Get transaction analytics
    @Query("SELECT vpt.status, COUNT(vpt) FROM VendorPackageTransaction vpt GROUP BY vpt.status")
    List<Object[]> getTransactionStatusCounts();
    
    @Query("SELECT vpt.paymentMethod, COUNT(vpt) FROM VendorPackageTransaction vpt WHERE vpt.status = :status GROUP BY vpt.paymentMethod")
    List<Object[]> getPaymentMethodCounts(@Param("status") VendorPackageTransaction.TransactionStatus status);
    
    // Get revenue analytics
    @Query("SELECT SUM(vpt.totalAmount) FROM VendorPackageTransaction vpt WHERE vpt.status = :status")
    BigDecimal getTotalRevenue(@Param("status") VendorPackageTransaction.TransactionStatus status);
    
    @Query("SELECT DATE(vpt.paymentDate), SUM(vpt.totalAmount) FROM VendorPackageTransaction vpt WHERE vpt.status = :status AND vpt.paymentDate >= :fromDate GROUP BY DATE(vpt.paymentDate) ORDER BY DATE(vpt.paymentDate)")
    List<Object[]> getDailyRevenue(
            @Param("status") VendorPackageTransaction.TransactionStatus status, 
            @Param("fromDate") LocalDateTime fromDate);
    
    // Find transactions by date range
    @Query("SELECT vpt FROM VendorPackageTransaction vpt WHERE vpt.paymentDate BETWEEN :startDate AND :endDate ORDER BY vpt.paymentDate DESC")
    List<VendorPackageTransaction> findByPaymentDateBetween(
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
}
