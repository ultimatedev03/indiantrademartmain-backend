package com.itech.itech_backend.modules.buyer.repository;

import com.itech.itech_backend.modules.shared.model.BuyerLead;
import com.itech.itech_backend.modules.core.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BuyerLeadRepository extends JpaRepository<BuyerLead, Long> {

    // Find leads by email or phone
    Optional<BuyerLead> findByEmail(String email);
    Optional<BuyerLead> findByEmailOrPhone(String email, String phone);
    
    // Find leads by user
    List<BuyerLead> findByUser(User user);
    List<BuyerLead> findByUserOrderByCreatedAtDesc(User user);

    // Find leads by status
    List<BuyerLead> findByStatus(BuyerLead.LeadStatus status);
    Page<BuyerLead> findByStatus(BuyerLead.LeadStatus status, Pageable pageable);

    // Find leads by urgency
    List<BuyerLead> findByUrgency(BuyerLead.LeadUrgency urgency);
    Page<BuyerLead> findByUrgencyOrderByCreatedAtDesc(BuyerLead.LeadUrgency urgency, Pageable pageable);

    // Find leads by source
    List<BuyerLead> findBySource(BuyerLead.LeadSource source);

    // Find leads by assigned sales rep
    List<BuyerLead> findByAssignedSalesRep(String salesRep);
    Page<BuyerLead> findByAssignedSalesRepOrderByFollowUpDateAsc(String salesRep, Pageable pageable);

    // Find leads that need follow-up
    @Query("SELECT bl FROM BuyerLead bl WHERE bl.followUpDate <= :date AND bl.status NOT IN ('CONVERTED', 'CLOSED_LOST')")
    List<BuyerLead> findLeadsForFollowUp(@Param("date") LocalDateTime date);

    // Find high-priority leads
    @Query("SELECT bl FROM BuyerLead bl WHERE bl.leadScore >= :minScore AND bl.status = 'NEW' ORDER BY bl.leadScore DESC")
    List<BuyerLead> findHighPriorityLeads(@Param("minScore") Integer minScore);

    // Find recent leads
    @Query("SELECT bl FROM BuyerLead bl WHERE bl.createdAt >= :since ORDER BY bl.createdAt DESC")
    List<BuyerLead> findRecentLeads(@Param("since") LocalDateTime since);

    // Find leads by search query containing keyword
    @Query("SELECT bl FROM BuyerLead bl WHERE bl.searchQuery LIKE %:keyword%")
    List<BuyerLead> findBySearchQueryContaining(@Param("keyword") String keyword);

    // Find leads by category interest
    @Query("SELECT bl FROM BuyerLead bl WHERE bl.interestedCategories LIKE %:category%")
    List<BuyerLead> findByInterestedCategory(@Param("category") String category);

    // Find leads within price range
    @Query("SELECT bl FROM BuyerLead bl WHERE bl.priceRangeMin <= :maxPrice AND bl.priceRangeMax >= :minPrice")
    List<BuyerLead> findByPriceRangeOverlap(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

    // Analytics queries
    @Query("SELECT COUNT(bl) FROM BuyerLead bl WHERE bl.createdAt >= :startDate AND bl.createdAt <= :endDate")
    Long countLeadsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT bl.status, COUNT(bl) FROM BuyerLead bl GROUP BY bl.status")
    List<Object[]> countLeadsByStatus();

    @Query("SELECT bl.source, COUNT(bl) FROM BuyerLead bl GROUP BY bl.source")
    List<Object[]> countLeadsBySource();

    @Query("SELECT bl.urgency, COUNT(bl) FROM BuyerLead bl GROUP BY bl.urgency")
    List<Object[]> countLeadsByUrgency();

    @Query("SELECT AVG(bl.leadScore) FROM BuyerLead bl WHERE bl.createdAt >= :startDate")
    Double averageLeadScore(@Param("startDate") LocalDateTime startDate);

    // Conversion analytics
    @Query("SELECT COUNT(bl) FROM BuyerLead bl WHERE bl.converted = true AND bl.updatedAt >= :startDate")
    Long countConvertedLeads(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT AVG(bl.conversionValue) FROM BuyerLead bl WHERE bl.converted = true AND bl.conversionValue IS NOT NULL")
    Double averageConversionValue();

    // Find leads needing attention (no activity for X days)
    @Query("SELECT bl FROM BuyerLead bl WHERE bl.lastActivity <= :cutoffDate AND bl.status NOT IN ('CONVERTED', 'CLOSED_LOST') ORDER BY bl.leadScore DESC")
    List<BuyerLead> findStaledLeads(@Param("cutoffDate") LocalDateTime cutoffDate);

    // Find similar leads based on search patterns
    @Query("SELECT bl FROM BuyerLead bl WHERE bl.id != :excludeId AND (bl.searchQuery LIKE %:searchPattern% OR bl.interestedCategories LIKE %:categoryPattern%) ORDER BY bl.leadScore DESC")
    List<BuyerLead> findSimilarLeads(@Param("excludeId") Long excludeId, @Param("searchPattern") String searchPattern, @Param("categoryPattern") String categoryPattern);

    // Custom search with multiple filters
    @Query("SELECT bl FROM BuyerLead bl WHERE " +
           "(:status IS NULL OR bl.status = :status) AND " +
           "(:urgency IS NULL OR bl.urgency = :urgency) AND " +
           "(:source IS NULL OR bl.source = :source) AND " +
           "(:assignedRep IS NULL OR bl.assignedSalesRep = :assignedRep) AND " +
           "(:minScore IS NULL OR bl.leadScore >= :minScore) AND " +
           "(:startDate IS NULL OR bl.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR bl.createdAt <= :endDate) " +
           "ORDER BY bl.leadScore DESC, bl.createdAt DESC")
    Page<BuyerLead> findLeadsWithFilters(
            @Param("status") BuyerLead.LeadStatus status,
            @Param("urgency") BuyerLead.LeadUrgency urgency,
            @Param("source") BuyerLead.LeadSource source,
            @Param("assignedRep") String assignedRep,
            @Param("minScore") Integer minScore,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    // Dashboard metrics
    @Query("SELECT " +
           "COUNT(CASE WHEN bl.createdAt >= :today THEN 1 END) as todayLeads, " +
           "COUNT(CASE WHEN bl.createdAt >= :thisWeek THEN 1 END) as weekLeads, " +
           "COUNT(CASE WHEN bl.createdAt >= :thisMonth THEN 1 END) as monthLeads, " +
           "COUNT(CASE WHEN bl.converted = true THEN 1 END) as totalConverted " +
           "FROM BuyerLead bl")
    List<Object[]> getLeadMetrics(@Param("today") LocalDateTime today, 
                                  @Param("thisWeek") LocalDateTime thisWeek, 
                                  @Param("thisMonth") LocalDateTime thisMonth);

    // Add missing repository methods
    Long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
