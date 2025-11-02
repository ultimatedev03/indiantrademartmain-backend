package com.itech.itech_backend.modules.admin.repository;

import com.itech.itech_backend.modules.admin.model.Lead;
import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.vendor.model.Vendors;
import com.itech.itech_backend.enums.LeadStatus;
import com.itech.itech_backend.enums.LeadPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {
    
    // Find leads by vendor
    List<Lead> findByVendor(Vendors vendor);
    
    // Find leads by vendor and status
    List<Lead> findByVendorAndStatus(Vendors vendor, LeadStatus status);
    
    // Find leads by vendor and priority
    List<Lead> findByVendorAndPriority(Vendors vendor, LeadPriority priority);
    
    // Find leads by vendor with follow-up date before given date
    List<Lead> findByVendorAndNextFollowUpDateBefore(Vendors vendor, LocalDateTime date);
    
    // Count leads by vendor and status
    long countByVendorAndStatus(Vendors vendor, LeadStatus status);
    
    // Find recent leads for vendor (last 30 days)
    @Query("SELECT l FROM Lead l WHERE l.vendor = :vendor AND l.inquiryDate >= :thirtyDaysAgo ORDER BY l.inquiryDate DESC")
    List<Lead> findRecentLeadsByVendor(@Param("vendor") Vendors vendor, @Param("thirtyDaysAgo") LocalDateTime thirtyDaysAgo);
    
    // Get lead statistics for vendor
    @Query("SELECT l.status, COUNT(l) FROM Lead l WHERE l.vendor = :vendor GROUP BY l.status")
    List<Object[]> getLeadStatsByVendor(@Param("vendor") Vendors vendor);
    
    // Find leads by customer email or phone
    List<Lead> findByCustomerEmailOrCustomerPhone(String email, String phone);
    
    // Search leads by customer name containing
    List<Lead> findByVendorAndCustomerNameContainingIgnoreCase(Vendors vendor, String customerName);
}

