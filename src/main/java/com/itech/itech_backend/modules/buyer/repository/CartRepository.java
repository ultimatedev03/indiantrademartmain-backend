package com.itech.itech_backend.modules.buyer.repository;

import com.itech.itech_backend.modules.buyer.model.Cart;
import com.itech.itech_backend.modules.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    
    Optional<Cart> findByUser(User user);
    
    Optional<Cart> findByUserId(Long userId);
    
    boolean existsByUserId(Long userId);
    
    void deleteByUserId(Long userId);
}

