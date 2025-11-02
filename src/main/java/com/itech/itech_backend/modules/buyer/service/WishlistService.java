package com.itech.itech_backend.modules.buyer.service;

import com.itech.itech_backend.modules.buyer.model.Wishlist;
import com.itech.itech_backend.modules.buyer.model.Product;
import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.buyer.repository.WishlistRepository;
import com.itech.itech_backend.modules.buyer.repository.BuyerProductRepository;
import com.itech.itech_backend.modules.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final BuyerProductRepository productRepository;
    private final UserRepository userRepository;

    public Wishlist addToWishlist(Long userId, Long productId) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
            
            if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
                throw new RuntimeException("Product already in wishlist");
            }

            Wishlist wishlist = Wishlist.builder()
                .user(user)
                .product(product)
                .createdAt(LocalDateTime.now())
                .build();
            
            return wishlistRepository.save(wishlist);
        } catch (Exception e) {
            log.error("Error adding to wishlist", e);
            throw new RuntimeException("Failed to add to wishlist: " + e.getMessage());
        }
    }

    public Wishlist addToWishlist(Wishlist wishlist) {
        return wishlistRepository.save(wishlist);
    }

    @Transactional
    public void removeFromWishlist(Long userId, Long productId) {
        try {
            Wishlist wishlist = wishlistRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new RuntimeException("Wishlist entry not found"));
            wishlistRepository.delete(wishlist);
        } catch (Exception e) {
            log.error("Error removing from wishlist", e);
            throw new RuntimeException("Failed to remove from wishlist: " + e.getMessage());
        }
    }

    public List<Wishlist> getUserWishlist(Long userId) {
        return wishlistRepository.findByUserId(userId);
    }

    public Page<Wishlist> getUserWishlistPaginated(Long userId, Pageable pageable) {
        return wishlistRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public void removeFromWishlist(Long id) {
        wishlistRepository.deleteById(id);
    }

    public boolean isInWishlist(Long userId, Long productId) {
        return wishlistRepository.existsByUserIdAndProductId(userId, productId);
    }

    public Long getWishlistCount(Long userId) {
        return wishlistRepository.countByUserId(userId);
    }

    public List<Wishlist> getWishlistByVendor(Long vendorId) {
        return wishlistRepository.findByVendorId(vendorId);
    }
}

