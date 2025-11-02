package com.itech.itech_backend.modules.shared.repository;

import com.itech.itech_backend.modules.shared.model.ActivityLog;
import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.admin.model.Admins;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    List<ActivityLog> findByAdminOrderByCreatedAtDesc(Admins admin);

    Page<ActivityLog> findByAdminOrderByCreatedAtDesc(Admins admin, Pageable pageable);

    List<ActivityLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, Long entityId);

    @Query("SELECT al FROM ActivityLog al WHERE al.createdAt BETWEEN :startDate AND :endDate ORDER BY al.createdAt DESC")
    List<ActivityLog> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(al) FROM ActivityLog al WHERE al.admin = :admin AND al.createdAt >= :sinceDate")
    Long countByAdminSince(@Param("admin") Admins admin, @Param("sinceDate") LocalDateTime sinceDate);

    @Query("SELECT al.action, COUNT(al) FROM ActivityLog al GROUP BY al.action ORDER BY COUNT(al) DESC")
    List<Object[]> getActionStatistics();
}

