package com.itech.itech_backend.modules.shared.service;

import org.json.JSONObject;

public interface WebhookService {
    void processRazorpayWebhook(JSONObject payload);
}

