package com.itech.itech_backend.modules.core.controller;

import com.itech.itech_backend.modules.shared.dto.KycDocumentUploadDto;
import com.itech.itech_backend.enums.KycDocumentType;
import com.itech.itech_backend.modules.core.model.KycDocument;
import com.itech.itech_backend.modules.core.service.KycDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/kyc")
public class KycDocumentController {

    @Autowired
    private KycDocumentService kycDocumentService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadKycDocument(
            @RequestParam("vendorId") Long vendorId,
            @RequestParam("documentType") String documentType,
            @RequestParam("file") MultipartFile file) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "Please select a file to upload");
                return ResponseEntity.badRequest().body(response);
            }

            // Validate file type
            String contentType = file.getContentType();
            if (!isValidFileType(contentType)) {
                response.put("success", false);
                response.put("message", "Invalid file type. Only JPG, PNG, and PDF files are allowed");
                return ResponseEntity.badRequest().body(response);
            }

            // Validate file size (5MB limit)
            if (file.getSize() > 5 * 1024 * 1024) {
                response.put("success", false);
                response.put("message", "File size must be less than 5MB");
                return ResponseEntity.badRequest().body(response);
            }

            KycDocumentUploadDto dto = new KycDocumentUploadDto();
            dto.setVendorId(vendorId);
            dto.setDocumentType(KycDocumentType.valueOf(documentType.toUpperCase()));
            dto.setFile(file);

            KycDocument savedDocument = kycDocumentService.uploadDocument(dto);

            response.put("success", true);
            response.put("message", "Document uploaded successfully");
            response.put("documentId", savedDocument.getId());
            response.put("fileName", savedDocument.getOriginalFileName());
            
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Failed to upload file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<KycDocument>> getPendingKycDocuments() {
        List<KycDocument> pendingDocuments = kycDocumentService.getPendingKycDocuments();
        return ResponseEntity.ok(pendingDocuments);
    }

    @PostMapping("/approve/{documentId}")
    public ResponseEntity<Map<String, Object>> approveKycDocument(
            @PathVariable Long documentId,
            @RequestParam Long reviewerId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            kycDocumentService.approveKycDocument(documentId, reviewerId);
            response.put("success", true);
            response.put("message", "Document approved successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to approve document: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/reject/{documentId}")
    public ResponseEntity<Map<String, Object>> rejectKycDocument(
            @PathVariable Long documentId,
            @RequestParam Long reviewerId,
            @RequestParam String reason) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            kycDocumentService.rejectKycDocument(documentId, reviewerId, reason);
            response.put("success", true);
            response.put("message", "Document rejected successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to reject document: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private boolean isValidFileType(String contentType) {
        return contentType != null && (
            contentType.equals("image/jpeg") ||
            contentType.equals("image/png") ||
            contentType.equals("application/pdf")
        );
    }
}

