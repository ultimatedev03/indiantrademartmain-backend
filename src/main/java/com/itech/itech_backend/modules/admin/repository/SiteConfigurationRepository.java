package com.itech.itech_backend.modules.admin.repository;

import com.itech.itech_backend.modules.admin.model.SiteConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SiteConfigurationRepository extends JpaRepository<SiteConfiguration, Long> {

    Optional<SiteConfiguration> findByConfigKeyAndIsActiveTrue(String configKey);

    List<SiteConfiguration> findByIsActiveTrueOrderByConfigKey();

    List<SiteConfiguration> findByConfigTypeAndIsActiveTrueOrderByConfigKey(String configType);

    @Query("SELECT s FROM SiteConfiguration s WHERE " +
           "LOWER(s.configKey) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "AND s.isActive = true ORDER BY s.configKey")
    List<SiteConfiguration> searchConfigurations(@Param("searchTerm") String searchTerm);

    boolean existsByConfigKey(String configKey);

    @Query("SELECT DISTINCT s.configType FROM SiteConfiguration s WHERE s.isActive = true ORDER BY s.configType")
    List<String> findDistinctConfigTypes();

    @Query("SELECT COUNT(s) FROM SiteConfiguration s WHERE s.isActive = true")
    long countActiveConfigurations();

    @Query("SELECT s FROM SiteConfiguration s WHERE s.configKey LIKE :prefix% AND s.isActive = true ORDER BY s.configKey")
    List<SiteConfiguration> findByConfigKeyPrefix(@Param("prefix") String prefix);
}
