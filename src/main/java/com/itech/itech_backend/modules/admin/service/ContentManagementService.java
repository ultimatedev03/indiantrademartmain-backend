package com.itech.itech_backend.modules.admin.service;

import com.itech.itech_backend.modules.shared.model.*;
import com.itech.itech_backend.modules.shared.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentManagementService {

    private final BannerRepository bannerRepository;
    private final SeoKeywordRepository seoKeywordRepository;
    private final CampaignRepository campaignRepository;
    private final CouponRepository couponRepository;

    // Banner Management
    public List<Banner> getAllBanners() {
        return bannerRepository.findAll();
    }

    public List<Banner> getActiveBanners() {
        return bannerRepository.findByIsActiveTrueOrderByDisplayOrder();
    }

    public Banner createBanner(Banner banner) {
        return bannerRepository.save(banner);
    }

    public Banner updateBanner(Long id, Banner banner) {
        banner.setId(id);
        return bannerRepository.save(banner);
    }

    public void deleteBanner(Long id) {
        bannerRepository.deleteById(id);
    }

    // SEO Management
    public List<SeoKeyword> getAllSeoKeywords() {
        return seoKeywordRepository.findAll();
    }

    public SeoKeyword createSeoKeyword(SeoKeyword keyword) {
        return seoKeywordRepository.save(keyword);
    }

    // Campaign Management
    public List<Campaign> getAllCampaigns() {
        return campaignRepository.findAll();
    }

    public Campaign createCampaign(Campaign campaign) {
        return campaignRepository.save(campaign);
    }

    public void updateCampaignStats(Long campaignId, String action) {
        Campaign campaign = campaignRepository.findById(campaignId).orElse(null);
        if (campaign != null) {
            if ("click".equals(action)) {
                campaign.setClickCount(campaign.getClickCount() + 1);
            } else if ("view".equals(action)) {
                campaign.setViewCount(campaign.getViewCount() + 1);
            }
            campaignRepository.save(campaign);
        }
    }

    // Coupon Management
    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    public Coupon createCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    public boolean validateCoupon(String code) {
        Coupon coupon = couponRepository.findByCode(code).orElse(null);
        if (coupon == null || !coupon.isActive()) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        if (coupon.getValidFrom() != null && now.isBefore(coupon.getValidFrom())) {
            return false;
        }
        if (coupon.getValidUntil() != null && now.isAfter(coupon.getValidUntil())) {
            return false;
        }
        if (coupon.getUsageLimit() > 0 && coupon.getUsedCount() >= coupon.getUsageLimit()) {
            return false;
        }

        return true;
    }

    public void useCoupon(String code) {
        Coupon coupon = couponRepository.findByCode(code).orElse(null);
        if (coupon != null && validateCoupon(code)) {
            coupon.setUsedCount(coupon.getUsedCount() + 1);
            couponRepository.save(coupon);
        }
    }
}

