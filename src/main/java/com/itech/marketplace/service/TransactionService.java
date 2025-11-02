package com.itech.marketplace.service;

import com.itech.itech_backend.modules.payment.model.Transaction;
import com.itech.itech_backend.modules.payment.model.Refund;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;

// Transaction Service Interface
public interface TransactionService {
    Page<Transaction> getTransactions(String type, String status, Long userId, 
                                    LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    Transaction getTransactionById(Long transactionId);
    boolean isTransactionOwner(Long transactionId, String username);
    
    // Additional methods for RefundService
    void createRefundTransaction(Refund refund);
    void updateRefundTransactionStatus(Refund refund, Transaction.TransactionStatus status);
}

