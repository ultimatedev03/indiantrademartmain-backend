package com.itech.itech_backend.modules.core.repository;

import com.itech.itech_backend.modules.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    Optional<User> findByEmailOrPhone(String email, String phone);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    
    // Role-based queries
    List<User> findByRole(String role);
    long countByRole(String role);
    
    // Verification status queries
    List<User> findByIsVerifiedTrue();
    List<User> findByIsVerifiedFalse();
    
    // Active status queries
    List<User> findByIsActiveTrue();
    List<User> findByIsActiveFalse();
    
    // Combined queries
    List<User> findByRoleAndIsVerifiedTrue(String role);
    List<User> findByRoleAndIsActiveTrue(String role);
    
    // Count methods for analytics
    long countByIsActiveTrue();
    long countByIsVerifiedTrue();
    long countByCreatedAtAfter(java.time.LocalDateTime date);
    
    // Additional methods for user management
    @Query("SELECT u FROM User u WHERE u.isActive = true ORDER BY u.createdAt DESC LIMIT :limit")
    List<User> findRecentActiveUsers(@Param("limit") int limit);
}

