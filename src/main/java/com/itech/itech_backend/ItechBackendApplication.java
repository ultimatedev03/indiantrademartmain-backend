package com.itech.itech_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ReactiveElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {
    RedisRepositoriesAutoConfiguration.class,
    ElasticsearchDataAutoConfiguration.class,
    ElasticsearchRepositoriesAutoConfiguration.class,
    ReactiveElasticsearchRepositoriesAutoConfiguration.class
})
@EnableJpaRepositories(basePackages = "com.itech.itech_backend.modules")
@EnableAsync
@EnableScheduling
@EntityScan(basePackages = {
    "com.itech.itech_backend.modules.core",
    "com.itech.itech_backend.modules.buyer", 
    "com.itech.itech_backend.modules.vendor",
    "com.itech.itech_backend.modules.product",
    "com.itech.itech_backend.modules.company",
    "com.itech.itech_backend.modules.shared",
    "com.itech.itech_backend.modules.support",
    "com.itech.itech_backend.modules.analytics",
    "com.itech.itech_backend.modules.payment",
    "com.itech.itech_backend.modules.admin",
    "com.itech.itech_backend.modules.directory",
    "com.itech.itech_backend.modules.category",
    "com.itech.itech_backend.modules.city",
    "com.itech.itech_backend.modules.dataentry",
    "com.itech.itech_backend.modules.employee",
    "com.itech.itech_backend.modules.imports",
    "com.itech.itech_backend.modules.order",
    "com.itech.itech_backend.modules.chat",
    "com.itech.itech_backend.modules.rfq",
    "com.itech.itech_backend.modules.security",
    "com.itech.itech_backend.modules.notification"
})
public class ItechBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItechBackendApplication.class, args);
	}

}
