package com.itech.itech_backend.modules.buyer.controller;

import com.itech.itech_backend.modules.buyer.model.Wishlist;
import com.itech.itech_backend.modules.buyer.service.WishlistService;
import com.itech.itech_backend.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@Slf4j
public class WishlistController {

    private final WishlistService wishlistService;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/add/{productId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addToWishlist(@PathVariable Long productId, HttpServletRequest request) {
        try {
            Long userId = jwtTokenUtil.extractUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body("User not authenticated");
            }
            
            Wishlist addedItem = wishlistService.addToWishlist(userId, productId);
            return ResponseEntity.ok(addedItem);
        } catch (Exception e) {
            log.error("Error adding to wishlist", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Wishlist> addToWishlistDirect(@RequestBody Wishlist wishlist) {
        Wishlist addedItem = wishlistService.addToWishlist(wishlist);
        return ResponseEntity.ok(addedItem);
    }

    @GetMapping("/my-wishlist")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getMyWishlist(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          HttpServletRequest request) {
        try {
            Long userId = jwtTokenUtil.extractUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body("User not authenticated");
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<Wishlist> wishlist = wishlistService.getUserWishlistPaginated(userId, pageable);
            return ResponseEntity.ok(wishlist);
        } catch (Exception e) {
            log.error("Error fetching wishlist", e);
            return ResponseEntity.badRequest().body("Failed to fetch wishlist");
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Wishlist>> getUserWishlist(@PathVariable Long userId) {
        List<Wishlist> wishlist = wishlistService.getUserWishlist(userId);
        return ResponseEntity.ok(wishlist);
    }

    @DeleteMapping("/remove/{productId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> removeFromWishlist(@PathVariable Long productId, HttpServletRequest request) {
        try {
            Long userId = jwtTokenUtil.extractUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body("User not authenticated");
            }
            
            wishlistService.removeFromWishlist(userId, productId);
            return ResponseEntity.ok("Product removed from wishlist");
        } catch (Exception e) {
            log.error("Error removing from wishlist", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeFromWishlistById(@PathVariable Long id) {
        wishlistService.removeFromWishlist(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check/{productId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> checkIfInWishlist(@PathVariable Long productId, HttpServletRequest request) {
        try {
            Long userId = jwtTokenUtil.extractUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body("User not authenticated");
            }
            
            boolean inWishlist = wishlistService.isInWishlist(userId, productId);
            return ResponseEntity.ok(Map.of("inWishlist", inWishlist));
        } catch (Exception e) {
            log.error("Error checking wishlist status", e);
            return ResponseEntity.badRequest().body("Failed to check wishlist status");
        }
    }

    @GetMapping("/count")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getWishlistCount(HttpServletRequest request) {
        try {
            Long userId = jwtTokenUtil.extractUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body("User not authenticated");
            }
            
            Long count = wishlistService.getWishlistCount(userId);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            log.error("Error getting wishlist count", e);
            return ResponseEntity.badRequest().body("Failed to get wishlist count");
        }
    }
}

