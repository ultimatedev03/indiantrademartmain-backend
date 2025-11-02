package com.itech.itech_backend.modules.buyer.repository;

import com.itech.itech_backend.modules.buyer.model.Wishlist;
import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.buyer.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findByUserId(Long userId);
    Page<Wishlist> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    Optional<Wishlist> findByUserIdAndProductId(Long userId, Long productId);
    void deleteByUserIdAndProductId(Long userId, Long productId);
    
    @Query("SELECT COUNT(w) FROM Wishlist w WHERE w.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT w FROM Wishlist w WHERE w.product.vendor.id = :vendorId")
    List<Wishlist> findByVendorId(@Param("vendorId") Long vendorId);
}

