package com.itech.itech_backend.modules.vendor.controller;

import com.itech.itech_backend.enums.VendorType;
import com.itech.itech_backend.modules.vendor.model.Vendors;
import com.itech.itech_backend.modules.vendor.service.VendorsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vendors")
@RequiredArgsConstructor
public class VendorsController {

    private final VendorsService vendorsService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Vendors>> getAllVendors() {
        List<Vendors> vendors = vendorsService.getAllVendors();
        return ResponseEntity.ok(vendors);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDOR')")
    public ResponseEntity<Vendors> getVendorById(@PathVariable Long id) {
        Optional<Vendors> vendor = vendorsService.getVendorById(id);
        return vendor.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Vendors> getVendorByEmail(@PathVariable String email) {
        Optional<Vendors> vendor = vendorsService.getVendorByEmail(email);
        return vendor.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/phone/{phone}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Vendors> getVendorByPhone(@PathVariable String phone) {
        Optional<Vendors> vendor = vendorsService.getVendorByPhone(phone);
        return vendor.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/gst/{gstNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Vendors> getVendorByGst(@PathVariable String gstNumber) {
        Optional<Vendors> vendor = vendorsService.getVendorByGst(gstNumber);
        return vendor.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pan/{panNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Vendors> getVendorByPan(@PathVariable String panNumber) {
        Optional<Vendors> vendor = vendorsService.getVendorByPan(panNumber);
        return vendor.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDOR')")
    public ResponseEntity<Vendors> updateVendor(@PathVariable Long id, @RequestBody Vendors vendor) {
        Optional<Vendors> existingVendor = vendorsService.getVendorById(id);
        if (existingVendor.isPresent()) {
            vendor.setId(id);
            Vendors updatedVendor = vendorsService.saveVendor(vendor);
            return ResponseEntity.ok(updatedVendor);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVendor(@PathVariable Long id) {
        if (vendorsService.getVendorById(id).isPresent()) {
            vendorsService.deleteVendor(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/verified")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Vendors>> getVerifiedVendors() {
        List<Vendors> vendors = vendorsService.getVerifiedVendors();
        return ResponseEntity.ok(vendors);
    }

    @GetMapping("/unverified")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Vendors>> getUnverifiedVendors() {
        List<Vendors> vendors = vendorsService.getUnverifiedVendors();
        return ResponseEntity.ok(vendors);
    }

    @GetMapping("/type/{vendorType}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Vendors>> getVendorsByType(@PathVariable VendorType vendorType) {
        List<Vendors> vendors = vendorsService.getVendorsByType(vendorType);
        return ResponseEntity.ok(vendors);
    }

    @GetMapping("/with-gst")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Vendors>> getVendorsWithGst() {
        List<Vendors> vendors = vendorsService.getVendorsWithGst();
        return ResponseEntity.ok(vendors);
    }

    @GetMapping("/with-pan")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Vendors>> getVendorsWithPan() {
        List<Vendors> vendors = vendorsService.getVendorsWithPan();
        return ResponseEntity.ok(vendors);
    }

    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getVendorCount() {
        long count = vendorsService.getVendorCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/exists/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable String email) {
        boolean exists = vendorsService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists/phone/{phone}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> existsByPhone(@PathVariable String phone) {
        boolean exists = vendorsService.existsByPhone(phone);
        return ResponseEntity.ok(exists);
    }
    
    // City-based endpoints
    @GetMapping("/city/{city}")
    public ResponseEntity<List<Vendors>> getVendorsByCity(@PathVariable String city) {
        List<Vendors> vendors = vendorsService.getVendorsByCity(city);
        return ResponseEntity.ok(vendors);
    }
    
    @GetMapping("/city/{city}/verified")
    public ResponseEntity<List<Vendors>> getVerifiedVendorsByCity(@PathVariable String city) {
        List<Vendors> vendors = vendorsService.getVerifiedVendorsByCity(city);
        return ResponseEntity.ok(vendors);
    }
    
    @GetMapping("/state/{state}")
    public ResponseEntity<List<Vendors>> getVendorsByState(@PathVariable String state) {
        List<Vendors> vendors = vendorsService.getVendorsByState(state);
        return ResponseEntity.ok(vendors);
    }
    
    @GetMapping("/state/{state}/verified")
    public ResponseEntity<List<Vendors>> getVerifiedVendorsByState(@PathVariable String state) {
        List<Vendors> vendors = vendorsService.getVerifiedVendorsByState(state);
        return ResponseEntity.ok(vendors);
    }
    
    @GetMapping("/cities")
    public ResponseEntity<List<String>> getAllCities() {
        List<String> cities = vendorsService.getAllCities();
        return ResponseEntity.ok(cities);
    }
    
    @GetMapping("/states")
    public ResponseEntity<List<String>> getAllStates() {
        List<String> states = vendorsService.getAllStates();
        return ResponseEntity.ok(states);
    }
    
    @GetMapping("/city/{city}/count")
    public ResponseEntity<Long> getVerifiedVendorCountByCity(@PathVariable String city) {
        Long count = vendorsService.getVerifiedVendorCountByCity(city);
        return ResponseEntity.ok(count);
    }
}

