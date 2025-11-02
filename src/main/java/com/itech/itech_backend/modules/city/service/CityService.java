package com.itech.itech_backend.modules.city.service;

import com.itech.itech_backend.modules.city.model.City;
import com.itech.itech_backend.modules.city.repository.CityRepository;
import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CityService {

    private final CityRepository cityRepository;
    private final UserRepository userRepository;

    /**
     * Create a new city
     */
    @Transactional
    public City createCity(String name, String stateProvince, String country, String postalCode,
                           Double latitude, Double longitude, String timeZone, String notes,
                           Boolean isMajorCity, Boolean isActive, Long employeeId) {
        try {
            log.info("🆕 Creating new city: {}, {}, {}", name, stateProvince, country);

            // Validate city name uniqueness within country
            if (cityRepository.existsByNameIgnoreCaseAndCountryIgnoreCase(name, country)) {
                throw new IllegalArgumentException("City '" + name + "' already exists in " + country);
            }

            // Get employee
            User employee = userRepository.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

            // Create city
            City city = City.builder()
                    .name(name.trim())
                    .stateProvince(stateProvince != null ? stateProvince.trim() : null)
                    .country(country.trim())
                    .postalCode(postalCode != null ? postalCode.trim() : null)
                    .latitude(latitude)
                    .longitude(longitude)
                    .timeZone(timeZone)
                    .notes(notes)
                    .isMajorCity(isMajorCity != null ? isMajorCity : false)
                    .isActive(isActive != null ? isActive : true)
                    .createdByEmployee(employee)
                    .displayOrder(0)
                    .build();

            City savedCity = cityRepository.save(city);
            log.info("✅ City created successfully: {} (ID: {})", savedCity.getName(), savedCity.getId());
            
            return savedCity;

        } catch (Exception e) {
            log.error("❌ Error creating city: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create city: " + e.getMessage());
        }
    }

    /**
     * Get all cities with pagination
     */
    public Page<City> getAllCities(Pageable pageable) {
        log.info("📋 Getting all cities with pagination");
        return cityRepository.findAll(pageable);
    }

    /**
     * Get active cities only
     */
    public List<City> getActiveCities() {
        log.info("📋 Getting active cities");
        return cityRepository.findByIsActiveTrueOrderByDisplayOrderAscNameAsc();
    }

    /**
     * Get major cities
     */
    public List<City> getMajorCities() {
        log.info("🌟 Getting major cities");
        return cityRepository.findByIsMajorCityTrueAndIsActiveTrueOrderByDisplayOrderAscNameAsc();
    }

    /**
     * Get cities by country
     */
    public List<City> getCitiesByCountry(String country) {
        log.info("🌍 Getting cities for country: {}", country);
        return cityRepository.findByCountryAndIsActiveTrueOrderByDisplayOrderAscNameAsc(country);
    }

    /**
     * Get cities by state/province
     */
    public List<City> getCitiesByState(String stateProvince) {
        log.info("🗺️ Getting cities for state: {}", stateProvince);
        return cityRepository.findByStateProvinceAndIsActiveTrueOrderByDisplayOrderAscNameAsc(stateProvince);
    }

    /**
     * Get cities by country and state
     */
    public List<City> getCitiesByCountryAndState(String country, String stateProvince) {
        log.info("📍 Getting cities for country: {}, state: {}", country, stateProvince);
        return cityRepository.findByCountryAndStateProvinceAndIsActiveTrueOrderByDisplayOrderAscNameAsc(country, stateProvince);
    }

    /**
     * Get city by ID
     */
    public City getCityById(Long id) {
        log.info("🔍 Getting city by ID: {}", id);
        return cityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("City not found with ID: " + id));
    }

    /**
     * Get city by slug
     */
    public City getCityBySlug(String slug) {
        log.info("🔍 Getting city by slug: {}", slug);
        return cityRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("City not found with slug: " + slug));
    }

    /**
     * Search cities by name or other criteria
     */
    public List<City> searchCities(String searchTerm) {
        log.info("🔎 Searching cities with term: {}", searchTerm);
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getActiveCities();
        }
        return cityRepository.searchCities(searchTerm.trim());
    }

    /**
     * Get cities created by specific employee
     */
    public List<City> getCitiesByEmployee(Long employeeId) {
        log.info("👤 Getting cities created by employee ID: {}", employeeId);
        return cityRepository.findByCreatedByEmployeeIdOrderByCreatedAtDesc(employeeId);
    }

    /**
     * Get distinct countries
     */
    public List<String> getCountries() {
        log.info("🌎 Getting distinct countries");
        return cityRepository.findDistinctCountries();
    }

    /**
     * Get states/provinces for a country
     */
    public List<String> getStatesByCountry(String country) {
        log.info("🗺️ Getting states for country: {}", country);
        return cityRepository.findDistinctStatesByCountry(country);
    }

    /**
     * Get cities with coordinates
     */
    public List<City> getCitiesWithCoordinates() {
        log.info("🌐 Getting cities with coordinates");
        return cityRepository.findCitiesWithCoordinates();
    }

    /**
     * Find cities within radius
     */
    public List<City> findCitiesWithinRadius(double latitude, double longitude, double radiusInKm) {
        log.info("📍 Finding cities within {}km of ({}, {})", radiusInKm, latitude, longitude);
        return cityRepository.findCitiesWithinRadius(latitude, longitude, radiusInKm);
    }

    /**
     * Update city
     */
    @Transactional
    public City updateCity(Long id, Map<String, Object> updates) {
        try {
            log.info("🔄 Updating city ID: {} with updates: {}", id, updates);

            City city = getCityById(id);
            
            // Update name (with uniqueness check)
            if (updates.containsKey("name")) {
                String newName = (String) updates.get("name");
                if (!city.getName().equalsIgnoreCase(newName) && 
                    cityRepository.existsByNameAndCountryIgnoreCaseAndIdNot(newName, city.getCountry(), id)) {
                    throw new IllegalArgumentException("City '" + newName + "' already exists in " + city.getCountry());
                }
                city.setName(newName.trim());
            }

            // Update state/province
            if (updates.containsKey("stateProvince")) {
                String stateProvince = (String) updates.get("stateProvince");
                city.setStateProvince(stateProvince != null ? stateProvince.trim() : null);
            }

            // Update country
            if (updates.containsKey("country")) {
                String country = (String) updates.get("country");
                city.setCountry(country.trim());
            }

            // Update postal code
            if (updates.containsKey("postalCode")) {
                String postalCode = (String) updates.get("postalCode");
                city.setPostalCode(postalCode != null ? postalCode.trim() : null);
            }

            // Update coordinates
            if (updates.containsKey("latitude")) {
                Double latitude = (Double) updates.get("latitude");
                city.setLatitude(latitude);
            }

            if (updates.containsKey("longitude")) {
                Double longitude = (Double) updates.get("longitude");
                city.setLongitude(longitude);
            }

            // Update time zone
            if (updates.containsKey("timeZone")) {
                city.setTimeZone((String) updates.get("timeZone"));
            }

            // Update notes
            if (updates.containsKey("notes")) {
                city.setNotes((String) updates.get("notes"));
            }

            // Update major city status
            if (updates.containsKey("isMajorCity")) {
                Boolean isMajorCity = (Boolean) updates.get("isMajorCity");
                city.setIsMajorCity(isMajorCity);
            }

            // Update active status
            if (updates.containsKey("isActive")) {
                Boolean isActive = (Boolean) updates.get("isActive");
                city.setIsActive(isActive);
            }

            // Update display order
            if (updates.containsKey("displayOrder")) {
                Integer displayOrder = (Integer) updates.get("displayOrder");
                city.setDisplayOrder(displayOrder);
            }

            City updatedCity = cityRepository.save(city);
            log.info("✅ City updated successfully: {}", updatedCity.getName());
            
            return updatedCity;

        } catch (Exception e) {
            log.error("❌ Error updating city: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update city: " + e.getMessage());
        }
    }

    /**
     * Toggle city status
     */
    @Transactional
    public City toggleCityStatus(Long id, Boolean isActive) {
        log.info("🔄 Toggling city status - ID: {}, Active: {}", id, isActive);
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("isActive", isActive);
        
        return updateCity(id, updates);
    }

    /**
     * Delete city (soft delete by setting isActive to false)
     */
    @Transactional
    public void deleteCity(Long id) {
        try {
            log.info("🗑️ Soft deleting city ID: {}", id);

            City city = getCityById(id);
            
            // TODO: Check if city has dependencies (users, orders, etc.)
            // This would require checking other repositories

            city.setIsActive(false);
            cityRepository.save(city);
            
            log.info("✅ City soft deleted successfully: {}", city.getName());

        } catch (Exception e) {
            log.error("❌ Error deleting city: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete city: " + e.getMessage());
        }
    }

    /**
     * Get city statistics
     */
    public Map<String, Object> getCityStatistics() {
        log.info("📊 Generating city statistics");
        
        Map<String, Object> stats = new HashMap<>();
        
        long totalCities = cityRepository.count();
        long activeCities = cityRepository.countByIsActiveTrue();
        long majorCities = cityRepository.countByIsMajorCityTrueAndIsActiveTrue();
        
        // Get top countries by city count
        List<String> countries = getCountries();
        Map<String, Long> countryStats = new HashMap<>();
        
        for (String country : countries.stream().limit(10).collect(Collectors.toList())) {
            long count = cityRepository.countByCountry(country);
            countryStats.put(country, count);
        }
        
        stats.put("totalCities", totalCities);
        stats.put("activeCities", activeCities);
        stats.put("inactiveCities", totalCities - activeCities);
        stats.put("majorCities", majorCities);
        stats.put("totalCountries", countries.size());
        stats.put("topCountries", countryStats);
        
        return stats;
    }

    /**
     * Get cities for dropdown (formatted for UI)
     */
    public List<Map<String, Object>> getCitiesForDropdown() {
        log.info("📋 Getting cities for dropdown");
        
        List<City> cities = cityRepository.findAllForDropdown();
        
        return cities.stream()
                .map(city -> {
                    Map<String, Object> cityData = new HashMap<>();
                    cityData.put("id", city.getId());
                    cityData.put("name", city.getName());
                    cityData.put("displayName", city.getDisplayName());
                    cityData.put("fullName", city.getFullName());
                    cityData.put("country", city.getCountry());
                    cityData.put("stateProvince", city.getStateProvince());
                    cityData.put("isMajorCity", city.getIsMajorCity());
                    return cityData;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get filtered cities with search and pagination
     */
    public Page<City> getFilteredCities(String search, Boolean isActive, String country, 
                                        String stateProvince, Boolean isMajorCity, Pageable pageable) {
        log.info("🔍 Getting filtered cities - Search: {}, Active: {}, Country: {}, State: {}, Major: {}", 
                search, isActive, country, stateProvince, isMajorCity);
        
        List<City> allCities = cityRepository.findAll();
        
        // Apply filters
        List<City> filteredCities = allCities.stream()
                .filter(city -> {
                    // Search filter
                    if (search != null && !search.trim().isEmpty()) {
                        String searchLower = search.toLowerCase();
                        if (!city.getName().toLowerCase().contains(searchLower) &&
                            (city.getStateProvince() == null || !city.getStateProvince().toLowerCase().contains(searchLower)) &&
                            !city.getCountry().toLowerCase().contains(searchLower) &&
                            (city.getSearchKeywords() == null || !city.getSearchKeywords().toLowerCase().contains(searchLower))) {
                            return false;
                        }
                    }
                    
                    // Active filter
                    if (isActive != null && !city.getIsActive().equals(isActive)) {
                        return false;
                    }
                    
                    // Country filter
                    if (country != null && !country.trim().isEmpty() && 
                        !city.getCountry().equalsIgnoreCase(country)) {
                        return false;
                    }
                    
                    // State filter
                    if (stateProvince != null && !stateProvince.trim().isEmpty()) {
                        if (city.getStateProvince() == null || 
                            !city.getStateProvince().equalsIgnoreCase(stateProvince)) {
                            return false;
                        }
                    }
                    
                    // Major city filter
                    if (isMajorCity != null && !city.getIsMajorCity().equals(isMajorCity)) {
                        return false;
                    }
                    
                    return true;
                })
                .sorted(Comparator.comparing(City::getDisplayOrder)
                        .thenComparing(City::getName))
                .collect(Collectors.toList());
        
        // Apply pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredCities.size());
        
        List<City> pageContent = filteredCities.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, filteredCities.size());
    }

    /**
     * Find similar cities (for duplicate detection)
     */
    public List<City> findSimilarCities(String name, String country, String stateProvince) {
        log.info("🔍 Finding similar cities to: {}, {}, {}", name, stateProvince, country);
        return cityRepository.findSimilarCities(name, country, stateProvince);
    }

    /**
     * Validate city data
     */
    public Map<String, String> validateCityData(String name, String country, String stateProvince, 
                                                Double latitude, Double longitude) {
        Map<String, String> errors = new HashMap<>();
        
        if (name == null || name.trim().isEmpty()) {
            errors.put("name", "City name is required");
        } else if (name.trim().length() < 2) {
            errors.put("name", "City name must be at least 2 characters long");
        } else if (name.trim().length() > 100) {
            errors.put("name", "City name must be less than 100 characters");
        }
        
        if (country == null || country.trim().isEmpty()) {
            errors.put("country", "Country is required");
        } else if (country.trim().length() < 2) {
            errors.put("country", "Country must be at least 2 characters long");
        }
        
        if (stateProvince != null && stateProvince.trim().length() > 100) {
            errors.put("stateProvince", "State/Province must be less than 100 characters");
        }
        
        if (latitude != null && (latitude < -90 || latitude > 90)) {
            errors.put("latitude", "Latitude must be between -90 and 90 degrees");
        }
        
        if (longitude != null && (longitude < -180 || longitude > 180)) {
            errors.put("longitude", "Longitude must be between -180 and 180 degrees");
        }
        
        // Check for duplicate city
        if (name != null && country != null) {
            if (cityRepository.existsByNameIgnoreCaseAndCountryIgnoreCase(name.trim(), country.trim())) {
                errors.put("duplicate", "City '" + name.trim() + "' already exists in " + country.trim());
            }
        }
        
        return errors;
    }
}
