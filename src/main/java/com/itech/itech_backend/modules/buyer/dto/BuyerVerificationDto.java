package com.itech.itech_backend.modules.buyer.dto;

import com.itech.itech_backend.enums.VerificationStatus;
import com.itech.itech_backend.enums.KycStatus;
import com.itech.itech_backend.modules.buyer.model.Buyer;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuyerVerificationDto {
    
    private Long buyerId;
    private String buyerName;
    private String email;
    
    // Verification Status Information
    private Boolean isEmailVerified;
    private Boolean isPhoneVerified;
    private Boolean isKycVerified;
    private Boolean isDocumentVerified;
    private Boolean isAddressVerified;
    private Boolean isBankDetailsVerified;
    
    // Overall verification status
    private VerificationStatus overallVerificationStatus;
    
    // KYC Information
    private KycStatus kycStatus;
    private String kycProvider;
    private String kycReferenceNumber;
    private LocalDateTime kycVerificationDate;
    private LocalDateTime kycExpiryDate;
    private String kycRejectionReason;
    private Integer kycAttempts;
    
    // Document Verification
    private List<DocumentVerificationInfo> documents;
    
    // Address Verification
    private String addressVerificationMethod; // "MANUAL", "AUTOMATED", "DOCUMENT_BASED"
    private String addressVerificationStatus;
    private LocalDateTime addressVerificationDate;
    private String addressVerificationReference;
    
    // Financial Verification (for business buyers)
    private Boolean bankDetailsVerified;
    private String bankVerificationMethod;
    private LocalDateTime bankVerificationDate;
    
    // Business Verification (for business buyers)
    private Boolean businessRegistrationVerified;
    private String businessRegistrationNumber;
    private Boolean gstVerified;
    private String gstNumber;
    private Boolean tradeLicenseVerified;
    private String tradeLicenseNumber;
    
    // Verification Timestamps
    private LocalDateTime emailVerificationDate;
    private LocalDateTime phoneVerificationDate;
    private LocalDateTime firstVerificationDate;
    private LocalDateTime lastVerificationUpdate;
    
    // Risk Assessment
    private String riskLevel; // "LOW", "MEDIUM", "HIGH"
    private Integer riskScore;
    private List<String> riskFactors;
    
    // Verification Notes and Comments
    private String verificationNotes;
    private String adminComments;
    
    // Additional Verification Data
    private Map<String, Object> additionalVerificationData;
    
    // Document Verification Sub-DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentVerificationInfo {
        private String documentType; // "AADHAR", "PAN", "PASSPORT", "DRIVING_LICENSE", etc.
        private String documentNumber;
        private Boolean isVerified;
        private String verificationStatus; // "PENDING", "VERIFIED", "REJECTED", "EXPIRED"
        private LocalDateTime verificationDate;
        private String rejectionReason;
        private String documentUrl; // Secure URL for document access
        private LocalDateTime expiryDate;
        private String issuer;
        private String verificationMethod; // "MANUAL", "OCR", "API"
        private String verificationReference;
    }
    
    // Helper methods for verification status checks
    public Boolean isFullyVerified() {
        return Boolean.TRUE.equals(isEmailVerified) && 
               Boolean.TRUE.equals(isPhoneVerified) && 
               Boolean.TRUE.equals(isKycVerified) && 
               Boolean.TRUE.equals(isDocumentVerified);
    }
    
    public Boolean isBasicVerificationComplete() {
        return Boolean.TRUE.equals(isEmailVerified) && 
               Boolean.TRUE.equals(isPhoneVerified);
    }
    
    public Boolean isBusinessVerificationRequired() {
        return Boolean.TRUE.equals(businessRegistrationVerified) || 
               Boolean.TRUE.equals(gstVerified) || 
               Boolean.TRUE.equals(tradeLicenseVerified);
    }
    
    public Boolean isHighRisk() {
        return "HIGH".equals(riskLevel) || (riskScore != null && riskScore > 70);
    }
}

