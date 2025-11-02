package com.itech.itech_backend.modules.admin.service;

import com.itech.itech_backend.modules.admin.model.SiteConfiguration;
import com.itech.itech_backend.modules.admin.repository.SiteConfigurationRepository;
import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SiteConfigurationService {

    private final SiteConfigurationRepository siteConfigurationRepository;
    private final UserRepository userRepository;

    public List<SiteConfiguration> getAllConfigurations() {
        return siteConfigurationRepository.findByIsActiveTrueOrderByConfigKey();
    }

    public Optional<SiteConfiguration> getConfigurationByKey(String configKey) {
        return siteConfigurationRepository.findByConfigKeyAndIsActiveTrue(configKey);
    }

    public String getConfigValue(String configKey, String defaultValue) {
        return getConfigurationByKey(configKey)
                .map(SiteConfiguration::getConfigValue)
                .orElse(defaultValue);
    }

    public Boolean getBooleanConfigValue(String configKey, Boolean defaultValue) {
        return getConfigurationByKey(configKey)
                .map(SiteConfiguration::getBooleanValue)
                .orElse(defaultValue);
    }

    public Integer getIntegerConfigValue(String configKey, Integer defaultValue) {
        return getConfigurationByKey(configKey)
                .map(SiteConfiguration::getIntegerValue)
                .orElse(defaultValue);
    }

    public SiteConfiguration createOrUpdateConfiguration(String configKey, String configValue, String description, String configType) {
        User currentUser = getCurrentUser();
        
        Optional<SiteConfiguration> existingConfig = siteConfigurationRepository.findByConfigKeyAndIsActiveTrue(configKey);
        
        SiteConfiguration config;
        if (existingConfig.isPresent()) {
            config = existingConfig.get();
            config.setConfigValue(configValue);
            config.setUpdatedBy(currentUser);
            if (description != null) {
                config.setDescription(description);
            }
            if (configType != null) {
                config.setConfigType(configType);
            }
        } else {
            config = SiteConfiguration.builder()
                    .configKey(configKey)
                    .configValue(configValue)
                    .description(description)
                    .configType(configType != null ? configType : "STRING")
                    .updatedBy(currentUser)
                    .build();
        }
        
        return siteConfigurationRepository.save(config);
    }

    public void deleteConfiguration(String configKey) {
        Optional<SiteConfiguration> config = siteConfigurationRepository.findByConfigKeyAndIsActiveTrue(configKey);
        if (config.isPresent()) {
            SiteConfiguration siteConfig = config.get();
            siteConfig.setIsActive(false);
            siteConfig.setUpdatedBy(getCurrentUser());
            siteConfigurationRepository.save(siteConfig);
        }
    }

    public List<SiteConfiguration> searchConfigurations(String searchTerm) {
        return siteConfigurationRepository.searchConfigurations(searchTerm);
    }

    public List<SiteConfiguration> getConfigurationsByType(String configType) {
        return siteConfigurationRepository.findByConfigTypeAndIsActiveTrueOrderByConfigKey(configType);
    }

    public List<String> getDistinctConfigTypes() {
        return siteConfigurationRepository.findDistinctConfigTypes();
    }

    public Map<String, Object> getDashboardStats() {
        long activeConfigurations = siteConfigurationRepository.countActiveConfigurations();
        List<String> configTypes = getDistinctConfigTypes();
        
        return Map.of(
                "activeConfigurations", activeConfigurations,
                "configTypes", configTypes,
                "totalConfigTypes", configTypes.size()
        );
    }

    public void initializeDefaultConfigurations() {
        log.info("Initializing default site configurations...");
        
        // Site branding configurations
        createOrUpdateConfigurationIfNotExists("site.name", "Indian TradeMart", "Site name displayed in header and title", "STRING");
        createOrUpdateConfigurationIfNotExists("site.logo.url", "/assets/images/logo.png", "Main logo URL", "URL");
        createOrUpdateConfigurationIfNotExists("site.favicon.url", "/assets/images/favicon.ico", "Favicon URL", "URL");
        
        // Feature flags
        createOrUpdateConfigurationIfNotExists("feature.registration.enabled", "true", "Enable user registration", "BOOLEAN");
        createOrUpdateConfigurationIfNotExists("feature.vendor.registration.enabled", "true", "Enable vendor registration", "BOOLEAN");
        createOrUpdateConfigurationIfNotExists("feature.maintenance.mode", "false", "Enable maintenance mode", "BOOLEAN");
        
        // Business settings
        createOrUpdateConfigurationIfNotExists("business.currency", "INR", "Default currency", "STRING");
        createOrUpdateConfigurationIfNotExists("business.tax.rate", "18.0", "Default tax rate percentage", "NUMBER");
        createOrUpdateConfigurationIfNotExists("business.min.order.amount", "500", "Minimum order amount", "NUMBER");
        
        // Email settings
        createOrUpdateConfigurationIfNotExists("email.from.address", "noreply@indiantrademart.com", "Default from email address", "EMAIL");
        createOrUpdateConfigurationIfNotExists("email.support.address", "support@indiantrademart.com", "Support email address", "EMAIL");
        
        // Social media links
        createOrUpdateConfigurationIfNotExists("social.facebook.url", "", "Facebook page URL", "URL");
        createOrUpdateConfigurationIfNotExists("social.twitter.url", "", "Twitter profile URL", "URL");
        createOrUpdateConfigurationIfNotExists("social.linkedin.url", "", "LinkedIn company page URL", "URL");
        
        log.info("Default site configurations initialized successfully");
    }

    private void createOrUpdateConfigurationIfNotExists(String configKey, String configValue, String description, String configType) {
        if (!siteConfigurationRepository.existsByConfigKey(configKey)) {
            createOrUpdateConfiguration(configKey, configValue, description, configType);
        }
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }
}
