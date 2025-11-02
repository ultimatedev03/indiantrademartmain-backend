package com.itech.itech_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        System.out.println("🌐 Adding CORS mappings for complete integration");
        
        registry.addMapping("/**")
                .allowedOrigins(
                    "http://localhost:3000",
                    "http://localhost:3001", 
                    "http://127.0.0.1:3000",
                    "http://127.0.0.1:3001",
                    "https://localhost:3000",
                    "https://localhost:3001",
                    "https://indiantrademart-frontend-env.eba-phxzpszy.ap-south-1.elasticbeanstalk.com",
                    "https://indiantrademart.com",
                    "https://dir.indiantrademart.com",
                    "https://vendor.indiantrademart.com",
                    "https://admin.indiantrademart.com"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
                
        System.out.println("✅ CORS mappings configured for all endpoints");
    }
}