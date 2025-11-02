package com.itech.itech_backend.modules.rfq.repository;

import com.itech.itech_backend.modules.rfq.model.RFQ;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RFQRepository extends JpaRepository<RFQ, Long> {
    
    List<RFQ> findByBuyer_IdOrderByCreatedAtDesc(Long buyerId);
    
    Page<RFQ> findByBuyer_Id(Long buyerId, Pageable pageable);
    
    Page<RFQ> findByStatus(RFQ.RFQStatus status, Pageable pageable);
    
    @Query("SELECT r FROM RFQ r WHERE r.status = 'OPEN' AND r.validUntil > :currentDate " +
           "ORDER BY r.createdAt DESC")
    List<RFQ> findActiveRFQs(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT r FROM RFQ r WHERE r.status = 'OPEN' AND r.validUntil > :currentDate " +
           "AND (:category IS NULL OR r.category = :category) " +
           "ORDER BY r.createdAt DESC")
    List<RFQ> findActiveRFQsByCategory(@Param("currentDate") LocalDateTime currentDate,
                                      @Param("category") String category);
    
    @Query("SELECT r FROM RFQ r WHERE r.title LIKE %:keyword% OR r.description LIKE %:keyword% " +
           "ORDER BY r.createdAt DESC")
    List<RFQ> searchRFQs(@Param("keyword") String keyword);
    
    @Query("SELECT COUNT(r) FROM RFQ r WHERE r.buyer.id = :buyerId AND r.status = :status")
    Long countByBuyerIdAndStatus(@Param("buyerId") Long buyerId, 
                                @Param("status") RFQ.RFQStatus status);
    
    List<RFQ> findByValidUntilBeforeAndStatus(LocalDateTime validUntil, RFQ.RFQStatus status);
}
