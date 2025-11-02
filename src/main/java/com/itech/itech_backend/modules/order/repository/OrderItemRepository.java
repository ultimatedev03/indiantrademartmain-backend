package com.itech.itech_backend.modules.order.repository;

import com.itech.itech_backend.modules.order.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    // ===============================
    // BASIC QUERIES
    // ===============================
    
    List<OrderItem> findByOrderId(Long orderId);
    
    List<OrderItem> findByProductId(Long productId);
    
    List<OrderItem> findByVendorId(Long vendorId);
    
    List<OrderItem> findByOrderIdAndVendorId(Long orderId, Long vendorId);
    
    // ===============================
    // STATUS-BASED QUERIES
    // ===============================
    
    List<OrderItem> findByFulfillmentStatus(OrderItem.ItemFulfillmentStatus fulfillmentStatus);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId AND oi.fulfillmentStatus = :status")
    List<OrderItem> findByOrderIdAndFulfillmentStatus(@Param("orderId") Long orderId, 
                                                       @Param("status") OrderItem.ItemFulfillmentStatus status);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.vendor.id = :vendorId AND oi.fulfillmentStatus = :status")
    List<OrderItem> findByVendorIdAndFulfillmentStatus(@Param("vendorId") Long vendorId, 
                                                        @Param("status") OrderItem.ItemFulfillmentStatus status);
    
    // ===============================
    // PRODUCT-SPECIFIC QUERIES
    // ===============================
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.product.id = :productId AND oi.order.orderStatus = 'COMPLETED'")
    List<OrderItem> findCompletedOrderItemsByProduct(@Param("productId") Long productId);
    
    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.product.id = :productId")
    Long countOrderItemsByProduct(@Param("productId") Long productId);
    
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.product.id = :productId AND oi.order.orderStatus = 'COMPLETED'")
    Integer getTotalQuantitySoldForProduct(@Param("productId") Long productId);
    
    @Query("SELECT SUM(oi.totalPrice) FROM OrderItem oi WHERE oi.product.id = :productId AND oi.order.orderStatus = 'COMPLETED'")
    BigDecimal getTotalRevenueForProduct(@Param("productId") Long productId);
    
    // ===============================
    // VENDOR-SPECIFIC QUERIES
    // ===============================
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.vendor.id = :vendorId AND oi.order.orderStatus = 'COMPLETED'")
    List<OrderItem> findCompletedOrderItemsByVendor(@Param("vendorId") Long vendorId);
    
    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.vendor.id = :vendorId")
    Long countOrderItemsByVendor(@Param("vendorId") Long vendorId);
    
    @Query("SELECT SUM(oi.totalPrice) FROM OrderItem oi WHERE oi.vendor.id = :vendorId AND oi.order.orderStatus = 'COMPLETED'")
    BigDecimal getTotalRevenueByVendor(@Param("vendorId") Long vendorId);
    
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.vendor.id = :vendorId AND oi.order.orderStatus = 'COMPLETED'")
    Integer getTotalQuantitySoldByVendor(@Param("vendorId") Long vendorId);
    
    // ===============================
    // FULFILLMENT QUERIES
    // ===============================
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.fulfillmentStatus = 'PENDING' ORDER BY oi.createdAt ASC")
    List<OrderItem> findPendingFulfillmentItems();
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.quantityShipped < oi.quantity AND oi.fulfillmentStatus NOT IN ('CANCELLED', 'REFUNDED')")
    List<OrderItem> findPartiallyShippedItems();
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.quantityDelivered < oi.quantityShipped")
    List<OrderItem> findItemsInTransit();
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.expectedDeliveryDate < CURRENT_TIMESTAMP AND oi.quantityDelivered < oi.quantity")
    List<OrderItem> findOverdueDeliveries();
    
    // ===============================
    // ANALYTICS QUERIES
    // ===============================
    
    @Query("SELECT oi.productName, COUNT(oi), SUM(oi.quantity) FROM OrderItem oi " +
           "WHERE oi.order.orderStatus = 'COMPLETED' " +
           "GROUP BY oi.productName ORDER BY COUNT(oi) DESC")
    List<Object[]> getTopSellingProducts();
    
    @Query("SELECT oi.productCategory, COUNT(oi), SUM(oi.totalPrice) FROM OrderItem oi " +
           "WHERE oi.order.orderStatus = 'COMPLETED' " +
           "GROUP BY oi.productCategory ORDER BY SUM(oi.totalPrice) DESC")
    List<Object[]> getTopRevenueCategories();
    
    @Query("SELECT oi.vendor.businessName, COUNT(oi), SUM(oi.totalPrice) FROM OrderItem oi " +
           "WHERE oi.order.orderStatus = 'COMPLETED' " +
           "GROUP BY oi.vendor.businessName ORDER BY SUM(oi.totalPrice) DESC")
    List<Object[]> getTopVendorsByRevenue();
    
    // ===============================
    // INVENTORY QUERIES
    // ===============================
    
    @Query("SELECT oi.productSku, SUM(oi.quantity) FROM OrderItem oi " +
           "WHERE oi.fulfillmentStatus IN ('PENDING', 'CONFIRMED', 'PROCESSING', 'PACKED') " +
           "GROUP BY oi.productSku")
    List<Object[]> getPendingInventoryRequirements();
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.requiresShipping = true AND oi.fulfillmentStatus = 'CONFIRMED'")
    List<OrderItem> findItemsReadyForShipping();
    
    // ===============================
    // RETURN AND REFUND QUERIES
    // ===============================
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.quantityReturned > 0")
    List<OrderItem> findItemsWithReturns();
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.refundedAmount > 0")
    List<OrderItem> findRefundedItems();
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.isReturnable = true AND oi.order.deliveredAt IS NOT NULL")
    List<OrderItem> findReturnableItems();
    
    // ===============================
    // SEARCH QUERIES
    // ===============================
    
    @Query("SELECT oi FROM OrderItem oi WHERE " +
           "oi.productName LIKE %:searchTerm% OR " +
           "oi.productSku LIKE %:searchTerm% OR " +
           "oi.productBrand LIKE %:searchTerm% OR " +
           "oi.productCategory LIKE %:searchTerm%")
    List<OrderItem> searchOrderItems(@Param("searchTerm") String searchTerm);
    
    // ===============================
    // GIFT AND SPECIAL HANDLING
    // ===============================
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.isGift = true")
    List<OrderItem> findGiftItems();
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.specialInstructions IS NOT NULL AND oi.specialInstructions != ''")
    List<OrderItem> findItemsWithSpecialInstructions();
    
    // ===============================
    // TAX AND COMPLIANCE QUERIES
    // ===============================
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.hsnCode = :hsnCode")
    List<OrderItem> findByHsnCode(@Param("hsnCode") String hsnCode);
    
    @Query("SELECT oi.hsnCode, COUNT(oi), SUM(oi.taxAmount) FROM OrderItem oi " +
           "WHERE oi.hsnCode IS NOT NULL " +
           "GROUP BY oi.hsnCode")
    List<Object[]> getTaxSummaryByHsn();
    
    // ===============================
    // CUSTOM BUSINESS QUERIES
    // ===============================
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.totalPrice > :amount")
    List<OrderItem> findHighValueItems(@Param("amount") BigDecimal amount);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.quantity >= :minQuantity")
    List<OrderItem> findBulkOrders(@Param("minQuantity") Integer minQuantity);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.itemDiscountAmount > 0 OR oi.volumeDiscountAmount > 0 OR oi.promotionalDiscountAmount > 0")
    List<OrderItem> findDiscountedItems();
}
