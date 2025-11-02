package com.itech.itech_backend.modules.dataentry.controller;

import com.itech.itech_backend.modules.city.model.City;
import com.itech.itech_backend.modules.city.service.CityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/dataentry/cities", "/api/cities", "/api/public/cities"})
@RequiredArgsConstructor
@Slf4j
public class CityController {

    private final CityService cityService;

    /**
     * Get all cities with pagination and filtering (Main endpoint for frontend)
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) Boolean isMajorCity) {
        try {
            log.info("Cities request - page: {}, size: {}, search: {}, country: {}, state: {}, active: {}, major: {}", 
                    page, size, search, country, state, isActive, isMajorCity);

            Pageable pageable = PageRequest.of(page, size);
            Page<City> cities = cityService.getFilteredCities(search, isActive, country, state, isMajorCity, pageable);

            Map<String, Object> response = createSuccessResponse("Cities retrieved successfully", cities);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting cities: {}", e.getMessage(), e);
            return createErrorResponse("Failed to retrieve cities: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get city by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCityById(@PathVariable Long id) {
        try {
            log.info("🔍 Get city by ID request: {}", id);

            City city = cityService.getCityById(id);
            if (city == null) {
                return createErrorResponse("City not found", HttpStatus.NOT_FOUND);
            }

            Map<String, Object> response = createSuccessResponse("City retrieved successfully", city);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting city by ID: {}", e.getMessage(), e);
            return createErrorResponse("Failed to retrieve city: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Create a new city
     */
    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createCity(@Valid @RequestBody City city) {
        try {
            log.info("✅ Create city request: {}", city.getName());

            // This would need to be implemented or adapted based on available CityService methods
            throw new UnsupportedOperationException("Create city method needs implementation");

        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Invalid city data: {}", e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("❌ Error creating city: {}", e.getMessage(), e);
            return createErrorResponse("Failed to create city: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update an existing city
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateCity(@PathVariable Long id, @Valid @RequestBody City city) {
        try {
            log.info("🔄 Update city request: ID {}, name: {}", id, city.getName());

            // This would need to be implemented or adapted based on available CityService methods
            throw new UnsupportedOperationException("Update city method needs implementation");

        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Invalid city data or city not found: {}", e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("❌ Error updating city: {}", e.getMessage(), e);
            return createErrorResponse("Failed to update city: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Toggle city active status
     */
    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> toggleCityActiveStatus(@PathVariable Long id) {
        return toggleCityStatus(id);
    }

    /**
     * Toggle city status (alias for toggle-active)
     */
    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> toggleCityStatus(@PathVariable Long id) {
        try {
            log.info("🔄 Toggle city active status: ID {}", id);

            // This would need to be implemented or adapted based on available CityService methods
            throw new UnsupportedOperationException("Toggle city status method needs implementation");

        } catch (IllegalArgumentException e) {
            log.warn("⚠️ City not found: {}", e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("❌ Error toggling city status: {}", e.getMessage(), e);
            return createErrorResponse("Failed to toggle city status: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete a city (soft delete)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteCity(@PathVariable Long id) {
        try {
            log.info("🗑️ Delete city request: ID {}", id);

            cityService.deleteCity(id);

            Map<String, Object> response = createSuccessResponse("City deleted successfully", null);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("⚠️ City not found: {}", e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("❌ Error deleting city: {}", e.getMessage(), e);
            return createErrorResponse("Failed to delete city: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Bulk delete cities
     */
    @DeleteMapping("/bulk")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> bulkDeleteCities(@RequestBody List<Long> cityIds) {
        try {
            log.info("🗑️ Bulk delete cities request: {} cities", cityIds.size());

            // This would need to be implemented or adapted based on available CityService methods
            throw new UnsupportedOperationException("Bulk delete cities method needs implementation");

        } catch (Exception e) {
            log.error("❌ Error bulk deleting cities: {}", e.getMessage(), e);
            return createErrorResponse("Failed to delete cities: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Bulk update cities
     */
    @PatchMapping("/bulk")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> bulkUpdateCities(@RequestBody Map<String, Object> updateData) {
        try {
            @SuppressWarnings("unchecked")
            List<Long> cityIds = (List<Long>) updateData.get("cityIds");
            
            log.info("🔄 Bulk update cities request: {} cities", cityIds.size());

            // This would need to be implemented or adapted based on available CityService methods
            throw new UnsupportedOperationException("Bulk update cities method needs implementation");

        } catch (Exception e) {
            log.error("❌ Error bulk updating cities: {}", e.getMessage(), e);
            return createErrorResponse("Failed to update cities: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Search cities
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchCities(
            @RequestParam String query,
            Pageable pageable) {
        try {
            log.info("🔍 Search cities request: query '{}', page: {}, size: {}", 
                    query, pageable.getPageNumber(), pageable.getPageSize());

            List<City> searchResults = cityService.searchCities(query);
            Page<City> cities = new org.springframework.data.domain.PageImpl<>(
                searchResults, pageable, searchResults.size());

            Map<String, Object> response = createSuccessResponse("Cities search completed", cities);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error searching cities: {}", e.getMessage(), e);
            return createErrorResponse("Failed to search cities: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get cities for dropdown (name and ID only)
     */
    @GetMapping("/dropdown")
    public ResponseEntity<Map<String, Object>> getCitiesForDropdown(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) Boolean majorCitiesOnly) {
        try {
            log.info("📋 Get cities for dropdown: country: {}, state: {}, majorOnly: {}", 
                    country, state, majorCitiesOnly);

            List<Map<String, Object>> cities = cityService.getCitiesForDropdown();

            Map<String, Object> response = createSuccessResponse("Cities for dropdown retrieved successfully", cities);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting cities for dropdown: {}", e.getMessage(), e);
            return createErrorResponse("Failed to get cities for dropdown: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get distinct countries
     */
    @GetMapping("/countries")
    public ResponseEntity<Map<String, Object>> getDistinctCountries() {
        try {
            log.info("🌍 Get distinct countries request");

            List<String> countries = cityService.getCountries();

            Map<String, Object> response = createSuccessResponse("Countries retrieved successfully", countries);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting countries: {}", e.getMessage(), e);
            return createErrorResponse("Failed to get countries: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get distinct states/provinces for a country
     */
    @GetMapping("/states")
    public ResponseEntity<Map<String, Object>> getDistinctStates(@RequestParam String country) {
        try {
            log.info("🏛️ Get distinct states request for country: {}", country);

            List<String> states = cityService.getStatesByCountry(country);

            Map<String, Object> response = createSuccessResponse("States retrieved successfully", states);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting states: {}", e.getMessage(), e);
            return createErrorResponse("Failed to get states: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get cities within radius of coordinates
     */
    @GetMapping("/nearby")
    public ResponseEntity<Map<String, Object>> getNearbyCities(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam Double radiusKm) {
        try {
            log.info("📍 Get nearby cities request: lat: {}, lon: {}, radius: {} km", 
                    latitude, longitude, radiusKm);

            List<City> cities = cityService.findCitiesWithinRadius(latitude, longitude, radiusKm);

            Map<String, Object> response = createSuccessResponse("Nearby cities retrieved successfully", cities);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting nearby cities: {}", e.getMessage(), e);
            return createErrorResponse("Failed to get nearby cities: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update city display order
     */
    @PatchMapping("/{id}/display-order")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateCityDisplayOrder(
            @PathVariable Long id,
            @RequestParam Integer displayOrder) {
        try {
            log.info("🔄 Update city display order: ID {}, order: {}", id, displayOrder);

            // This would need to be implemented or adapted based on available CityService methods
            throw new UnsupportedOperationException("Update display order method needs implementation");

        } catch (IllegalArgumentException e) {
            log.warn("⚠️ City not found: {}", e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("❌ Error updating city display order: {}", e.getMessage(), e);
            return createErrorResponse("Failed to update display order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get city statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getCityStatistics() {
        try {
            log.info("📊 Get city statistics request");

            Map<String, Object> statistics = cityService.getCityStatistics();

            Map<String, Object> response = createSuccessResponse("City statistics retrieved successfully", statistics);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting city statistics: {}", e.getMessage(), e);
            return createErrorResponse("Failed to get statistics: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Reorder cities
     */
    @PatchMapping("/reorder")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> reorderCities(@RequestBody List<Map<String, Object>> cityOrders) {
        try {
            log.info("🔄 Reorder cities request: {} cities", cityOrders.size());

            // This would need to be implemented or adapted based on available CityService methods
            throw new UnsupportedOperationException("Reorder cities method needs implementation");

        } catch (Exception e) {
            log.error("❌ Error reordering cities: {}", e.getMessage(), e);
            return createErrorResponse("Failed to reorder cities: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get cities with pagination (explicit endpoint for frontend)
     */
    @GetMapping("/paginated")
    public ResponseEntity<Map<String, Object>> getCitiesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String stateId,
            @RequestParam(required = false) String search) {
        try {
            log.info("📋 Get cities paginated - page: {}, size: {}, stateId: {}, search: {}", page, size, stateId, search);

            Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
            Page<City> cities;
            
            if (stateId != null) {
                cities = cityService.getFilteredCities(search, true, null, stateId, null, pageable);
            } else {
                cities = cityService.getFilteredCities(search, true, null, null, null, pageable);
            }

            Map<String, Object> response = createSuccessResponse("Cities retrieved successfully", cities);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting cities paginated: {}", e.getMessage(), e);
            return createErrorResponse("Failed to retrieve cities: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Private helper methods

    private Map<String, Object> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        if (data != null) {
            response.put("data", data);
        }
        return response;
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        return ResponseEntity.status(status).body(errorResponse);
    }
}
