package com.itech.itech_backend.modules.directory.repository;

import com.itech.itech_backend.modules.directory.model.ContactInquiry;
import com.itech.itech_backend.modules.directory.model.ServiceProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContactInquiryRepository extends JpaRepository<ContactInquiry, Long> {

    // Find inquiries by service provider
    Page<ContactInquiry> findByServiceProviderOrderByCreatedAtDesc(ServiceProvider serviceProvider, Pageable pageable);

    // Find inquiries by status
    Page<ContactInquiry> findByStatusOrderByCreatedAtDesc(ContactInquiry.InquiryStatus status, Pageable pageable);

    // Find inquiries by service provider and status
    Page<ContactInquiry> findByServiceProviderAndStatusOrderByCreatedAtDesc(
            ServiceProvider serviceProvider, ContactInquiry.InquiryStatus status, Pageable pageable);

    // Find recent inquiries
    @Query("SELECT ci FROM ContactInquiry ci WHERE ci.createdAt >= :since ORDER BY ci.createdAt DESC")
    List<ContactInquiry> findRecentInquiries(@Param("since") LocalDateTime since);

    // Find urgent inquiries
    List<ContactInquiry> findByUrgentAndStatusOrderByCreatedAtDesc(Boolean urgent, ContactInquiry.InquiryStatus status);

    // Find inquiries by priority
    Page<ContactInquiry> findByPriorityOrderByCreatedAtDesc(ContactInquiry.InquiryPriority priority, Pageable pageable);

    // Count inquiries by service provider
    long countByServiceProvider(ServiceProvider serviceProvider);

    // Count inquiries by status for a service provider
    long countByServiceProviderAndStatus(ServiceProvider serviceProvider, ContactInquiry.InquiryStatus status);

    // Find inquiries needing follow-up
    @Query("SELECT ci FROM ContactInquiry ci WHERE ci.followUpDate <= :date AND ci.status IN :statuses ORDER BY ci.followUpDate ASC")
    List<ContactInquiry> findInquiriesNeedingFollowUp(@Param("date") LocalDateTime date, 
                                                     @Param("statuses") List<ContactInquiry.InquiryStatus> statuses);

    // Search inquiries by name or mobile
    @Query("SELECT ci FROM ContactInquiry ci WHERE " +
           "LOWER(ci.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "ci.mobile LIKE CONCAT('%', :query, '%')")
    Page<ContactInquiry> searchInquiries(@Param("query") String query, Pageable pageable);

    // Find inquiries by date range
    @Query("SELECT ci FROM ContactInquiry ci WHERE ci.createdAt BETWEEN :startDate AND :endDate ORDER BY ci.createdAt DESC")
    Page<ContactInquiry> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                       @Param("endDate") LocalDateTime endDate, 
                                       Pageable pageable);

    // Get inquiry statistics
    @Query("SELECT ci.status, COUNT(ci) FROM ContactInquiry ci WHERE ci.serviceProvider = :serviceProvider GROUP BY ci.status")
    List<Object[]> getInquiryStatsByServiceProvider(@Param("serviceProvider") ServiceProvider serviceProvider);

    // Find inquiries by mobile number (for duplicate check)
    List<ContactInquiry> findByMobileAndServiceProviderOrderByCreatedAtDesc(String mobile, ServiceProvider serviceProvider);

    // Find converted inquiries (for success rate calculation)
    @Query("SELECT ci FROM ContactInquiry ci WHERE ci.status = 'CONVERTED' AND ci.serviceProvider = :serviceProvider")
    List<ContactInquiry> findConvertedInquiriesByServiceProvider(@Param("serviceProvider") ServiceProvider serviceProvider);

    // Monthly inquiry count for analytics
    @Query("SELECT YEAR(ci.createdAt), MONTH(ci.createdAt), COUNT(ci) FROM ContactInquiry ci " +
           "WHERE ci.serviceProvider = :serviceProvider AND ci.createdAt >= :since " +
           "GROUP BY YEAR(ci.createdAt), MONTH(ci.createdAt) ORDER BY YEAR(ci.createdAt), MONTH(ci.createdAt)")
    List<Object[]> getMonthlyInquiryCount(@Param("serviceProvider") ServiceProvider serviceProvider, 
                                        @Param("since") LocalDateTime since);
}
