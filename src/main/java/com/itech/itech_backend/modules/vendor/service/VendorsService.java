package com.itech.itech_backend.modules.vendor.service;

import com.itech.itech_backend.enums.VendorType;
import com.itech.itech_backend.modules.vendor.model.Vendors;
import com.itech.itech_backend.modules.vendor.repository.VendorsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VendorsService {

    private final VendorsRepository vendorsRepository;

    public List<Vendors> getAllVendors() {
        return vendorsRepository.findAll();
    }

    public Optional<Vendors> getVendorById(Long id) {
        return vendorsRepository.findById(id);
    }

    public Optional<Vendors> getVendorByEmail(String email) {
        return vendorsRepository.findByEmail(email);
    }

    public Optional<Vendors> getVendorByPhone(String phone) {
        return vendorsRepository.findByPhone(phone);
    }

    public Optional<Vendors> getVendorByGst(String gstNumber) {
        return vendorsRepository.findByGstNumber(gstNumber);
    }

    public Optional<Vendors> getVendorByPan(String panNumber) {
        return vendorsRepository.findByPanNumber(panNumber);
    }

    public Vendors saveVendor(Vendors vendor) {
        return vendorsRepository.save(vendor);
    }

    public void deleteVendor(Long id) {
        vendorsRepository.deleteById(id);
    }

    public boolean existsByEmail(String email) {
        return vendorsRepository.existsByEmail(email);
    }

    public boolean existsByPhone(String phone) {
        return vendorsRepository.existsByPhone(phone);
    }

    public long getVendorCount() {
        return vendorsRepository.count();
    }

    public List<Vendors> getVerifiedVendors() {
        return vendorsRepository.findAll().stream()
                .filter(Vendors::isVerified)
                .toList();
    }

    public List<Vendors> getUnverifiedVendors() {
        return vendorsRepository.findAll().stream()
                .filter(vendor -> !vendor.isVerified())
                .toList();
    }

    public List<Vendors> getVendorsByType(VendorType vendorType) {
        return vendorsRepository.findAll().stream()
                .filter(vendor -> vendor.getVendorType() == vendorType)
                .toList();
    }

    public List<Vendors> getVendorsWithGst() {
        return vendorsRepository.findAll().stream()
                .filter(vendor -> vendor.getGstNumber() != null && !vendor.getGstNumber().isEmpty())
                .toList();
    }

    public List<Vendors> getVendorsWithPan() {
        return vendorsRepository.findAll().stream()
                .filter(vendor -> vendor.getPanNumber() != null && !vendor.getPanNumber().isEmpty())
                .toList();
    }
    
    // City-based methods
    public List<Vendors> getVendorsByCity(String city) {
        return vendorsRepository.findByCityIgnoreCase(city);
    }
    
    public List<Vendors> getVerifiedVendorsByCity(String city) {
        return vendorsRepository.findByCityIgnoreCaseAndVerifiedTrue(city);
    }
    
    public List<Vendors> getVendorsByState(String state) {
        return vendorsRepository.findByStateIgnoreCase(state);
    }
    
    public List<Vendors> getVerifiedVendorsByState(String state) {
        return vendorsRepository.findByStateIgnoreCaseAndVerifiedTrue(state);
    }
    
    public List<String> getAllCities() {
        return vendorsRepository.findAllDistinctCities();
    }
    
    public List<String> getAllStates() {
        return vendorsRepository.findAllDistinctStates();
    }
    
    public Long getVerifiedVendorCountByCity(String city) {
        return vendorsRepository.countVerifiedVendorsByCity(city);
    }
}

