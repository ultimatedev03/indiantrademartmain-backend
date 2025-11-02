package com.itech.itech_backend.modules.buyer.repository;

import com.itech.itech_backend.modules.buyer.model.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    
    Page<Inquiry> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    Page<Inquiry> findByProductVendorIdOrderByCreatedAtDesc(Long vendorId, Pageable pageable);
    
    List<Inquiry> findByProductVendorIdAndIsResolvedFalseOrderByCreatedAtDesc(Long vendorId);
    
    Long countByProductVendorIdAndIsResolvedFalse(Long vendorId);
    
    @Query("SELECT COUNT(i) FROM BuyerInquiry i WHERE i.product.vendor.id = :vendorId")
    Long countByVendorId(@Param("vendorId") Long vendorId);
    
    List<Inquiry> findByProductIdOrderByCreatedAtDesc(Long productId);
    
    // Analytics methods
    long countByIsResolvedTrue();
    long countByCreatedAtAfter(java.time.LocalDateTime date);
}

