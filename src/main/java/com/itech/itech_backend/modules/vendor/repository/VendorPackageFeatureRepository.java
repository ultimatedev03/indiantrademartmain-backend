package com.itech.itech_backend.modules.vendor.repository;

import com.itech.itech_backend.modules.vendor.model.VendorPackage;
import com.itech.itech_backend.modules.vendor.model.VendorPackageFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorPackageFeatureRepository extends JpaRepository<VendorPackageFeature, Long> {
    
    // Find features by package
    List<VendorPackageFeature> findByVendorPackageOrderByDisplayOrderAsc(VendorPackage vendorPackage);
    
    // Find included features by package
    List<VendorPackageFeature> findByVendorPackageAndIsIncludedTrueOrderByDisplayOrderAsc(VendorPackage vendorPackage);
    
    // Find features by type
    List<VendorPackageFeature> findByVendorPackageAndFeatureTypeOrderByDisplayOrderAsc(
            VendorPackage vendorPackage, VendorPackageFeature.FeatureType featureType);
    
    // Find highlighted features
    List<VendorPackageFeature> findByVendorPackageAndIsHighlightedTrueOrderByDisplayOrderAsc(VendorPackage vendorPackage);
    
    // Delete features by package
    void deleteByVendorPackage(VendorPackage vendorPackage);
}
