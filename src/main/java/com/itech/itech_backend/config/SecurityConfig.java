package com.itech.itech_backend.config;

import com.itech.itech_backend.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;
import org.springframework.beans.factory.annotation.Value;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    
    @Value("${ALLOWED_ORIGINS:http://localhost:3000,https://localhost:3000}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("🔧 Configuring Security with Complete CORS Integration");
        
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // === PUBLIC ENDPOINTS (NO AUTH REQUIRED) ===
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(
                    "/actuator/**",
                    "/health/**",
                    "/health",
                    "/api/",
                    "/api"
                ).permitAll()
                
                // === ALL AUTH ENDPOINTS (REGISTRATION & LOGIN) ===
                .requestMatchers(
                    "/auth/**",
                    "/api/auth/**",
                    "/api/v1/auth/**"
                ).permitAll()
                
                // === PUBLIC PRODUCT ENDPOINTS ===
                .requestMatchers(HttpMethod.GET, 
                    "/api/products/**",
                    "/api/v1/products/**",
                    "/products/**"
                ).permitAll()
                
                // === PUBLIC CATEGORY ENDPOINTS ===
                .requestMatchers(HttpMethod.GET,
                    "/api/categories/**", 
                    "/api/v1/categories/**",
                    "/categories/**"
                ).permitAll()
                
                // === VENDOR ENDPOINTS (REQUIRE VENDOR ROLE) ===
                .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("VENDOR")
                .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("VENDOR")
                .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("VENDOR")
                .requestMatchers("/api/products/vendor/**").hasRole("VENDOR")
                .requestMatchers("/api/v1/products/vendor/**").hasRole("VENDOR")
                
                // === ADMIN ENDPOINTS ===
                .requestMatchers("/admin/**", "/api/admin/**", "/api/v1/admin/**").hasRole("ADMIN")
                
                // === ALL OTHER ENDPOINTS ===
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        System.out.println("✅ Security configuration completed with CORS integration");
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        System.out.println("🌐 Configuring CORS for Frontend-Backend Integration");
        System.out.println("🔧 ALLOWED_ORIGINS from environment: " + allowedOrigins);
        
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Parse environment variable for allowed origins
        List<String> origins = new ArrayList<>();
        
        // Add localhost for development
        origins.addAll(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:3001", 
            "http://127.0.0.1:3000",
            "http://127.0.0.1:3001",
            "https://localhost:3000",
            "https://localhost:3001"
        ));
        
        // Add origins from environment variable
        if (allowedOrigins != null && !allowedOrigins.trim().isEmpty()) {
            String[] envOrigins = allowedOrigins.split(",");
            for (String origin : envOrigins) {
                String trimmedOrigin = origin.trim();
                if (!trimmedOrigin.isEmpty()) {
                    origins.add(trimmedOrigin);
                    System.out.println("✅ Added CORS origin: " + trimmedOrigin);
                }
            }
        }
        
        configuration.setAllowedOrigins(origins);
        
        // Allow all HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
        ));
        
        // Allow all headers
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Allow credentials
        configuration.setAllowCredentials(true);
        
        // Cache preflight for 1 hour
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        System.out.println("✅ CORS configured for localhost:3000 with all methods and headers");
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}