package com.itech.itech_backend.modules.vendor.service;

import com.itech.itech_backend.modules.vendor.model.Vendors;
import com.itech.itech_backend.modules.vendor.model.VendorRanking;
import com.itech.itech_backend.modules.vendor.repository.VendorRankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VendorRankingService {

    private final VendorRankingRepository rankingRepo;

    public VendorRanking getOrCreateRanking(Vendors vendor) {
        return rankingRepo.findByVendor(vendor)
                .orElseGet(() -> rankingRepo.save(
                        VendorRanking.builder().vendor(vendor).totalLeadsGenerated(0).performanceScore(0).build()));
    }

    public List<VendorRanking> getAllRankings() {
        return rankingRepo.findAll();
    }
}

