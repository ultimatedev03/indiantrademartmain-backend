package com.itech.itech_backend.modules.admin.service;

import com.itech.itech_backend.modules.admin.model.Admins;
import com.itech.itech_backend.modules.vendor.model.Vendors;
import com.itech.itech_backend.modules.vendor.repository.VendorsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final VendorsRepository vendorsRepository;

    public List<Vendors> getAllVendors() {
        return vendorsRepository.findAll();
    }

    public Vendors updateVendorType(Long vendorId, String vendorType) {
        Vendors vendor = vendorsRepository.findById(vendorId).orElseThrow();
        vendor.setVendorType(Enum.valueOf(com.itech.itech_backend.enums.VendorType.class, vendorType));
        return vendorsRepository.save(vendor);
    }
}

