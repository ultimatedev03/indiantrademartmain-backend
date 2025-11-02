package com.itech.itech_backend.modules.security.repository;

import com.itech.itech_backend.modules.security.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    
    List<UserRole> findByUserIdAndIsActiveTrue(String userId);
    
    List<UserRole> findByUserId(String userId);
    
    @Query("SELECT ur FROM UserRole ur WHERE ur.userId = :userId AND ur.role.name = :roleName " +
           "AND ur.isActive = true")
    Optional<UserRole> findByUserIdAndRoleName(@Param("userId") String userId, 
                                              @Param("roleName") String roleName);
    
    @Query("SELECT ur FROM UserRole ur WHERE ur.userId = :userId AND ur.isActive = true " +
           "AND (ur.expiresAt IS NULL OR ur.expiresAt > :currentDate)")
    List<UserRole> findActiveRolesByUserId(@Param("userId") String userId, 
                                          @Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT COUNT(ur) FROM UserRole ur WHERE ur.userId = :userId AND ur.isActive = true")
    Long countActiveRolesByUserId(@Param("userId") String userId);
    
    void deleteByUserIdAndRoleId(String userId, Long roleId);
}
