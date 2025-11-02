package com.itech.itech_backend.modules.shared.repository;

import com.itech.itech_backend.modules.shared.model.ApiLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiLogRepository extends JpaRepository<ApiLog, Long> {
    
    // Analytics methods
    long countByCreatedAtAfter(java.time.LocalDateTime date);

    @Query("SELECT COUNT(a) FROM ApiLog a WHERE a.createdAt >= :date AND a.statusCode >= 200 AND a.statusCode < 300")
    long countSuccessfulSince(@Param("date") java.time.LocalDateTime date);

    @Query("SELECT AVG(a.responseTime) FROM ApiLog a WHERE a.createdAt >= :date")
    Double getAverageResponseTimeSince(@Param("date") java.time.LocalDateTime date);
}

