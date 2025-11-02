package com.itech.itech_backend.modules.dataentry.controller;

import com.itech.itech_backend.modules.dataentry.entity.State;
import com.itech.itech_backend.modules.dataentry.service.StateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dataentry/states")
@RequiredArgsConstructor
@Slf4j
public class StateController {

    private final StateService stateService;

    /**
     * Get all states with pagination and filtering
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllStates(
            Pageable pageable,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String stateCode,
            @RequestParam(required = false) Boolean isActive) {
        try {
            log.info("🏛️ Get states request - page: {}, size: {}, name: {}, country: {}, stateCode: {}, isActive: {}", 
                    pageable.getPageNumber(), pageable.getPageSize(), name, country, stateCode, isActive);

            Page<State> states = stateService.getStatesWithFilters(
                pageable, name, country, stateCode, isActive);

            Map<String, Object> response = createSuccessResponse("States retrieved successfully", states);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting states: {}", e.getMessage(), e);
            return createErrorResponse("Failed to retrieve states: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get state by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getStateById(@PathVariable Long id) {
        try {
            log.info("🔍 Get state by ID request: {}", id);

            State state = stateService.getStateById(id);
            if (state == null) {
                return createErrorResponse("State not found", HttpStatus.NOT_FOUND);
            }

            Map<String, Object> response = createSuccessResponse("State retrieved successfully", state);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting state by ID: {}", e.getMessage(), e);
            return createErrorResponse("Failed to retrieve state: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Create a new state
     */
    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createState(@Valid @RequestBody State state) {
        try {
            log.info("✅ Create state request: {}", state.getName());

            State savedState = stateService.createState(state);

            Map<String, Object> response = createSuccessResponse("State created successfully", savedState);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Invalid state data: {}", e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("❌ Error creating state: {}", e.getMessage(), e);
            return createErrorResponse("Failed to create state: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update an existing state
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateState(@PathVariable Long id, @Valid @RequestBody State state) {
        try {
            log.info("🔄 Update state request: ID {}, name: {}", id, state.getName());

            State updatedState = stateService.updateState(id, state);

            Map<String, Object> response = createSuccessResponse("State updated successfully", updatedState);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Invalid state data or state not found: {}", e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("❌ Error updating state: {}", e.getMessage(), e);
            return createErrorResponse("Failed to update state: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Toggle state active status
     */
    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> toggleStateActiveStatus(@PathVariable Long id) {
        return toggleStateStatus(id);
    }

    /**
     * Toggle state status (alias for toggle-active)
     */
    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> toggleStateStatus(@PathVariable Long id) {
        try {
            log.info("🔄 Toggle state active status: ID {}", id);

            State updatedState = stateService.toggleActiveStatus(id);

            String status = updatedState.getIsActive() ? "activated" : "deactivated";
            Map<String, Object> response = createSuccessResponse("State " + status + " successfully", updatedState);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("⚠️ State not found: {}", e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("❌ Error toggling state status: {}", e.getMessage(), e);
            return createErrorResponse("Failed to toggle state status: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete a state (soft delete)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteState(@PathVariable Long id) {
        try {
            log.info("🗑️ Delete state request: ID {}", id);

            stateService.deleteState(id);

            Map<String, Object> response = createSuccessResponse("State deleted successfully", null);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("⚠️ State not found: {}", e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("❌ Error deleting state: {}", e.getMessage(), e);
            return createErrorResponse("Failed to delete state: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Bulk delete states
     */
    @DeleteMapping("/bulk")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> bulkDeleteStates(@RequestBody List<Long> stateIds) {
        try {
            log.info("🗑️ Bulk delete states request: {} states", stateIds.size());

            stateService.bulkDeleteStates(stateIds);

            Map<String, Object> response = createSuccessResponse(
                "States deleted successfully (" + stateIds.size() + " states)", null);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error bulk deleting states: {}", e.getMessage(), e);
            return createErrorResponse("Failed to delete states: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Bulk update states
     */
    @PatchMapping("/bulk")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> bulkUpdateStates(@RequestBody Map<String, Object> updateData) {
        try {
            @SuppressWarnings("unchecked")
            List<Long> stateIds = (List<Long>) updateData.get("stateIds");
            
            log.info("🔄 Bulk update states request: {} states", stateIds.size());

            List<State> updatedStates = stateService.bulkUpdateStates(stateIds, updateData);

            Map<String, Object> response = createSuccessResponse(
                "States updated successfully (" + updatedStates.size() + " states)", updatedStates);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error bulk updating states: {}", e.getMessage(), e);
            return createErrorResponse("Failed to update states: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Search states
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchStates(
            @RequestParam String query,
            Pageable pageable) {
        try {
            log.info("🔍 Search states request: query '{}', page: {}, size: {}", 
                    query, pageable.getPageNumber(), pageable.getPageSize());

            Page<State> states = stateService.searchStates(query, pageable);

            Map<String, Object> response = createSuccessResponse("States search completed", states);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error searching states: {}", e.getMessage(), e);
            return createErrorResponse("Failed to search states: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get states for dropdown (name and ID only)
     */
    @GetMapping("/dropdown")
    public ResponseEntity<Map<String, Object>> getStatesForDropdown(
            @RequestParam(required = false) String country) {
        try {
            log.info("📋 Get states for dropdown: country: {}", country);

            List<Map<String, Object>> states = stateService.getStatesForDropdown(country);

            Map<String, Object> response = createSuccessResponse("States for dropdown retrieved successfully", states);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting states for dropdown: {}", e.getMessage(), e);
            return createErrorResponse("Failed to get states for dropdown: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get distinct countries
     */
    @GetMapping("/countries")
    public ResponseEntity<Map<String, Object>> getDistinctCountries() {
        try {
            log.info("🌍 Get distinct countries request");

            List<String> countries = stateService.getDistinctCountries();

            Map<String, Object> response = createSuccessResponse("Countries retrieved successfully", countries);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting countries: {}", e.getMessage(), e);
            return createErrorResponse("Failed to get countries: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get distinct country codes
     */
    @GetMapping("/country-codes")
    public ResponseEntity<Map<String, Object>> getDistinctCountryCodes() {
        try {
            log.info("🌍 Get distinct country codes request");

            List<String> countryCodes = stateService.getDistinctCountryCodes();

            Map<String, Object> response = createSuccessResponse("Country codes retrieved successfully", countryCodes);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting country codes: {}", e.getMessage(), e);
            return createErrorResponse("Failed to get country codes: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get states within radius of coordinates
     */
    @GetMapping("/nearby")
    public ResponseEntity<Map<String, Object>> getNearbyStates(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam Double radiusKm) {
        try {
            log.info("📍 Get nearby states request: lat: {}, lon: {}, radius: {} km", 
                    latitude, longitude, radiusKm);

            List<State> states = stateService.getStatesWithinRadius(latitude, longitude, radiusKm);

            Map<String, Object> response = createSuccessResponse("Nearby states retrieved successfully", states);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting nearby states: {}", e.getMessage(), e);
            return createErrorResponse("Failed to get nearby states: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update state display order
     */
    @PatchMapping("/{id}/display-order")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateStateDisplayOrder(
            @PathVariable Long id,
            @RequestParam Integer displayOrder) {
        try {
            log.info("🔄 Update state display order: ID {}, order: {}", id, displayOrder);

            State updatedState = stateService.updateDisplayOrder(id, displayOrder);

            Map<String, Object> response = createSuccessResponse("State display order updated successfully", updatedState);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("⚠️ State not found: {}", e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("❌ Error updating state display order: {}", e.getMessage(), e);
            return createErrorResponse("Failed to update display order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get state statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStateStatistics() {
        try {
            log.info("📊 Get state statistics request");

            Map<String, Object> statistics = stateService.getStateStatistics();

            Map<String, Object> response = createSuccessResponse("State statistics retrieved successfully", statistics);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting state statistics: {}", e.getMessage(), e);
            return createErrorResponse("Failed to get statistics: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Reorder states
     */
    @PatchMapping("/reorder")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> reorderStates(@RequestBody List<Map<String, Object>> stateOrders) {
        try {
            log.info("🔄 Reorder states request: {} states", stateOrders.size());

            List<State> reorderedStates = stateService.reorderStates(stateOrders);

            Map<String, Object> response = createSuccessResponse(
                "States reordered successfully (" + reorderedStates.size() + " states)", reorderedStates);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error reordering states: {}", e.getMessage(), e);
            return createErrorResponse("Failed to reorder states: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get active states by country
     */
    @GetMapping("/by-country/{country}")
    public ResponseEntity<Map<String, Object>> getActiveStatesByCountry(@PathVariable String country) {
        try {
            log.info("🏛️ Get active states by country: {}", country);

            List<State> states = stateService.getActiveStatesByCountry(country);

            Map<String, Object> response = createSuccessResponse("States retrieved successfully", states);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting states by country: {}", e.getMessage(), e);
            return createErrorResponse("Failed to get states: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get all active states
     */
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getAllActiveStates() {
        try {
            log.info("🏛️ Get all active states request");

            List<State> states = stateService.getAllActiveStates();

            Map<String, Object> response = createSuccessResponse("Active states retrieved successfully", states);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting active states: {}", e.getMessage(), e);
            return createErrorResponse("Failed to get active states: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get states with pagination (explicit endpoint for frontend)
     */
    @GetMapping("/paginated")
    public ResponseEntity<Map<String, Object>> getStatesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {
        try {
            log.info("📋 Get states paginated - page: {}, size: {}, search: {}", page, size, search);

            Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
            Page<State> states = stateService.getStatesWithFilters(pageable, search, null, null, null);

            Map<String, Object> response = createSuccessResponse("States retrieved successfully", states);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting states paginated: {}", e.getMessage(), e);
            return createErrorResponse("Failed to retrieve states: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get cities by state ID
     */
    @GetMapping("/{stateId}/cities")
    public ResponseEntity<Map<String, Object>> getCitiesByState(@PathVariable Long stateId) {
        try {
            log.info("🏙️ Get cities by state ID: {}", stateId);

            // This would need to be implemented based on available StateService methods
            throw new UnsupportedOperationException("Get cities by state method needs implementation");

        } catch (Exception e) {
            log.error("❌ Error getting cities by state: {}", e.getMessage(), e);
            return createErrorResponse("Failed to get cities: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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
