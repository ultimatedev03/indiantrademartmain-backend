package com.itech.marketplace.service;

import com.itech.itech_backend.modules.payment.model.Refund;
import com.itech.marketplace.dto.RefundRequestDto;
import com.itech.marketplace.dto.RefundAnalyticsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

/**
 * RefundService interface for marketplace operations
 */
public interface RefundService {
    
    // Core refund operations
    Refund getRefundById(Long refundId);
    Refund initiateRefund(RefundRequestDto request);
    Refund approveRefund(Long refundId);
    Refund rejectRefund(Long refundId, String reason);
    Refund processRefund(Long refundId);
    
    // Query operations
    Page<Refund> getRefunds(String status, Long orderId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    boolean isRefundOwner(Long refundId, String username);
    
    // Analytics
    RefundAnalyticsDto getRefundAnalytics(LocalDateTime startDate, LocalDateTime endDate);
}

