package com.itech.itech_backend.modules.buyer.controller;

import com.itech.itech_backend.modules.buyer.model.Review;
import com.itech.itech_backend.modules.vendor.model.VendorReview;
import com.itech.itech_backend.modules.buyer.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/product")
    public ResponseEntity<Review> createProductReview(@RequestBody Review review) {
        Review createdReview = reviewService.createProductReview(review);
        return ResponseEntity.ok(createdReview);
    }

    @PostMapping("/vendor")
    public ResponseEntity<VendorReview> createVendorReview(@RequestBody VendorReview vendorReview) {
        VendorReview createdReview = reviewService.createVendorReview(vendorReview);
        return ResponseEntity.ok(createdReview);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getProductReviews(@PathVariable Long productId) {
        List<Review> reviews = reviewService.getProductReviews(productId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<VendorReview>> getVendorReviews(@PathVariable Long vendorId) {
        List<VendorReview> reviews = reviewService.getVendorReviews(vendorId);
        return ResponseEntity.ok(reviews);
    }
}

