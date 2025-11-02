package com.itech.itech_backend.modules.payment.repository;

import com.itech.itech_backend.modules.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByVendorId(Long vendorId);
    Optional<Payment> findByGatewayPaymentId(String gatewayPaymentId);
    Optional<Payment> findByTransactionId(String transactionId);
    List<Payment> findByBuyerId(Long buyerId);
}

