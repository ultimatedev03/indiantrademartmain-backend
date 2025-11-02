package com.itech.itech_backend.modules.vendor.repository;

import com.itech.itech_backend.modules.vendor.model.VendorRanking;
import com.itech.itech_backend.modules.vendor.model.Vendors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VendorRankingRepository extends JpaRepository<VendorRanking, Long> {
    Optional<VendorRanking> findByVendor(Vendors vendor);
    
    @Query("SELECT vr FROM VendorRanking vr WHERE vr.vendor.id = :vendorId")
    Optional<VendorRanking> findByVendorId(@Param("vendorId") Long vendorId);
}

