package com.itech.itech_backend.modules.vendor.dto;

import com.itech.itech_backend.modules.vendor.model.Vendor;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorVerificationDto {
    
    @NotNull(message = "Vendor ID is required")
    private Long vendorId;
    
    @NotNull(message = "Vendor status is required")
    private Vendor.VendorStatus vendorStatus;
    
    private Boolean kycApproved;
    
    private String verificationNotes;
    private String rejectionReason;
    private String verifiedBy;
    
    // KYC specific fields
    private Boolean approveGstDocument;
    private Boolean approvePanDocument;
    private Boolean approveAddressProof;
    private Boolean approveBusinessRegistration;
    private Boolean approveBankDetails;
    
    // Additional verification checks
    private Boolean verifyBusinessAddress;
    private Boolean verifyContactDetails;
    private Boolean verifyFinancialInformation;
    private Boolean verifyReferences;
}

