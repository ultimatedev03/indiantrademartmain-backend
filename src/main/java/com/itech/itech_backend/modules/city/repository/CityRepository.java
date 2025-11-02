package com.itech.itech_backend.modules.city.repository;

import com.itech.itech_backend.modules.city.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    
    // Find by name
    Optional<City> findByName(String name);
    
    // Find by slug
    Optional<City> findBySlug(String slug);
    
    // Find active cities
    List<City> findByIsActiveTrueOrderByDisplayOrderAscNameAsc();
    
    // Find cities by country
    List<City> findByCountryAndIsActiveTrueOrderByDisplayOrderAscNameAsc(String country);
    
    // Find cities by state/province
    List<City> findByStateProvinceAndIsActiveTrueOrderByDisplayOrderAscNameAsc(String stateProvince);
    
    // Find cities by country and state
    List<City> findByCountryAndStateProvinceAndIsActiveTrueOrderByDisplayOrderAscNameAsc(String country, String stateProvince);
    
    // Find major cities
    List<City> findByIsMajorCityTrueAndIsActiveTrueOrderByDisplayOrderAscNameAsc();
    
    // Find cities created by specific employee
    List<City> findByCreatedByEmployeeIdOrderByCreatedAtDesc(Long employeeId);
    
    // Search cities by name (case-insensitive)
    @Query("SELECT c FROM City c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) AND c.isActive = true ORDER BY c.displayOrder ASC, c.name ASC")
    List<City> findByNameContainingIgnoreCase(@Param("name") String name);
    
    // Search cities by multiple criteria
    @Query("SELECT c FROM City c WHERE " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.stateProvince) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.country) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.searchKeywords) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND c.isActive = true ORDER BY c.displayOrder ASC, c.name ASC")
    List<City> searchCities(@Param("search") String search);
    
    // Find cities with coordinates
    @Query("SELECT c FROM City c WHERE c.latitude IS NOT NULL AND c.longitude IS NOT NULL AND c.isActive = true ORDER BY c.name ASC")
    List<City> findCitiesWithCoordinates();
    
    // Find cities within a radius (if you need geo-spatial queries later)
    @Query(value = "SELECT * FROM cities c WHERE " +
                   "c.is_active = true AND " +
                   "c.latitude IS NOT NULL AND c.longitude IS NOT NULL AND " +
                   "(6371 * acos(cos(radians(:lat)) * cos(radians(c.latitude)) * cos(radians(c.longitude) - radians(:lng)) + sin(radians(:lat)) * sin(radians(c.latitude)))) <= :radius " +
                   "ORDER BY (6371 * acos(cos(radians(:lat)) * cos(radians(c.latitude)) * cos(radians(c.longitude) - radians(:lng)) + sin(radians(:lat)) * sin(radians(c.latitude))))",
           nativeQuery = true)
    List<City> findCitiesWithinRadius(@Param("lat") double latitude, 
                                      @Param("lng") double longitude, 
                                      @Param("radius") double radiusInKm);
    
    // Get distinct countries
    @Query("SELECT DISTINCT c.country FROM City c WHERE c.isActive = true ORDER BY c.country")
    List<String> findDistinctCountries();
    
    // Get distinct states/provinces for a country
    @Query("SELECT DISTINCT c.stateProvince FROM City c WHERE c.country = :country AND c.stateProvince IS NOT NULL AND c.isActive = true ORDER BY c.stateProvince")
    List<String> findDistinctStatesByCountry(@Param("country") String country);
    
    // Check if city name exists (excluding specific ID for updates)
    @Query("SELECT COUNT(c) > 0 FROM City c WHERE LOWER(c.name) = LOWER(:name) AND c.country = :country AND c.id != :excludeId")
    boolean existsByNameAndCountryIgnoreCaseAndIdNot(@Param("name") String name, @Param("country") String country, @Param("excludeId") Long excludeId);
    
    // Check if city name exists in country
    boolean existsByNameIgnoreCaseAndCountryIgnoreCase(String name, String country);
    
    // Count cities by country
    @Query("SELECT COUNT(c) FROM City c WHERE c.country = :country AND c.isActive = true")
    Long countByCountry(@Param("country") String country);
    
    // Count active cities
    Long countByIsActiveTrue();
    
    // Count major cities
    Long countByIsMajorCityTrueAndIsActiveTrue();
    
    // Get cities for dropdown (name and display name)
    @Query("SELECT c FROM City c WHERE c.isActive = true ORDER BY c.displayOrder ASC, c.name ASC")
    List<City> findAllForDropdown();
    
    // Find cities by postal code
    List<City> findByPostalCodeAndIsActiveTrue(String postalCode);
    
    // Custom query to find similar cities (for duplicate detection)
    @Query("SELECT c FROM City c WHERE " +
           "LOWER(c.name) = LOWER(:name) AND " +
           "LOWER(c.country) = LOWER(:country) AND " +
           "(c.stateProvince IS NULL OR LOWER(c.stateProvince) = LOWER(:state)) " +
           "ORDER BY c.createdAt DESC")
    List<City> findSimilarCities(@Param("name") String name, 
                                 @Param("country") String country, 
                                 @Param("state") String stateProvince);
}
