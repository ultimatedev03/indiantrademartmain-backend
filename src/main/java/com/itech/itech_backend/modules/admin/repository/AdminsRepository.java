package com.itech.itech_backend.modules.admin.repository;

import com.itech.itech_backend.modules.admin.model.Admins;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminsRepository extends JpaRepository<Admins, Long> {
    Optional<Admins> findByEmail(String email);
    Optional<Admins> findByPhone(String phone);
    Optional<Admins> findByEmailOrPhone(String email, String phone);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    Optional<Admins> findByEmailAndIsActiveTrue(String email);
}

