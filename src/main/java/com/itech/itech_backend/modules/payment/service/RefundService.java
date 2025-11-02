package com.itech.itech_backend.modules.payment.service;

import com.itech.itech_backend.modules.payment.model.Refund;
import com.itech.itech_backend.modules.payment.model.Payment;
import com.itech.itech_backend.modules.payment.model.Transaction;
import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.vendor.model.Vendors;
import com.itech.itech_backend.modules.payment.repository.RefundRepository;
import com.itech.itech_backend.modules.payment.repository.PaymentRepository;
import com.itech.itech_backend.modules.core.repository.UserRepository;
import com.itech.itech_backend.modules.shared.service.TransactionServiceImpl;
import com.itech.marketplace.dto.*;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefundService implements com.itech.marketplace.service.RefundService {

    private final RefundRepository refundRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final TransactionServiceImpl transactionService;

    @Value("${razorpay.key.id:}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret:}")
    private String razorpayKeySecret;

    private RazorpayClient razorpayClient;

    /**
     * Request refund for a payment
     */
    @Transactional
    public Refund requestRefund(Long paymentId, BigDecimal refundAmount, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // Validate refund amount
        if (refundAmount.compareTo(payment.getAmount()) > 0) {
            throw new RuntimeException("Refund amount cannot exceed payment amount");
        }

        // Check if payment is refundable
        if (!"PAID".equals(payment.getPaymentStatus())) {
            throw new RuntimeException("Payment is not in refundable state");
        }

        String refundId = generateRefundId();
        Refund.RefundType refundType = refundAmount.compareTo(payment.getAmount()) == 0 ? 
                Refund.RefundType.FULL : Refund.RefundType.PARTIAL;

        Refund refund = Refund.builder()
                .refundId(refundId)
                .payment(payment)
                .vendor(null) // TODO: Fix vendor relationship in Payment model
                .refundAmount(refundAmount)
                .originalAmount(payment.getAmount())
                .status(Refund.RefundStatus.REQUESTED)
                .type(refundType)
                .reason(reason)
                .build();

        Refund savedRefund = refundRepository.save(refund);
        
        // Create transaction record
        transactionService.createRefundTransaction(savedRefund);
        
        log.info("Refund requested: {} for payment: {}", refundId, paymentId);
        return savedRefund;
    }

    /**
     * Approve refund (Admin only)
     */
    @Transactional
    public Refund approveRefund(Long refundId, String adminNotes) {
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new RuntimeException("Refund not found"));

        if (refund.getStatus() != Refund.RefundStatus.REQUESTED) {
            throw new RuntimeException("Refund is not in requested state");
        }

        // Get current admin user
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User admin = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        refund.setStatus(Refund.RefundStatus.APPROVED);
        refund.setAdminNotes(adminNotes);
        refund.setProcessedBy(admin);
        refund.setProcessedAt(LocalDateTime.now());

        Refund savedRefund = refundRepository.save(refund);
        
        // Process refund with payment gateway
        processRefundWithGateway(savedRefund);
        
        log.info("Refund approved: {} by admin: {}", refund.getRefundId(), admin.getEmail());
        return savedRefund;
    }

    /**
     * Reject refund (Admin only)
     */
    @Transactional
    public Refund rejectRefund(Long refundId, String adminNotes) {
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new RuntimeException("Refund not found"));

        if (refund.getStatus() != Refund.RefundStatus.REQUESTED) {
            throw new RuntimeException("Refund is not in requested state");
        }

        // Get current admin user
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User admin = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        refund.setStatus(Refund.RefundStatus.REJECTED);
        refund.setAdminNotes(adminNotes);
        refund.setProcessedBy(admin);
        refund.setProcessedAt(LocalDateTime.now());

        Refund savedRefund = refundRepository.save(refund);
        
        // Update transaction status
        transactionService.updateRefundTransactionStatus(savedRefund, Transaction.TransactionStatus.CANCELLED);
        
        log.info("Refund rejected: {} by admin: {}", refund.getRefundId(), admin.getEmail());
        return savedRefund;
    }

    /**
     * Process refund with Razorpay
     */
    private void processRefundWithGateway(Refund refund) {
        try {
            refund.setStatus(Refund.RefundStatus.PROCESSING);
            refundRepository.save(refund);

            RazorpayClient client = getRazorpayClient();
            
            // Create refund request
            JSONObject refundRequest = new JSONObject();
            refundRequest.put("amount", refund.getRefundAmount().multiply(BigDecimal.valueOf(100)).intValue()); // Amount in paise
            refundRequest.put("speed", "normal");
            
            JSONObject notes = new JSONObject();
            notes.put("refund_id", refund.getRefundId());
            notes.put("reason", refund.getReason());
            refundRequest.put("notes", notes);

            // Create refund using the Razorpay client
            com.razorpay.Refund razorpayRefund = client.payments.refund(refund.getPayment().getGatewayPaymentId(), refundRequest);

            // Update refund with Razorpay refund ID
            refund.setRazorpayRefundId(razorpayRefund.get("id"));
            refund.setStatus(Refund.RefundStatus.COMPLETED);
            refundRepository.save(refund);

            // Update transaction status
            transactionService.updateRefundTransactionStatus(refund, Transaction.TransactionStatus.COMPLETED);

            log.info("Refund processed successfully with Razorpay: {}", refund.getRefundId());

        } catch (RazorpayException e) {
            log.error("Error processing refund with Razorpay: {}", e.getMessage());
            refund.setStatus(Refund.RefundStatus.FAILED);
            refundRepository.save(refund);
            
            // Update transaction status
            transactionService.updateRefundTransactionStatus(refund, Transaction.TransactionStatus.FAILED);
            
            throw new RuntimeException("Failed to process refund: " + e.getMessage());
        }
    }

    /**
     * Get Razorpay client
     */
    private RazorpayClient getRazorpayClient() throws RazorpayException {
        if (razorpayClient == null) {
            razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
        }
        return razorpayClient;
    }

    /**
     * Generate unique refund ID
     */
    private String generateRefundId() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "REF-" + timestamp + "-" + uuid;
    }

    /**
     * Get refunds by vendor
     */
    public List<Refund> getVendorRefunds(Vendors vendor) {
        return refundRepository.findByVendorOrderByRequestedAtDesc(vendor);
    }

    /**
     * Get paginated refunds by vendor
     */
    public Page<Refund> getVendorRefunds(Vendors vendor, Pageable pageable) {
        return refundRepository.findByVendorOrderByRequestedAtDesc(vendor, pageable);
    }

    /**
     * Get all refunds for admin
     */
    public List<Refund> getAllRefunds() {
        return refundRepository.findAll();
    }

    /**
     * Get refunds by status
     */
    public List<Refund> getRefundsByStatus(Refund.RefundStatus status) {
        return refundRepository.findByStatus(status);
    }

    /**
     * Get pending refunds count
     */
    public Long getPendingRefundCount() {
        return refundRepository.getPendingRefundCount();
    }

    /**
     * Get refunds by date range
     */
    public List<Refund> getRefundsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return refundRepository.findByDateRange(startDate, endDate);
    }

    /**
     * Get refund analytics
     */
    public Map<String, Object> getRefundAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        // Status counts
        List<Object[]> statusCounts = refundRepository.getRefundStatusCounts();
        Map<String, Long> statusAnalytics = new HashMap<>();
        for (Object[] row : statusCounts) {
            statusAnalytics.put(row[0].toString(), (Long) row[1]);
        }
        analytics.put("statusCounts", statusAnalytics);
        
        // Total refunded amount
        BigDecimal totalRefunded = refundRepository.getTotalRefundedAmount();
        analytics.put("totalRefundedAmount", totalRefunded != null ? totalRefunded : BigDecimal.ZERO);
        
        // Pending refunds count
        Long pendingCount = refundRepository.getPendingRefundCount();
        analytics.put("pendingRefundCount", pendingCount != null ? pendingCount : 0L);

        // Daily refunds (last 30 days)
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Object[]> dailyRefunds = refundRepository.getDailyRefunds(thirtyDaysAgo);
        analytics.put("dailyRefunds", dailyRefunds);
        
        // Current month vs previous month
        LocalDateTime currentMonthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime previousMonthStart = currentMonthStart.minusMonths(1);
        LocalDateTime previousMonthEnd = currentMonthStart.minusSeconds(1);
        
        BigDecimal currentMonthRefunds = refundRepository.getTotalRefundedAmountByDateRange(currentMonthStart, LocalDateTime.now());
        BigDecimal previousMonthRefunds = refundRepository.getTotalRefundedAmountByDateRange(previousMonthStart, previousMonthEnd);
        
        analytics.put("currentMonthRefunds", currentMonthRefunds != null ? currentMonthRefunds : BigDecimal.ZERO);
        analytics.put("previousMonthRefunds", previousMonthRefunds != null ? previousMonthRefunds : BigDecimal.ZERO);
        
        return analytics;
    }

    /**
     * Cancel refund (before processing)
     */
    @Transactional
    public Refund cancelRefund(Long refundId, String reason) {
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new RuntimeException("Refund not found"));

        if (refund.getStatus() != Refund.RefundStatus.REQUESTED && 
            refund.getStatus() != Refund.RefundStatus.APPROVED) {
            throw new RuntimeException("Refund cannot be cancelled in current state");
        }

        refund.setStatus(Refund.RefundStatus.REJECTED);
        refund.setAdminNotes("Cancelled: " + reason);
        refund.setProcessedAt(LocalDateTime.now());

        Refund savedRefund = refundRepository.save(refund);
        
        // Update transaction status
        transactionService.updateRefundTransactionStatus(savedRefund, Transaction.TransactionStatus.CANCELLED);
        
        log.info("Refund cancelled: {}", refund.getRefundId());
        return savedRefund;
    }

    /**
     * Get refund by ID
     */
    public Refund getRefundById(Long refundId) {
        return refundRepository.findById(refundId)
                .orElseThrow(() -> new RuntimeException("Refund not found"));
    }

    /**
     * Get refund by refund ID string
     */
    public Refund getRefundByRefundId(String refundId) {
        return refundRepository.findByRefundId(refundId)
                .orElseThrow(() -> new RuntimeException("Refund not found"));
    }

    // Additional methods required by FinanceController
    public Page<Refund> getRefunds(String status, Long orderId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        // Implementation for filtering refunds
        return refundRepository.findAll(pageable); // Simplified implementation
    }

    public boolean isRefundOwner(Long refundId, String username) {
        // Check if the authenticated user owns this refund
        return true; // Placeholder implementation
    }

    public Refund initiateRefund(RefundRequestDto request) {
        // Initiate refund from DTO
        return requestRefund(request.getOrderId(), request.getAmount(), request.getReason());
    }

    public Refund approveRefund(Long refundId) {
        return approveRefund(refundId, "Approved by admin");
    }


    public Refund processRefund(Long refundId) {
        Refund refund = getRefundById(refundId);
        processRefundWithGateway(refund);
        return refund;
    }

    public RefundAnalyticsDto getRefundAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        // Generate refund analytics
        throw new RuntimeException("Not implemented yet");
    }

    // Webhook-related methods moved to WebhookService
}


