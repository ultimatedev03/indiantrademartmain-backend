package com.itech.itech_backend.modules.vendor.service;

import lombok.Builder;
import lombok.Data;

import java.util.function.Consumer;

public interface ThirdPartyVerificationService {

    void verifyGST(String gstNumber, String documentUrl, Consumer<VerificationResult> callback);

    void verifyPAN(String panNumber, String documentUrl, Consumer<VerificationResult> callback);

    void verifyBankAccount(String accountNumber, String documentUrl, Consumer<VerificationResult> callback);

    @Data
    @Builder
    class VerificationResult {
        private boolean valid;
        private String reason;
        private String verificationId;
        private String metadata;
    }
}
