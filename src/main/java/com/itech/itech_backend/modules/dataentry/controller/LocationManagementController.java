package com.itech.itech_backend.modules.dataentry.controller;

import com.itech.itech_backend.modules.core.model.Location;
import com.itech.itech_backend.modules.core.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/data-entry/locations")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class LocationManagementController {

    private final LocationRepository locationRepository;

    @GetMapping
    @PreAuthorize("hasRole('DATA_ENTRY') or hasRole('ADMIN')")
    public ResponseEntity<?> getAllLocations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String city) {
        try {
            log.info("Get all locations - page: {}, size: {}, search: {}, state: {}, city: {}", 
                    page, size, search, state, city);

            Pageable pageable = PageRequest.of(page, size);
            Page<Location> locations;

            if (search != null && !search.trim().isEmpty()) {
                locations = (Page<Location>) locationRepository.searchLocations(null, search, search);
            } else if (state != null && city != null) {
                locations = (Page<Location>) locationRepository.findByStateIgnoreCaseAndCityIgnoreCaseAndIsActiveTrue(state, city);
            } else if (state != null) {
                locations = (Page<Location>) locationRepository.findByStateIgnoreCaseAndIsActiveTrue(state);
            } else {
                locations = locationRepository.findAll(pageable);
            }

            return ResponseEntity.ok(Map.of(
                "success", true,
                "locations", locations.getContent(),
                "totalElements", locations.getTotalElements(),
                "totalPages", locations.getTotalPages(),
                "currentPage", locations.getNumber(),
                "size", locations.getSize()
            ));
        } catch (Exception e) {
            log.error("Error fetching locations: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch locations", "message", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('DATA_ENTRY') or hasRole('ADMIN')")
    public ResponseEntity<?> getLocationById(@PathVariable Long id) {
        try {
            Optional<Location> location = locationRepository.findById(id);
            if (location.isPresent()) {
                return ResponseEntity.ok(Map.of("success", true, "location", location.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error fetching location by id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch location", "message", e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('DATA_ENTRY') or hasRole('ADMIN')")
    public ResponseEntity<?> createLocation(@Valid @RequestBody Location location) {
        try {
            log.info("Creating new location: {}, {}, {}", location.getCity(), location.getState(), location.getCountry());

            // Check if location already exists
            if (locationRepository.existsByStateAndCityAndPincode(location.getState(), location.getCity(), location.getPincode())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Location already exists with same state, city, and pincode"));
            }

            Location savedLocation = locationRepository.save(location);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("success", true, "message", "Location created successfully", "location", savedLocation));
        } catch (Exception e) {
            log.error("Error creating location: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to create location", "message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DATA_ENTRY') or hasRole('ADMIN')")
    public ResponseEntity<?> updateLocation(@PathVariable Long id, @Valid @RequestBody Location locationData) {
        try {
            log.info("Updating location with id: {}", id);

            Optional<Location> existingLocationOpt = locationRepository.findById(id);
            if (existingLocationOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Location existingLocation = existingLocationOpt.get();
            existingLocation.setCountry(locationData.getCountry());
            existingLocation.setState(locationData.getState());
            existingLocation.setCity(locationData.getCity());
            existingLocation.setPincode(locationData.getPincode());
            existingLocation.setIsActive(locationData.getIsActive());

            Location updatedLocation = locationRepository.save(existingLocation);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Location updated successfully",
                "location", updatedLocation
            ));
        } catch (Exception e) {
            log.error("Error updating location {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update location", "message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DATA_ENTRY') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteLocation(@PathVariable Long id) {
        try {
            log.info("Deleting location with id: {}", id);

            Optional<Location> locationOpt = locationRepository.findById(id);
            if (locationOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Location location = locationOpt.get();
            location.setIsActive(false); // Soft delete
            locationRepository.save(location);

            return ResponseEntity.ok(Map.of("success", true, "message", "Location deleted successfully"));
        } catch (Exception e) {
            log.error("Error deleting location {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to delete location", "message", e.getMessage()));
        }
    }

    @GetMapping("/states")
    public ResponseEntity<?> getDistinctStates() {
        try {
            List<String> states = locationRepository.findDistinctActiveStates();
            return ResponseEntity.ok(Map.of("success", true, "states", states, "count", states.size()));
        } catch (Exception e) {
            log.error("Error fetching distinct states: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch states", "message", e.getMessage()));
        }
    }

    @GetMapping("/cities")
    public ResponseEntity<?> getCitiesByState(@RequestParam String state) {
        try {
            List<String> cities = locationRepository.findDistinctActiveCitiesByState(state);
            return ResponseEntity.ok(Map.of("success", true, "cities", cities, "count", cities.size()));
        } catch (Exception e) {
            log.error("Error fetching cities for state {}: {}", state, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch cities", "message", e.getMessage()));
        }
    }

    @GetMapping("/pincode/{pincode}")
    public ResponseEntity<?> getLocationByPincode(@PathVariable String pincode) {
        try {
            Optional<Location> location = locationRepository.findByPincodeAndIsActiveTrue(pincode);
            if (location.isPresent()) {
                return ResponseEntity.ok(Map.of("success", true, "location", location.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error fetching location by pincode {}: {}", pincode, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch location", "message", e.getMessage()));
        }
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasRole('DATA_ENTRY') or hasRole('ADMIN')")
    public ResponseEntity<?> bulkCreateLocations(@RequestBody List<Location> locations) {
        try {
            log.info("Bulk creating {} locations", locations.size());

            List<Location> savedLocations = locationRepository.saveAll(locations);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("success", true, "message", "Locations created successfully", 
                           "locations", savedLocations, "count", savedLocations.size()));
        } catch (Exception e) {
            log.error("Error bulk creating locations: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to create locations", "message", e.getMessage()));
        }
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('DATA_ENTRY') or hasRole('ADMIN')")
    public ResponseEntity<?> getLocationStats() {
        try {
            long totalLocations = locationRepository.count();
            List<String> states = locationRepository.findDistinctActiveStates();
            long activeLocations = locationRepository.findByIsActiveTrue().size();

            Map<String, Object> stats = Map.of(
                "totalLocations", totalLocations,
                "activeLocations", activeLocations,
                "totalStates", states.size(),
                "states", states
            );

            return ResponseEntity.ok(Map.of("success", true, "stats", stats));
        } catch (Exception e) {
            log.error("Error fetching location stats: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch location stats", "message", e.getMessage()));
        }
    }
}
