package com.itech.itech_backend.modules.core.service;

import com.itech.itech_backend.modules.shared.dto.KycDocumentUploadDto;
import com.itech.itech_backend.enums.KycDocumentType;
import com.itech.itech_backend.enums.KycStatus;
import com.itech.itech_backend.modules.core.model.KycDocument;
import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.vendor.model.Vendors;
import com.itech.itech_backend.modules.core.repository.KycDocumentRepository;
import com.itech.itech_backend.modules.core.repository.UserRepository;
import com.itech.itech_backend.modules.shared.service.EmailService;
import com.itech.itech_backend.modules.vendor.repository.VendorsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KycDocumentService {

    private static final String KYC_UPLOAD_DIR = "uploads/kyc/";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String[] ALLOWED_EXTENSIONS = {".pdf", ".jpg", ".jpeg", ".png", ".doc", ".docx"};

    private final KycDocumentRepository kycDocumentRepository;
    private final VendorsRepository vendorsRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

@Transactional
    public KycDocument uploadDocument(KycDocumentUploadDto dto) throws IOException {

        Optional<Vendors> vendorOpt = vendorsRepository.findById(dto.getVendorId());
        if (vendorOpt.isEmpty()) {
            throw new IllegalArgumentException("Vendor not found");
        }
        Vendors vendor = vendorOpt.get();

        // Ensure directory exists
        Path uploadDir = Paths.get(KYC_UPLOAD_DIR);
        if (Files.notExists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        MultipartFile file = dto.getFile();
        String originalFilename = file.getOriginalFilename();
        String extension = Optional.ofNullable(originalFilename)
            .filter(f -> f.contains("."))
            .map(f -> f.substring(originalFilename.lastIndexOf('.')))
            .orElse("");

        String uniqueFileName = UUID.randomUUID().toString() + extension;
        Path filePath = uploadDir.resolve(uniqueFileName);
        file.transferTo(filePath.toFile());

        KycDocument kycDocument = KycDocument.builder()
            .vendor(vendor)
            .documentType(dto.getDocumentType())
            .fileName(uniqueFileName)
            .filePath(filePath.toString())
            .originalFileName(originalFilename)
            .fileSize(file.getSize())
            .mimeType(file.getContentType())
            .build();

        return kycDocumentRepository.save(kycDocument);
    }

    public List<KycDocument> getPendingKycDocuments() {
        return kycDocumentRepository.findPendingDocumentsForReview();
    }

    public void approveKycDocument(Long documentId, Long reviewerId) {
        Optional<KycDocument> kycDocumentOpt = kycDocumentRepository.findById(documentId);
        if (kycDocumentOpt.isPresent()) {
            KycDocument kycDocument = kycDocumentOpt.get();
            kycDocument.setStatus(KycStatus.APPROVED);
            User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new RuntimeException("Reviewer not found"));
            kycDocument.setReviewedBy(reviewer);
            kycDocument.setReviewedAt(java.time.LocalDateTime.now());
            kycDocumentRepository.save(kycDocument);
        }
    }

    public void rejectKycDocument(Long documentId, Long reviewerId, String reason) {
        Optional<KycDocument> kycDocumentOpt = kycDocumentRepository.findById(documentId);
        if (kycDocumentOpt.isPresent()) {
            KycDocument kycDocument = kycDocumentOpt.get();
            kycDocument.setStatus(KycStatus.REJECTED);
            kycDocument.setRejectionReason(reason);
            // Assuming you have a user service to fetch User based on ID
            // User reviewer = userService.findById(reviewerId);
            // kycDocument.setReviewedBy(reviewer);
            kycDocument.setReviewedAt(java.time.LocalDateTime.now());
            kycDocumentRepository.save(kycDocument);
        }
    }
}


