package com.itech.itech_backend.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.itech.itech_backend.repository")
@EntityScan(basePackages = "com.itech.itech_backend.model")
@EnableTransactionManagement
public class DatabaseConfig {
    // This class relies on Spring Boot's auto-configuration
    // for DataSource and EntityManagerFactory
}
