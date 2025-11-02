package com.itech.itech_backend.modules.core.repository;

import com.itech.itech_backend.enums.KycDocumentType;
import com.itech.itech_backend.enums.KycStatus;
import com.itech.itech_backend.modules.core.model.KycDocument;
import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.vendor.model.Vendors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KycDocumentRepository extends JpaRepository<KycDocument, Long> {
    
    List<KycDocument> findByVendor(Vendors vendor);
    
    List<KycDocument> findByVendorId(Long vendorId);
    
    List<KycDocument> findByStatus(KycStatus status);
    
    Optional<KycDocument> findByVendorAndDocumentType(Vendors vendor, KycDocumentType documentType);
    
    @Query("SELECT k FROM KycDocument k WHERE k.vendor.id = :vendorId AND k.documentType = :documentType")
    Optional<KycDocument> findByVendorIdAndDocumentType(@Param("vendorId") Long vendorId, @Param("documentType") KycDocumentType documentType);
    
    @Query("SELECT COUNT(k) FROM KycDocument k WHERE k.vendor.id = :vendorId AND k.status = :status")
    long countByVendorIdAndStatus(@Param("vendorId") Long vendorId, @Param("status") KycStatus status);
    
    @Query("SELECT k FROM KycDocument k WHERE k.status = 'PENDING' ORDER BY k.createdAt ASC")
    List<KycDocument> findPendingDocumentsForReview();
    
    @Query("SELECT DISTINCT k.vendor FROM KycDocument k WHERE k.status = 'PENDING'")
    List<Vendors> findVendorsWithPendingKyc();
    
    boolean existsByVendorAndDocumentType(Vendors vendor, KycDocumentType documentType);
}

