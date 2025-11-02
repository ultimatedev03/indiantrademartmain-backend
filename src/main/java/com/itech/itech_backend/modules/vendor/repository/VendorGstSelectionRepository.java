package com.itech.itech_backend.modules.vendor.repository;

import com.itech.itech_backend.modules.vendor.model.Vendors;
import com.itech.itech_backend.modules.vendor.model.VendorGstSelection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorGstSelectionRepository extends JpaRepository<VendorGstSelection, Long> {
    
    List<VendorGstSelection> findByVendorAndGstNumber(Vendors vendor, String gstNumber);
    
    List<VendorGstSelection> findByVendorAndIsSelected(Vendors vendor, boolean isSelected);
    
    List<VendorGstSelection> findByVendor(Vendors vendor);
    
    @Query("SELECT v FROM VendorGstSelection v WHERE v.vendor = :vendor AND v.gstNumber = :gstNumber AND v.isSelected = true")
    List<VendorGstSelection> findSelectedGstRatesByVendorAndGstNumber(@Param("vendor") Vendors vendor, @Param("gstNumber") String gstNumber);
    
    void deleteByVendorAndGstNumber(Vendors vendor, String gstNumber);
}

