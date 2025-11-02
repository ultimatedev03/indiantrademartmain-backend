package com.itech.itech_backend.modules.shared.dto;

import com.itech.itech_backend.enums.KycDocumentType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class KycDocumentUploadDto {
    private Long vendorId;
    private KycDocumentType documentType;
    private MultipartFile file;
    private String description;
}

