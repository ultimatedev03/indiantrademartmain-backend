package com.itech.itech_backend.modules.company.dto;

import com.itech.itech_backend.modules.company.model.Company;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyVerificationDto {
    
    @NotNull(message = "Company ID is required")
    private Long companyId;
    
    @NotNull(message = "Verification status is required")
    private Company.VerificationStatus verificationStatus;
    
    private String rejectionReason;
    private String verificationNotes;
    private String verifiedBy;
}

