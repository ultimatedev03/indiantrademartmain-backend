package com.itech.itech_backend.modules.directory.repository;

import com.itech.itech_backend.modules.directory.model.ServiceProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {

    // Search by category
    Page<ServiceProvider> findByCategoryContainingIgnoreCaseAndStatus(
            String category, ServiceProvider.ProviderStatus status, Pageable pageable);

    // Search by location
    Page<ServiceProvider> findByCityContainingIgnoreCaseAndStatus(
            String city, ServiceProvider.ProviderStatus status, Pageable pageable);

    Page<ServiceProvider> findByStateContainingIgnoreCaseAndStatus(
            String state, ServiceProvider.ProviderStatus status, Pageable pageable);

    // Search by business name
    Page<ServiceProvider> findByBusinessNameContainingIgnoreCaseAndStatus(
            String businessName, ServiceProvider.ProviderStatus status, Pageable pageable);

    // Combined search
    @Query("SELECT sp FROM ServiceProvider sp WHERE " +
           "(LOWER(sp.businessName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(sp.category) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(sp.services) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(sp.description) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "sp.status = :status")
    Page<ServiceProvider> searchByQuery(@Param("query") String query, 
                                      @Param("status") ServiceProvider.ProviderStatus status,
                                      Pageable pageable);

    // Location-based search
    @Query("SELECT sp FROM ServiceProvider sp WHERE " +
           "(LOWER(sp.city) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
           "LOWER(sp.state) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
           "LOWER(sp.area) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "sp.status = :status")
    Page<ServiceProvider> searchByLocation(@Param("location") String location,
                                         @Param("status") ServiceProvider.ProviderStatus status,
                                         Pageable pageable);

    // Combined query and location search
    @Query("SELECT sp FROM ServiceProvider sp WHERE " +
           "(LOWER(sp.businessName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(sp.category) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(sp.services) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(sp.description) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(LOWER(sp.city) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
           "LOWER(sp.state) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
           "LOWER(sp.area) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "sp.status = :status")
    Page<ServiceProvider> searchByQueryAndLocation(@Param("query") String query,
                                                  @Param("location") String location,
                                                  @Param("status") ServiceProvider.ProviderStatus status,
                                                  Pageable pageable);

    // Advanced search with category filter
    @Query("SELECT sp FROM ServiceProvider sp WHERE " +
           "(LOWER(sp.businessName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(sp.services) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(sp.description) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(LOWER(sp.city) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
           "LOWER(sp.state) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
           "LOWER(sp.area) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "LOWER(sp.category) LIKE LOWER(CONCAT('%', :category, '%')) AND " +
           "sp.status = :status")
    Page<ServiceProvider> searchByQueryLocationAndCategory(@Param("query") String query,
                                                          @Param("location") String location,
                                                          @Param("category") String category,
                                                          @Param("status") ServiceProvider.ProviderStatus status,
                                                          Pageable pageable);

    // Find by status
    Page<ServiceProvider> findByStatus(ServiceProvider.ProviderStatus status, Pageable pageable);

    // Find verified providers
    Page<ServiceProvider> findByVerifiedAndStatus(Boolean verified, ServiceProvider.ProviderStatus status, Pageable pageable);

    // Find by rating range
    @Query("SELECT sp FROM ServiceProvider sp WHERE sp.rating >= :minRating AND sp.status = :status")
    Page<ServiceProvider> findByMinRating(@Param("minRating") Double minRating,
                                        @Param("status") ServiceProvider.ProviderStatus status,
                                        Pageable pageable);

    // Find top rated providers
    @Query("SELECT sp FROM ServiceProvider sp WHERE sp.status = :status ORDER BY sp.rating DESC, sp.reviewCount DESC")
    Page<ServiceProvider> findTopRatedProviders(@Param("status") ServiceProvider.ProviderStatus status,
                                              Pageable pageable);

    // Find by city and category
    Page<ServiceProvider> findByCityIgnoreCaseAndCategoryContainingIgnoreCaseAndStatus(
            String city, String category, ServiceProvider.ProviderStatus status, Pageable pageable);

    // Find popular providers (most contacted)
    @Query("SELECT sp FROM ServiceProvider sp WHERE sp.status = :status ORDER BY sp.contactRequests DESC, sp.profileViews DESC")
    Page<ServiceProvider> findPopularProviders(@Param("status") ServiceProvider.ProviderStatus status,
                                             Pageable pageable);

    // Count providers by city
    @Query("SELECT sp.city, COUNT(sp) FROM ServiceProvider sp WHERE sp.status = :status GROUP BY sp.city ORDER BY COUNT(sp) DESC")
    List<Object[]> countProvidersByCity(@Param("status") ServiceProvider.ProviderStatus status);

    // Count providers by category
    @Query("SELECT sp.category, COUNT(sp) FROM ServiceProvider sp WHERE sp.status = :status GROUP BY sp.category ORDER BY COUNT(sp) DESC")
    List<Object[]> countProvidersByCategory(@Param("status") ServiceProvider.ProviderStatus status);

    // Find by slug (for SEO URLs)
    Optional<ServiceProvider> findBySlugAndStatus(String slug, ServiceProvider.ProviderStatus status);

    // Find providers within radius (if coordinates are available)
    @Query("SELECT sp FROM ServiceProvider sp WHERE " +
           "sp.latitude IS NOT NULL AND sp.longitude IS NOT NULL AND " +
           "(6371 * acos(cos(radians(:latitude)) * cos(radians(sp.latitude)) * " +
           "cos(radians(sp.longitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(sp.latitude)))) <= :radius AND " +
           "sp.status = :status")
    List<ServiceProvider> findProvidersWithinRadius(@Param("latitude") Double latitude,
                                                   @Param("longitude") Double longitude,
                                                   @Param("radius") Double radius,
                                                   @Param("status") ServiceProvider.ProviderStatus status);

    // Find recently active providers
    @Query("SELECT sp FROM ServiceProvider sp WHERE sp.lastActive >= :since AND sp.status = :status ORDER BY sp.lastActive DESC")
    Page<ServiceProvider> findRecentlyActiveProviders(@Param("since") java.time.LocalDateTime since,
                                                     @Param("status") ServiceProvider.ProviderStatus status,
                                                     Pageable pageable);

    // Find providers by experience range
    @Query("SELECT sp FROM ServiceProvider sp WHERE sp.yearsOfExperience >= :minYears AND sp.status = :status")
    Page<ServiceProvider> findByMinExperience(@Param("minYears") Integer minYears,
                                            @Param("status") ServiceProvider.ProviderStatus status,
                                            Pageable pageable);

    // Count total active providers
    long countByStatus(ServiceProvider.ProviderStatus status);

    // Find providers needing verification
    List<ServiceProvider> findByKycVerifiedAndStatus(Boolean kycVerified, ServiceProvider.ProviderStatus status);

    // Search providers with filters for admin
    @Query("SELECT sp FROM ServiceProvider sp WHERE " +
           "(:query IS NULL OR " +
           "LOWER(sp.businessName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(sp.category) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(sp.ownerName) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:city IS NULL OR LOWER(sp.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
           "(:state IS NULL OR LOWER(sp.state) LIKE LOWER(CONCAT('%', :state, '%'))) AND " +
           "(:status IS NULL OR sp.status = :status) AND " +
           "(:verified IS NULL OR sp.verified = :verified)")
    Page<ServiceProvider> adminSearchProviders(@Param("query") String query,
                                             @Param("city") String city,
                                             @Param("state") String state,
                                             @Param("status") ServiceProvider.ProviderStatus status,
                                             @Param("verified") Boolean verified,
                                             Pageable pageable);

    // Update profile view count
    @Query("UPDATE ServiceProvider sp SET sp.profileViews = sp.profileViews + 1 WHERE sp.id = :id")
    void incrementProfileViews(@Param("id") Long id);

    // Update contact request count
    @Query("UPDATE ServiceProvider sp SET sp.contactRequests = sp.contactRequests + 1 WHERE sp.id = :id")
    void incrementContactRequests(@Param("id") Long id);
}
