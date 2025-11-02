package com.itech.itech_backend.modules.payment.controller;

import com.itech.itech_backend.modules.payment.model.Payment;
import com.itech.itech_backend.modules.payment.model.SubscriptionPlan;
import com.itech.itech_backend.modules.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {
        Payment createdPayment = paymentService.createPayment(payment);
        return ResponseEntity.ok(createdPayment);
    }

    @GetMapping("/subscription-plans")
    public ResponseEntity<List<SubscriptionPlan>> getSubscriptionPlans() {
        List<SubscriptionPlan> plans = paymentService.getAllSubscriptionPlans();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<Payment>> getVendorPayments(@PathVariable Long vendorId) {
        List<Payment> payments = paymentService.getVendorPayments(vendorId);
        return ResponseEntity.ok(payments);
    }
}

