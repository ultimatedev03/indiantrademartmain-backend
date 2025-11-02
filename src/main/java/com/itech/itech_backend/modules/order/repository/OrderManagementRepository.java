package com.itech.itech_backend.modules.order.repository;

import com.itech.itech_backend.modules.order.model.Order;
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
public interface OrderManagementRepository extends JpaRepository<Order, Long> {
    
    // ===============================
    // BASIC QUERIES
    // ===============================
    
    Optional<Order> findByOrderNumber(String orderNumber);
    
    List<Order> findByBuyerId(Long buyerId);
    
    List<Order> findByPrimaryVendorId(Long vendorId);
    
    Page<Order> findByBuyerId(Long buyerId, Pageable pageable);
    
    Page<Order> findByPrimaryVendorId(Long vendorId, Pageable pageable);
    
    // ===============================
    // STATUS-BASED QUERIES
    // ===============================
    
    List<Order> findByOrderStatus(Order.OrderStatus orderStatus);
    
    List<Order> findByPaymentStatus(Order.PaymentStatus paymentStatus);
    
    List<Order> findByFulfillmentStatus(Order.FulfillmentStatus fulfillmentStatus);
    
    Page<Order> findByOrderStatusIn(List<Order.OrderStatus> statuses, Pageable pageable);
    
    // ===============================
    // BUYER-SPECIFIC QUERIES
    // ===============================
    
    @Query("SELECT o FROM Order o WHERE o.buyer.id = :buyerId AND o.orderStatus = :status")
    List<Order> findByBuyerIdAndStatus(@Param("buyerId") Long buyerId, 
                                       @Param("status") Order.OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.buyer.id = :buyerId AND o.createdAt >= :fromDate")
    List<Order> findRecentOrdersByBuyer(@Param("buyerId") Long buyerId, 
                                        @Param("fromDate") LocalDateTime fromDate);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.buyer.id = :buyerId")
    Long countOrdersByBuyer(@Param("buyerId") Long buyerId);
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.buyer.id = :buyerId AND o.orderStatus = 'COMPLETED'")
    BigDecimal getTotalSpentByBuyer(@Param("buyerId") Long buyerId);
    
    // ===============================
    // VENDOR-SPECIFIC QUERIES
    // ===============================
    
    @Query("SELECT o FROM Order o WHERE o.primaryVendor.id = :vendorId AND o.orderStatus = :status")
    List<Order> findByVendorIdAndStatus(@Param("vendorId") Long vendorId, 
                                        @Param("status") Order.OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.primaryVendor.id = :vendorId AND o.createdAt >= :fromDate")
    List<Order> findRecentOrdersByVendor(@Param("vendorId") Long vendorId, 
                                         @Param("fromDate") LocalDateTime fromDate);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.primaryVendor.id = :vendorId")
    Long countOrdersByVendor(@Param("vendorId") Long vendorId);
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.primaryVendor.id = :vendorId AND o.orderStatus = 'COMPLETED'")
    BigDecimal getTotalRevenueByVendor(@Param("vendorId") Long vendorId);
    
    // ===============================
    // DATE RANGE QUERIES
    // ===============================
    
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findOrdersByDateRange(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate AND o.orderStatus = :status")
    List<Order> findOrdersByDateRangeAndStatus(@Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate,
                                               @Param("status") Order.OrderStatus status);
    
    // ===============================
    // AMOUNT-BASED QUERIES
    // ===============================
    
    @Query("SELECT o FROM Order o WHERE o.totalAmount BETWEEN :minAmount AND :maxAmount")
    List<Order> findOrdersByAmountRange(@Param("minAmount") BigDecimal minAmount, 
                                        @Param("maxAmount") BigDecimal maxAmount);
    
    @Query("SELECT o FROM Order o WHERE o.totalAmount >= :amount")
    List<Order> findHighValueOrders(@Param("amount") BigDecimal amount);
    
    // ===============================
    // PENDING ACTIONS QUERIES
    // ===============================
    
    @Query("SELECT o FROM Order o WHERE o.orderStatus = 'PENDING' AND o.createdAt < :cutoffTime")
    List<Order> findPendingOrdersOlderThan(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    @Query("SELECT o FROM Order o WHERE o.paymentStatus = 'PENDING' AND o.paymentDueDate < :currentTime")
    List<Order> findOverduePayments(@Param("currentTime") LocalDateTime currentTime);
    
    @Query("SELECT o FROM Order o WHERE o.fulfillmentStatus = 'UNFULFILLED' AND o.requiredByDate < :currentTime")
    List<Order> findOverdueFulfillments(@Param("currentTime") LocalDateTime currentTime);
    
    // ===============================
    // ANALYTICS QUERIES
    // ===============================
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt >= :fromDate")
    Long countOrdersFromDate(@Param("fromDate") LocalDateTime fromDate);
    
    @Query("SELECT o.orderStatus, COUNT(o) FROM Order o GROUP BY o.orderStatus")
    List<Object[]> getOrderCountByStatus();
    
    @Query("SELECT CAST(o.createdAt as date), COUNT(o) FROM Order o WHERE o.createdAt >= :fromDate GROUP BY CAST(o.createdAt as date)")
    List<Object[]> getDailyOrderCounts(@Param("fromDate") LocalDateTime fromDate);
    
    @Query("SELECT EXTRACT(MONTH FROM o.createdAt), COUNT(o), SUM(o.totalAmount) FROM Order o WHERE o.createdAt >= :fromDate GROUP BY EXTRACT(MONTH FROM o.createdAt)")
    List<Object[]> getMonthlyOrderStats(@Param("fromDate") LocalDateTime fromDate);
    
    // ===============================
    // SEARCH QUERIES
    // ===============================
    
    @Query("SELECT o FROM Order o WHERE " +
           "o.orderNumber LIKE %:searchTerm% OR " +
           "o.referenceNumber LIKE %:searchTerm% OR " +
           "o.poNumber LIKE %:searchTerm% OR " +
           "o.buyer.email LIKE %:searchTerm% OR " +
           "o.buyer.firstName LIKE %:searchTerm% OR " +
           "o.buyer.lastName LIKE %:searchTerm%")
    Page<Order> searchOrders(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // ===============================
    // COMPANY-SPECIFIC QUERIES
    // ===============================
    
    List<Order> findByCompanyId(Long companyId);
    
    @Query("SELECT o FROM Order o WHERE o.company.id = :companyId AND o.orderStatus = :status")
    List<Order> findByCompanyIdAndStatus(@Param("companyId") Long companyId, 
                                         @Param("status") Order.OrderStatus status);
    
    // ===============================
    // PRIORITY-BASED QUERIES
    // ===============================
    
    List<Order> findByPriority(Order.OrderPriority priority);
    
    @Query("SELECT o FROM Order o WHERE o.isUrgent = true AND o.orderStatus IN ('PENDING', 'CONFIRMED')")
    List<Order> findUrgentPendingOrders();
    
    // ===============================
    // RECENT ACTIVITY
    // ===============================
    
    @Query("SELECT o FROM Order o ORDER BY o.updatedAt DESC")
    List<Order> findRecentlyUpdatedOrders(Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE o.orderStatus IN ('PENDING', 'CONFIRMED', 'PROCESSING') ORDER BY o.createdAt ASC")
    List<Order> findActiveOrdersOldestFirst(Pageable pageable);
}
