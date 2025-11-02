package com.itech.itech_backend.modules.dataentry.service;

import com.itech.itech_backend.modules.dataentry.entity.State;
import com.itech.itech_backend.modules.dataentry.repository.StateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StateService {

    private final StateRepository stateRepository;

    /**
     * Get state by ID
     */
    @Transactional(readOnly = true)
    public State getStateById(Long id) {
        log.debug("📋 Getting state by ID: {}", id);
        return stateRepository.findById(id).orElse(null);
    }

    /**
     * Get states with filtering and pagination
     */
    @Transactional(readOnly = true)
    public Page<State> getStatesWithFilters(Pageable pageable, String name, String country, 
                                           String stateCode, Boolean isActive) {
        log.debug("🔍 Getting states with filters - name: {}, country: {}, stateCode: {}, isActive: {}", 
                 name, country, stateCode, isActive);
        
        return stateRepository.findWithFilters(name, country, stateCode, isActive, pageable);
    }

    /**
     * Create a new state
     */
    public State createState(State state) {
        log.info("✅ Creating new state: {}", state.getName());
        
        // Validate state data
        validateStateData(state, null);
        
        // Check for duplicates
        if (stateRepository.existsByNameAndCountryIgnoreCase(state.getName(), state.getCountry())) {
            throw new IllegalArgumentException("State '" + state.getName() + "' already exists in " + state.getCountry());
        }
        
        if (state.getStateCode() != null && !state.getStateCode().isEmpty()) {
            if (stateRepository.existsByStateCodeAndCountryIgnoreCase(state.getStateCode(), state.getCountry())) {
                throw new IllegalArgumentException("State code '" + state.getStateCode() + "' already exists in " + state.getCountry());
            }
        }
        
        // Set defaults
        if (state.getIsActive() == null) {
            state.setIsActive(true);
        }
        if (state.getDisplayOrder() == null) {
            state.setDisplayOrder(0);
        }
        
        return stateRepository.save(state);
    }

    /**
     * Update an existing state
     */
    public State updateState(Long id, State updatedState) {
        log.info("🔄 Updating state ID: {}", id);
        
        State existingState = stateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("State not found with ID: " + id));
        
        // Validate updated data
        validateStateData(updatedState, id);
        
        // Check for duplicates (excluding current state)
        if (!existingState.getName().equalsIgnoreCase(updatedState.getName()) ||
            !existingState.getCountry().equalsIgnoreCase(updatedState.getCountry())) {
            if (stateRepository.existsByNameAndCountryAndIdNot(updatedState.getName(), updatedState.getCountry(), id)) {
                throw new IllegalArgumentException("State '" + updatedState.getName() + "' already exists in " + updatedState.getCountry());
            }
        }
        
        if (updatedState.getStateCode() != null && !updatedState.getStateCode().isEmpty()) {
            if (!updatedState.getStateCode().equalsIgnoreCase(existingState.getStateCode())) {
                if (stateRepository.existsByStateCodeAndCountryAndIdNot(updatedState.getStateCode(), updatedState.getCountry(), id)) {
                    throw new IllegalArgumentException("State code '" + updatedState.getStateCode() + "' already exists in " + updatedState.getCountry());
                }
            }
        }
        
        // Update fields
        existingState.setName(updatedState.getName());
        existingState.setStateCode(updatedState.getStateCode());
        existingState.setCountry(updatedState.getCountry());
        existingState.setCountryCode(updatedState.getCountryCode());
        existingState.setCapital(updatedState.getCapital());
        existingState.setPopulation(updatedState.getPopulation());
        existingState.setAreaKm2(updatedState.getAreaKm2());
        existingState.setLatitude(updatedState.getLatitude());
        existingState.setLongitude(updatedState.getLongitude());
        existingState.setTimeZone(updatedState.getTimeZone());
        existingState.setWebsiteUrl(updatedState.getWebsiteUrl());
        existingState.setNotes(updatedState.getNotes());
        existingState.setDisplayOrder(updatedState.getDisplayOrder());
        
        if (updatedState.getIsActive() != null) {
            existingState.setIsActive(updatedState.getIsActive());
        }
        
        return stateRepository.save(existingState);
    }

    /**
     * Toggle state active status
     */
    public State toggleActiveStatus(Long id) {
        log.info("🔄 Toggling active status for state ID: {}", id);
        
        State state = stateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("State not found with ID: " + id));
        
        state.setIsActive(!state.getIsActive());
        return stateRepository.save(state);
    }

    /**
     * Delete state (soft delete by deactivating)
     */
    public void deleteState(Long id) {
        log.info("🗑️ Deleting state ID: {}", id);
        
        State state = stateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("State not found with ID: " + id));
        
        // Check if state has active cities
        long activeCities = state.getActiveCityCount();
        if (activeCities > 0) {
            log.warn("⚠️ State has {} active cities, deactivating instead of deleting", activeCities);
            state.setIsActive(false);
            stateRepository.save(state);
        } else {
            // Safe to delete
            stateRepository.delete(state);
        }
    }

    /**
     * Bulk delete states
     */
    public void bulkDeleteStates(List<Long> stateIds) {
        log.info("🗑️ Bulk deleting {} states", stateIds.size());
        
        List<State> states = stateRepository.findAllById(stateIds);
        
        for (State state : states) {
            if (state.getActiveCityCount() > 0) {
                // Deactivate instead of delete if has active cities
                state.setIsActive(false);
            } else {
                stateRepository.delete(state);
            }
        }
        
        // Save deactivated states
        List<State> toSave = states.stream()
                .filter(s -> !s.getIsActive() && s.getId() != null)
                .collect(Collectors.toList());
        if (!toSave.isEmpty()) {
            stateRepository.saveAll(toSave);
        }
    }

    /**
     * Bulk update states
     */
    public List<State> bulkUpdateStates(List<Long> stateIds, Map<String, Object> updateData) {
        log.info("🔄 Bulk updating {} states", stateIds.size());
        
        List<State> states = stateRepository.findAllById(stateIds);
        
        for (State state : states) {
            // Apply bulk updates
            if (updateData.containsKey("isActive")) {
                state.setIsActive((Boolean) updateData.get("isActive"));
            }
            if (updateData.containsKey("displayOrder")) {
                state.setDisplayOrder((Integer) updateData.get("displayOrder"));
            }
            if (updateData.containsKey("timeZone")) {
                state.setTimeZone((String) updateData.get("timeZone"));
            }
            if (updateData.containsKey("countryCode")) {
                state.setCountryCode((String) updateData.get("countryCode"));
            }
        }
        
        return stateRepository.saveAll(states);
    }

    /**
     * Search states
     */
    @Transactional(readOnly = true)
    public Page<State> searchStates(String query, Pageable pageable) {
        log.debug("🔍 Searching states with query: {}", query);
        return stateRepository.searchByTerm(query, pageable);
    }

    /**
     * Get states for dropdown
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getStatesForDropdown(String country) {
        log.debug("📋 Getting states for dropdown - country: {}", country);
        
        List<Object[]> results = stateRepository.findStatesForDropdown(country);
        
        return results.stream()
                .map(result -> {
                    Map<String, Object> state = new HashMap<>();
                    state.put("id", result[0]);
                    state.put("name", result[1]);
                    state.put("stateCode", result[2]);
                    return state;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get distinct countries
     */
    @Transactional(readOnly = true)
    public List<String> getDistinctCountries() {
        log.debug("🌍 Getting distinct countries");
        return stateRepository.findDistinctCountries();
    }

    /**
     * Get distinct country codes
     */
    @Transactional(readOnly = true)
    public List<String> getDistinctCountryCodes() {
        log.debug("🌍 Getting distinct country codes");
        return stateRepository.findDistinctCountryCodes();
    }

    /**
     * Get states within radius
     */
    @Transactional(readOnly = true)
    public List<State> getStatesWithinRadius(Double latitude, Double longitude, Double radiusKm) {
        log.debug("📍 Getting states within {} km of coordinates: {}, {}", radiusKm, latitude, longitude);
        return stateRepository.findStatesWithinRadius(latitude, longitude, radiusKm);
    }

    /**
     * Update display order
     */
    public State updateDisplayOrder(Long id, Integer displayOrder) {
        log.info("🔄 Updating display order for state ID: {} to {}", id, displayOrder);
        
        State state = stateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("State not found with ID: " + id));
        
        state.setDisplayOrder(displayOrder);
        return stateRepository.save(state);
    }

    /**
     * Get state statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getStateStatistics() {
        log.debug("📊 Getting state statistics");
        
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalStates", stateRepository.getTotalStatesCount());
        stats.put("activeStates", stateRepository.getActiveStatesCount());
        stats.put("inactiveStates", stateRepository.countByIsActive(false));
        stats.put("avgCitiesPerState", stateRepository.getAverageCitiesPerState());
        
        // Countries breakdown
        List<Object[]> countryStats = stateRepository.getStateCountByCountry();
        Map<String, Long> countryBreakdown = new HashMap<>();
        for (Object[] stat : countryStats) {
            countryBreakdown.put((String) stat[0], (Long) stat[1]);
        }
        stats.put("statesByCountry", countryBreakdown);
        
        // Recent activity
        stats.put("totalCountries", stateRepository.findDistinctCountries().size());
        stats.put("statesWithCoordinates", stateRepository.findStatesWithCoordinates().size());
        
        return stats;
    }

    /**
     * Reorder states
     */
    public List<State> reorderStates(List<Map<String, Object>> stateOrders) {
        log.info("🔄 Reordering {} states", stateOrders.size());
        
        List<State> reorderedStates = stateOrders.stream()
                .map(orderData -> {
                    Long id = Long.valueOf(orderData.get("id").toString());
                    Integer order = Integer.valueOf(orderData.get("displayOrder").toString());
                    
                    State state = stateRepository.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("State not found with ID: " + id));
                    
                    state.setDisplayOrder(order);
                    return state;
                })
                .collect(Collectors.toList());
        
        return stateRepository.saveAll(reorderedStates);
    }

    /**
     * Get states by country (active only)
     */
    @Transactional(readOnly = true)
    public List<State> getActiveStatesByCountry(String country) {
        log.debug("📋 Getting active states for country: {}", country);
        return stateRepository.findActiveStatesByCountryOrdered(country);
    }

    /**
     * Get all active states
     */
    @Transactional(readOnly = true)
    public List<State> getAllActiveStates() {
        log.debug("📋 Getting all active states");
        return stateRepository.findActiveStatesOrdered();
    }

    // Private helper methods

    private void validateStateData(State state, Long excludeId) {
        if (state.getName() == null || state.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("State name is required");
        }
        
        if (state.getCountry() == null || state.getCountry().trim().isEmpty()) {
            throw new IllegalArgumentException("Country is required");
        }
        
        if (state.getName().length() < 2 || state.getName().length() > 100) {
            throw new IllegalArgumentException("State name must be between 2 and 100 characters");
        }
        
        if (state.getCountry().length() < 2 || state.getCountry().length() > 100) {
            throw new IllegalArgumentException("Country name must be between 2 and 100 characters");
        }
        
        if (state.getStateCode() != null && state.getStateCode().length() > 10) {
            throw new IllegalArgumentException("State code must not exceed 10 characters");
        }
        
        if (state.getCountryCode() != null && state.getCountryCode().length() > 3) {
            throw new IllegalArgumentException("Country code must not exceed 3 characters");
        }
        
        if (state.getCapital() != null && state.getCapital().length() > 100) {
            throw new IllegalArgumentException("Capital city name must not exceed 100 characters");
        }
        
        if (state.getTimeZone() != null && state.getTimeZone().length() > 50) {
            throw new IllegalArgumentException("Time zone must not exceed 50 characters");
        }
        
        if (state.getWebsiteUrl() != null && state.getWebsiteUrl().length() > 255) {
            throw new IllegalArgumentException("Website URL must not exceed 255 characters");
        }
        
        // Validate coordinates
        if (state.getLatitude() != null) {
            if (state.getLatitude().compareTo(BigDecimal.valueOf(-90)) < 0 || 
                state.getLatitude().compareTo(BigDecimal.valueOf(90)) > 0) {
                throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
            }
        }
        
        if (state.getLongitude() != null) {
            if (state.getLongitude().compareTo(BigDecimal.valueOf(-180)) < 0 || 
                state.getLongitude().compareTo(BigDecimal.valueOf(180)) > 0) {
                throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees");
            }
        }
        
        // Validate population
        if (state.getPopulation() != null && state.getPopulation() < 0) {
            throw new IllegalArgumentException("Population cannot be negative");
        }
        
        // Validate area
        if (state.getAreaKm2() != null && state.getAreaKm2().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Area cannot be negative");
        }
    }
}
