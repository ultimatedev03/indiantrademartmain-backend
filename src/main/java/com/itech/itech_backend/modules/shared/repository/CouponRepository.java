package com.itech.itech_backend.modules.shared.repository;

import com.itech.itech_backend.modules.shared.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCode(String code);
    Optional<Coupon> findByCodeAndIsActiveTrue(String code);
}

