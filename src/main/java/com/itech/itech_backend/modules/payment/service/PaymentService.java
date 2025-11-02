package com.itech.itech_backend.modules.payment.service;

import com.itech.itech_backend.modules.payment.model.Payment;
import com.itech.itech_backend.modules.payment.model.SubscriptionPlan;
import com.itech.itech_backend.modules.payment.repository.PaymentRepository;
import com.itech.itech_backend.modules.payment.repository.SubscriptionPlanRepository;
import com.itech.marketplace.dto.PaymentSummaryDto;
import com.itech.marketplace.dto.VendorPaymentDto;
import com.itech.marketplace.model.VendorPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    
    @Value("${razorpay.key.id:}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret:}")
    private String razorpayKeySecret;

    @Value("${razorpay.currency:INR}")
    private String currency;

    private RazorpayClient razorpayClient;

    public Payment createPayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    public List<SubscriptionPlan> getAllSubscriptionPlans() {
        return subscriptionPlanRepository.findAll();
    }

    public List<Payment> getVendorPayments(Long vendorId) {
        return paymentRepository.findByVendorId(vendorId);
    }
    
    private RazorpayClient getRazorpayClient() throws RazorpayException {
        if (razorpayClient == null) {
            razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
        }
        return razorpayClient;
    }

    public Map<String, Object> createOrder(String orderNumber, BigDecimal amount, String customerEmail, String customerPhone) {
        try {
            RazorpayClient client = getRazorpayClient();
            
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue()); // Amount in paise
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", orderNumber);
            
            // Customer details
            JSONObject notes = new JSONObject();
            notes.put("email", customerEmail);
            notes.put("phone", customerPhone);
            orderRequest.put("notes", notes);
            
            Order order = client.orders.create(orderRequest);
            
            Map<String, Object> response = new HashMap<>();
            response.put("razorpayOrderId", order.get("id"));
            response.put("amount", order.get("amount"));
            response.put("currency", order.get("currency"));
            response.put("key", razorpayKeyId);
            
            log.info("Razorpay order created successfully: {}", order.get("id").toString());
            return response;
            
        } catch (RazorpayException e) {
            log.error("Error creating Razorpay order", e);
            throw new RuntimeException("Failed to create payment order: " + e.getMessage());
        }
    }

    public boolean verifyPayment(String razorpayOrderId, String razorpayPaymentId, String signature) {
        try {
            // Create signature verification string
            String generatedSignature = hmacSha256(razorpayOrderId + "|" + razorpayPaymentId, razorpayKeySecret);
            
            boolean isValid = generatedSignature.equals(signature);
            log.info("Payment verification result: {} for order: {}", isValid, razorpayOrderId);
            
            return isValid;
        } catch (Exception e) {
            log.error("Error verifying payment", e);
            return false;
        }
    }

    private String hmacSha256(String data, String key) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(key.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes());
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating HMAC SHA256", e);
        }
    }

    // Additional methods required by FinanceController
    public PaymentSummaryDto getPaymentSummary(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            // For now, return a basic summary
            return PaymentSummaryDto.builder()
                .totalAmount(BigDecimal.ZERO)
                .totalPayments(BigDecimal.ZERO)
                .successfulPayments(BigDecimal.ZERO)
                .failedPayments(BigDecimal.ZERO)
                .pendingPayments(BigDecimal.ZERO)
                .build();
        } catch (Exception e) {
            log.error("Error generating payment summary", e);
            throw new RuntimeException("Failed to generate payment summary");
        }
    }

    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }
    
    public Payment findByRazorpayOrderId(String razorpayOrderId) {
        return paymentRepository.findByTransactionId(razorpayOrderId)
            .orElse(null);
    }
    
    public List<Payment> getPaymentHistory(Long userId) {
        return paymentRepository.findByBuyerId(userId);
    }
    
    // Vendor payment methods for FinanceController
    public Page<VendorPayment> getVendorPayments(
            Long vendorId, String status, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        // For now, return empty page - this needs proper implementation
        log.info("Getting vendor payments for vendor: {}, status: {}", vendorId, status);
        return Page.empty(pageable);
    }
    
    public VendorPayment processVendorPayment(VendorPaymentDto request) {
        // For now, return null - this needs proper implementation
        log.info("Processing vendor payment: {}", request);
        return null;
    }
}

