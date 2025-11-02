package com.itech.itech_backend.modules.admin.controller;

import com.itech.itech_backend.modules.shared.model.*;
import com.itech.itech_backend.modules.admin.service.ContentManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
public class ContentManagementController {

    private final ContentManagementService contentManagementService;

    // Banner Management
    @GetMapping("/banners")
    public ResponseEntity<List<Banner>> getAllBanners() {
        List<Banner> banners = contentManagementService.getAllBanners();
        return ResponseEntity.ok(banners);
    }

    @PostMapping("/banners")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Banner> createBanner(@RequestBody Banner banner) {
        Banner createdBanner = contentManagementService.createBanner(banner);
        return ResponseEntity.ok(createdBanner);
    }

    @PutMapping("/banners/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Banner> updateBanner(@PathVariable Long id, @RequestBody Banner banner) {
        Banner updatedBanner = contentManagementService.updateBanner(id, banner);
        return ResponseEntity.ok(updatedBanner);
    }

    @DeleteMapping("/banners/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBanner(@PathVariable Long id) {
        contentManagementService.deleteBanner(id);
        return ResponseEntity.noContent().build();
    }

    // SEO Management
    @GetMapping("/seo-keywords")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SeoKeyword>> getAllSeoKeywords() {
        List<SeoKeyword> keywords = contentManagementService.getAllSeoKeywords();
        return ResponseEntity.ok(keywords);
    }

    @PostMapping("/seo-keywords")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SeoKeyword> createSeoKeyword(@RequestBody SeoKeyword keyword) {
        SeoKeyword createdKeyword = contentManagementService.createSeoKeyword(keyword);
        return ResponseEntity.ok(createdKeyword);
    }

    // Campaign Management
    @GetMapping("/campaigns")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Campaign>> getAllCampaigns() {
        List<Campaign> campaigns = contentManagementService.getAllCampaigns();
        return ResponseEntity.ok(campaigns);
    }

    @PostMapping("/campaigns")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Campaign> createCampaign(@RequestBody Campaign campaign) {
        Campaign createdCampaign = contentManagementService.createCampaign(campaign);
        return ResponseEntity.ok(createdCampaign);
    }

    // Coupon Management
    @GetMapping("/coupons")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        List<Coupon> coupons = contentManagementService.getAllCoupons();
        return ResponseEntity.ok(coupons);
    }

    @PostMapping("/coupons")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Coupon> createCoupon(@RequestBody Coupon coupon) {
        Coupon createdCoupon = contentManagementService.createCoupon(coupon);
        return ResponseEntity.ok(createdCoupon);
    }

    @GetMapping("/coupons/validate/{code}")
    public ResponseEntity<Boolean> validateCoupon(@PathVariable String code) {
        boolean isValid = contentManagementService.validateCoupon(code);
        return ResponseEntity.ok(isValid);
    }
}

