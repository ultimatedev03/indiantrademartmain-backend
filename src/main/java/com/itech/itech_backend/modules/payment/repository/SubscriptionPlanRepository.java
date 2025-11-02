package com.itech.itech_backend.modules.payment.repository;

import com.itech.itech_backend.modules.payment.model.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
}

