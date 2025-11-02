package com.itech.itech_backend.modules.security.repository;

import com.itech.itech_backend.modules.security.model.TwoFactorAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TwoFactorAuthRepository extends JpaRepository<TwoFactorAuth, Long> {
    
    Optional<TwoFactorAuth> findByUserId(Long userId);
    
    @Query("SELECT tfa FROM TwoFactorAuth tfa WHERE tfa.userId = :userId AND tfa.isEnabled = true")
    Optional<TwoFactorAuth> findEnabledByUserId(@Param("userId") Long userId);
    
    boolean existsByUserId(Long userId);
    
    void deleteByUserId(Long userId);
    
    @Query("SELECT COUNT(tfa) FROM TwoFactorAuth tfa WHERE tfa.isEnabled = true")
    long countEnabledTwoFactorAuth();
}
