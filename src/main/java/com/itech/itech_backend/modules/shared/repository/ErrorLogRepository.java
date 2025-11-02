package com.itech.itech_backend.modules.shared.repository;

import com.itech.itech_backend.modules.shared.model.ErrorLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long> {

    Page<ErrorLog> findByOrderByCreatedAtDesc(Pageable pageable);

    List<ErrorLog> findByErrorTypeOrderByCreatedAtDesc(String errorType);

    @Query("SELECT el FROM ErrorLog el WHERE el.createdAt BETWEEN :startDate AND :endDate ORDER BY el.createdAt DESC")
    List<ErrorLog> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(el) FROM ErrorLog el WHERE el.createdAt >= :sinceDate")
    Long countSince(@Param("sinceDate") LocalDateTime sinceDate);

    @Query("SELECT el.errorType, COUNT(el) FROM ErrorLog el GROUP BY el.errorType ORDER BY COUNT(el) DESC")
    List<Object[]> getErrorTypeStatistics();

    @Query("SELECT el.endpoint, COUNT(el) FROM ErrorLog el WHERE el.endpoint IS NOT NULL GROUP BY el.endpoint ORDER BY COUNT(el) DESC")
    List<Object[]> getEndpointErrorStatistics();
}

