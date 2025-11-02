package com.itech.itech_backend.modules.payment.service;

import org.json.JSONObject;

public interface WebhookService {
    void processRazorpayWebhook(JSONObject payload);
}


