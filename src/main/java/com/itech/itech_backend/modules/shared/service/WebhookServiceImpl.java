package com.itech.itech_backend.modules.shared.service;

import com.itech.itech_backend.modules.payment.model.*;
import com.itech.itech_backend.modules.vendor.model.*;
import com.itech.itech_backend.modules.payment.repository.RefundRepository;
import com.itech.itech_backend.modules.payment.repository.PaymentRepository;
import com.itech.itech_backend.modules.shared.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookServiceImpl implements WebhookService {

    private final RefundRepository refundRepository;
    private final PaymentRepository paymentRepository;
    private final TransactionServiceImpl transactionService;

    @Override
    public void processRazorpayWebhook(JSONObject payload) {
        String event = payload.optString("event");

        if ("refund.processed".equals(event)) {
            JSONObject refundEntity = payload.optJSONObject("payload").optJSONObject("refund").optJSONObject("entity");
            createRefundFromWebhook(refundEntity);
        }
    }

    /**
     * Create refund from webhook data
     */
    @Transactional
    private void createRefundFromWebhook(JSONObject refundData) {
        try {
            // Extract data from JSONObject
            String refundId = refundData.optString("id");
            String paymentId = refundData.optString("payment_id");
            BigDecimal refundAmount = BigDecimal.valueOf(refundData.optDouble("amount", 0.0) / 100.0); // Convert from paise to rupees
            String status = refundData.optString("status", "REQUESTED");
            
            // Find existing refund by Razorpay refund ID or create new one
            Refund existingRefund = refundRepository.findByRazorpayRefundId(refundId).orElse(null);
            
            if (existingRefund != null) {
                // Update existing refund status
                updateRefundStatus(existingRefund, status);
                log.info("Updated existing refund from webhook: {}", refundId);
            } else {
                // Find payment by Razorpay payment ID
                Payment payment = paymentRepository.findByGatewayPaymentId(paymentId).orElse(null);
                
                if (payment != null) {
                    // Create new refund record
                    createNewRefundFromWebhook(payment, refundId, refundAmount, status);
                    log.info("Created new refund from webhook: {}", refundId);
                } else {
                    log.warn("Payment not found for webhook refund: {}", paymentId);
                }
            }
            
        } catch (Exception e) {
            log.error("Error processing webhook refund data: {}", e.getMessage(), e);
        }
    }

    private void updateRefundStatus(Refund refund, String status) {
        Refund.RefundStatus refundStatus = parseRefundStatus(status);
        refund.setStatus(refundStatus);
        refund.setProcessedAt(LocalDateTime.now());
        refundRepository.save(refund);
        
        // Update transaction status
        Transaction.TransactionStatus transactionStatus = getTransactionStatusFromRefundStatus(refundStatus);
        transactionService.updateRefundTransactionStatus(refund, transactionStatus);
    }

    private void createNewRefundFromWebhook(Payment payment, String razorpayRefundId, BigDecimal refundAmount, String status) {
        Refund.RefundType refundType = refundAmount.compareTo(payment.getAmount()) == 0 ? 
                Refund.RefundType.FULL : Refund.RefundType.PARTIAL;
        
        Refund.RefundStatus refundStatus = parseRefundStatus(status);
        
        Refund refund = Refund.builder()
                .refundId(generateRefundId())
                .payment(payment)
                .vendor(null) // TODO: Fix vendor relationship in Payment model
                .refundAmount(refundAmount)
                .originalAmount(payment.getAmount())
                .status(refundStatus)
                .type(refundType)
                .reason("Webhook initiated refund")
                .razorpayRefundId(razorpayRefundId)
                .processedAt(LocalDateTime.now())
                .build();
        
        Refund savedRefund = refundRepository.save(refund);
        
        // Create transaction record
        transactionService.createRefundTransaction(savedRefund);
        
        // Update transaction status if completed
        if (refundStatus == Refund.RefundStatus.COMPLETED) {
            transactionService.updateRefundTransactionStatus(savedRefund, Transaction.TransactionStatus.COMPLETED);
        }
    }

    private Refund.RefundStatus parseRefundStatus(String status) {
        try {
            switch (status.toLowerCase()) {
                case "processed":
                case "completed":
                    return Refund.RefundStatus.COMPLETED;
                case "failed":
                    return Refund.RefundStatus.FAILED;
                case "pending":
                    return Refund.RefundStatus.PROCESSING;
                default:
                    return Refund.RefundStatus.REQUESTED;
            }
        } catch (Exception e) {
            return Refund.RefundStatus.REQUESTED;
        }
    }

    private Transaction.TransactionStatus getTransactionStatusFromRefundStatus(Refund.RefundStatus refundStatus) {
        switch (refundStatus) {
            case COMPLETED:
                return Transaction.TransactionStatus.COMPLETED;
            case FAILED:
                return Transaction.TransactionStatus.FAILED;
            case PROCESSING:
                return Transaction.TransactionStatus.PENDING;
            default:
                return Transaction.TransactionStatus.PENDING;
        }
    }

    private String generateRefundId() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "REF-" + timestamp + "-" + uuid;
    }
}


