package com.itech.itech_backend.modules.payment.controller;

import com.itech.itech_backend.modules.shared.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
@Slf4j
public class RazorpayWebhookController {

    private final WebhookService webhookService;

    @PostMapping("/razorpay")
    public ResponseEntity<String> handleRazorpayWebhook(@RequestBody String payload) {
        try {
            JSONObject webhookData = new JSONObject(payload);
            webhookService.processRazorpayWebhook(webhookData);
            return ResponseEntity.ok("Webhook processed successfully");
        } catch (Exception e) {
            log.error("Error processing Razorpay webhook: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error processing webhook");
        }
    }
}

