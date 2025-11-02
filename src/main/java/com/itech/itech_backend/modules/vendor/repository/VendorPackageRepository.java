package com.itech.itech_backend.modules.vendor.repository;

import com.itech.itech_backend.modules.payment.model.Subscription;
import com.itech.itech_backend.modules.vendor.model.VendorPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorPackageRepository extends JpaRepository<VendorPackage, Long> {
    
    // Find active packages only
    List<VendorPackage> findByIsActiveTrueOrderBySortOrderAsc();
    
    // Find by plan type
    List<VendorPackage> findByPlanTypeAndIsActiveTrueOrderBySortOrderAsc(Subscription.PlanType planType);
    
    // Find by name
    Optional<VendorPackage> findByNameAndIsActiveTrue(String name);
    
    // Find popular packages
    List<VendorPackage> findByIsPopularTrueAndIsActiveTrueOrderBySortOrderAsc();
    
    // Find packages within price range
    @Query("SELECT vp FROM VendorPackage vp WHERE vp.isActive = true AND vp.price BETWEEN :minPrice AND :maxPrice ORDER BY vp.price ASC")
    List<VendorPackage> findByPriceRangeAndActive(@Param("minPrice") java.math.BigDecimal minPrice, 
                                                   @Param("maxPrice") java.math.BigDecimal maxPrice);
    
    // Find packages by duration type
    List<VendorPackage> findByDurationTypeAndIsActiveTrueOrderBySortOrderAsc(VendorPackage.DurationType durationType);
    
    // Count active packages by plan type
    @Query("SELECT vp.planType, COUNT(vp) FROM VendorPackage vp WHERE vp.isActive = true GROUP BY vp.planType")
    List<Object[]> countByPlanType();
}
