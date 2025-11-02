package com.itech.itech_backend.modules.dataentry.repository;

import com.itech.itech_backend.modules.dataentry.entity.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {

    // Basic finder methods
    Optional<State> findByNameAndCountry(String name, String country);
    Optional<State> findByStateCodeAndCountry(String stateCode, String country);
    Optional<State> findBySlug(String slug);
    List<State> findByCountry(String country);
    List<State> findByCountryOrderByDisplayOrderAscNameAsc(String country);
    List<State> findByIsActive(Boolean isActive);
    Page<State> findByIsActive(Boolean isActive, Pageable pageable);

    // Search methods
    @Query("SELECT s FROM State s WHERE " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.stateCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.country) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.capital) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.searchKeywords) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<State> searchByTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT s FROM State s WHERE " +
           "(:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:country IS NULL OR LOWER(s.country) = LOWER(:country)) AND " +
           "(:stateCode IS NULL OR LOWER(s.stateCode) = LOWER(:stateCode)) AND " +
           "(:isActive IS NULL OR s.isActive = :isActive)")
    Page<State> findWithFilters(@Param("name") String name,
                               @Param("country") String country,
                               @Param("stateCode") String stateCode,
                               @Param("isActive") Boolean isActive,
                               Pageable pageable);

    // Distinct values for filtering
    @Query("SELECT DISTINCT s.country FROM State s WHERE s.country IS NOT NULL ORDER BY s.country")
    List<String> findDistinctCountries();

    @Query("SELECT DISTINCT s.countryCode FROM State s WHERE s.countryCode IS NOT NULL ORDER BY s.countryCode")
    List<String> findDistinctCountryCodes();

    // Geographic queries
    @Query("SELECT s FROM State s WHERE s.latitude IS NOT NULL AND s.longitude IS NOT NULL")
    List<State> findStatesWithCoordinates();

    @Query(value = "SELECT s.*, " +
                   "(6371 * acos(cos(radians(:latitude)) * cos(radians(s.latitude)) * " +
                   "cos(radians(s.longitude) - radians(:longitude)) + " +
                   "sin(radians(:latitude)) * sin(radians(s.latitude)))) AS distance " +
                   "FROM states s " +
                   "WHERE s.latitude IS NOT NULL AND s.longitude IS NOT NULL " +
                   "HAVING distance <= :radiusKm " +
                   "ORDER BY distance",
           nativeQuery = true)
    List<State> findStatesWithinRadius(@Param("latitude") Double latitude,
                                      @Param("longitude") Double longitude,
                                      @Param("radiusKm") Double radiusKm);

    // Existence checks
    boolean existsByNameAndCountry(String name, String country);
    boolean existsByStateCodeAndCountry(String stateCode, String country);
    boolean existsBySlug(String slug);
    boolean existsByNameAndCountryAndIdNot(String name, String country, Long id);
    boolean existsByStateCodeAndCountryAndIdNot(String stateCode, String country, Long id);
    boolean existsBySlugAndIdNot(String slug, Long id);

    // Counting methods
    long countByCountry(String country);
    long countByIsActive(Boolean isActive);
    long countByCountryAndIsActive(String country, Boolean isActive);

    // Statistics queries
    @Query("SELECT COUNT(s) FROM State s")
    long getTotalStatesCount();

    @Query("SELECT COUNT(s) FROM State s WHERE s.isActive = true")
    long getActiveStatesCount();

    @Query("SELECT AVG(SIZE(s.cities)) FROM State s")
    Double getAverageCitiesPerState();

    @Query("SELECT s.country, COUNT(s) FROM State s GROUP BY s.country ORDER BY COUNT(s) DESC")
    List<Object[]> getStateCountByCountry();

    // Bulk operations
    @Modifying
    @Query("UPDATE State s SET s.isActive = :isActive WHERE s.id IN :ids")
    int bulkUpdateActiveStatus(@Param("ids") List<Long> ids, @Param("isActive") Boolean isActive);

    @Modifying
    @Query("UPDATE State s SET s.displayOrder = :displayOrder WHERE s.id = :id")
    int updateDisplayOrder(@Param("id") Long id, @Param("displayOrder") Integer displayOrder);

    // For dropdown/select purposes
    @Query("SELECT s.id, s.name, s.stateCode FROM State s WHERE " +
           "(:country IS NULL OR s.country = :country) AND " +
           "s.isActive = true " +
           "ORDER BY s.displayOrder ASC, s.name ASC")
    List<Object[]> findStatesForDropdown(@Param("country") String country);

    // Active states ordered by display order and name
    @Query("SELECT s FROM State s WHERE s.isActive = true " +
           "ORDER BY s.displayOrder ASC, s.name ASC")
    List<State> findActiveStatesOrdered();

    @Query("SELECT s FROM State s WHERE s.country = :country AND s.isActive = true " +
           "ORDER BY s.displayOrder ASC, s.name ASC")
    List<State> findActiveStatesByCountryOrdered(@Param("country") String country);

    // Recently created states
    @Query("SELECT s FROM State s ORDER BY s.createdAt DESC")
    Page<State> findRecentStates(Pageable pageable);

    // States with most cities
    @Query("SELECT s FROM State s ORDER BY SIZE(s.cities) DESC")
    Page<State> findStatesByCityCount(Pageable pageable);

    // States by population (if available)
    @Query("SELECT s FROM State s WHERE s.population IS NOT NULL ORDER BY s.population DESC")
    List<State> findStatesByPopulationDesc();

    // States by area
    @Query("SELECT s FROM State s WHERE s.areaKm2 IS NOT NULL ORDER BY s.areaKm2 DESC")
    List<State> findStatesByAreaDesc();

    // Custom validation queries
    @Query("SELECT COUNT(s) > 0 FROM State s WHERE " +
           "LOWER(s.name) = LOWER(:name) AND LOWER(s.country) = LOWER(:country)")
    boolean existsByNameAndCountryIgnoreCase(@Param("name") String name, @Param("country") String country);

    @Query("SELECT COUNT(s) > 0 FROM State s WHERE " +
           "LOWER(s.stateCode) = LOWER(:stateCode) AND LOWER(s.country) = LOWER(:country)")
    boolean existsByStateCodeAndCountryIgnoreCase(@Param("stateCode") String stateCode, @Param("country") String country);
}
