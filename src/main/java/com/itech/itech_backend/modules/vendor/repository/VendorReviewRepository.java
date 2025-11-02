package com.itech.itech_backend.modules.vendor.repository;

import com.itech.itech_backend.modules.vendor.model.VendorReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorReviewRepository extends JpaRepository<VendorReview, Long> {
    List<VendorReview> findByVendorId(Long vendorId);
}

