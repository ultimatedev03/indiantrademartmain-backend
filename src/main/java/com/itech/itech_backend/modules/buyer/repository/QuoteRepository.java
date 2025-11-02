package com.itech.itech_backend.modules.buyer.repository;

import com.itech.itech_backend.modules.buyer.model.Quote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {
    List<Quote> findByVendorId(Long vendorId);
    List<Quote> findByInquiryId(Long inquiryId);
    List<Quote> findByInquiryUserId(Long userId);
    
    Page<Quote> findByVendorIdOrderByCreatedAtDesc(Long vendorId, Pageable pageable);
    Page<Quote> findByInquiryUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    List<Quote> findByInquiryIdOrderByCreatedAtDesc(Long inquiryId);
    
    @Query("SELECT COUNT(q) FROM Quote q WHERE q.vendor.id = :vendorId")
    Long countByVendorId(@Param("vendorId") Long vendorId);
    
    @Query("SELECT COUNT(q) FROM Quote q WHERE q.vendor.id = :vendorId AND q.isAccepted = true")
    Long countAcceptedQuotesByVendorId(@Param("vendorId") Long vendorId);
    
    List<Quote> findByIsAcceptedTrueOrderByCreatedAtDesc();
    List<Quote> findByIsAcceptedTrue();
    List<Quote> findByIsAcceptedFalse();
    
    @Query("SELECT q FROM Quote q WHERE q.vendor.id = :vendorId AND q.isAccepted = true")
    List<Quote> findAcceptedQuotesByVendor(@Param("vendorId") Long vendorId);
    
    // Additional methods for analytics
    @Query("SELECT COUNT(q) FROM Quote q WHERE q.vendor.id = :vendorId AND q.isAccepted = :isAccepted")
    long countByVendorIdAndIsAccepted(@Param("vendorId") Long vendorId, @Param("isAccepted") boolean isAccepted);
    
    @Query("SELECT COUNT(q) FROM Quote q WHERE q.vendor.id = :vendorId AND q.isAccepted = false")
    long countPendingQuotesByVendorId(@Param("vendorId") Long vendorId);
    
    @Query("SELECT COUNT(q) FROM Quote q WHERE q.vendor.id = :vendorId AND q.isAccepted = true")
    long countAcceptedQuotesByVendorIdAlternate(@Param("vendorId") Long vendorId);
}

