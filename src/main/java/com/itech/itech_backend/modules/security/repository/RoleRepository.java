package com.itech.itech_backend.modules.security.repository;

import com.itech.itech_backend.modules.security.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    Optional<Role> findByName(String name);
    
    List<Role> findByIsActiveTrue();
    
    @Query("SELECT r FROM Role r WHERE r.name IN :roleNames AND r.isActive = true")
    List<Role> findByNameInAndIsActiveTrue(@Param("roleNames") List<String> roleNames);
    
    @Query("SELECT r FROM Role r JOIN r.permissions p WHERE p.name = :permissionName")
    List<Role> findRolesWithPermission(@Param("permissionName") String permissionName);
    
    boolean existsByName(String name);
}
