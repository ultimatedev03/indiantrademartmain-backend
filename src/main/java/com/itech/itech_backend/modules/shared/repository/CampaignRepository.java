package com.itech.itech_backend.modules.shared.repository;

import com.itech.itech_backend.modules.shared.model.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    List<Campaign> findByStatus(String status);
    List<Campaign> findByType(String type);
}

