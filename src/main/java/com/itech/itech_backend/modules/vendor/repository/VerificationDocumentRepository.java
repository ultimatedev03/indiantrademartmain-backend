package com.itech.itech_backend.modules.vendor.repository;

import com.itech.itech_backend.modules.vendor.model.VerificationDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VerificationDocumentRepository extends JpaRepository<VerificationDocument, Long> {
    
    List<VerificationDocument> findByVendorId(String vendorId);
    
    List<VerificationDocument> findByVendorIdAndDocumentType(String vendorId, String documentType);
    
    List<VerificationDocument> findByStatusAndExpiryDateBefore(VerificationDocument.Status status, LocalDateTime date);
    
    List<VerificationDocument> findByStatus(VerificationDocument.Status status);
}
