package com.itech.itech_backend.modules.shared.repository;

import com.itech.itech_backend.modules.shared.model.SystemMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemMetricsRepository extends JpaRepository<SystemMetrics, Long> {
}

