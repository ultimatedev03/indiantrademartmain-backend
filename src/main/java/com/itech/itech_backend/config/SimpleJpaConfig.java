package com.itech.itech_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Simple JPA Configuration - relies on Spring Boot auto-configuration
 * This avoids conflicts with multi-datasource setups that might be in disabled files
 */
@Configuration
@EnableTransactionManagement
public class SimpleJpaConfig {
    // Spring Boot handles EntityManagerFactory and TransactionManager automatically
    // No explicit bean definitions needed - let Spring auto-configure everything
}
