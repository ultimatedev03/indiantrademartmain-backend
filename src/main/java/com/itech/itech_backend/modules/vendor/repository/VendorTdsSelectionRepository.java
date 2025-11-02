package com.itech.itech_backend.modules.vendor.repository;

import com.itech.itech_backend.modules.vendor.model.Vendors;
import com.itech.itech_backend.modules.vendor.model.VendorTdsSelection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorTdsSelectionRepository extends JpaRepository<VendorTdsSelection, Long> {
    
    List<VendorTdsSelection> findByVendorAndPanNumber(Vendors vendor, String panNumber);
    
    List<VendorTdsSelection> findByVendorAndIsSelected(Vendors vendor, boolean isSelected);
    
    List<VendorTdsSelection> findByVendor(Vendors vendor);
    
    @Query("SELECT v FROM VendorTdsSelection v WHERE v.vendor = :vendor AND v.panNumber = :panNumber AND v.isSelected = true")
    List<VendorTdsSelection> findSelectedTdsRatesByVendorAndPanNumber(@Param("vendor") Vendors vendor, @Param("panNumber") String panNumber);
    
    void deleteByVendorAndPanNumber(Vendors vendor, String panNumber);
}

