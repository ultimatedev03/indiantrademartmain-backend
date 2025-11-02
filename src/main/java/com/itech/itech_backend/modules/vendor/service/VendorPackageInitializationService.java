package com.itech.itech_backend.modules.vendor.service;

import com.itech.itech_backend.modules.payment.model.Subscription;
import com.itech.itech_backend.modules.vendor.model.VendorPackage;
import com.itech.itech_backend.modules.vendor.model.VendorPackageFeature;
import com.itech.itech_backend.modules.vendor.repository.VendorPackageFeatureRepository;
import com.itech.itech_backend.modules.vendor.repository.VendorPackageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VendorPackageInitializationService implements CommandLineRunner {
    
    private final VendorPackageRepository vendorPackageRepository;
    private final VendorPackageFeatureRepository vendorPackageFeatureRepository;
    
    @Override
    @Transactional
    public void run(String... args) {
        if (vendorPackageRepository.count() == 0) {
            log.info("Initializing default vendor packages...");
            initializeDefaultPackages();
            log.info("Default vendor packages initialized successfully");
        } else {
            log.info("Vendor packages already exist, skipping initialization");
        }
    }
    
    private void initializeDefaultPackages() {
        // Create Silver Package
        VendorPackage silverPackage = createSilverPackage();
        silverPackage = vendorPackageRepository.save(silverPackage);
        createSilverFeatures(silverPackage);
        
        // Create Gold Package
        VendorPackage goldPackage = createGoldPackage();
        goldPackage = vendorPackageRepository.save(goldPackage);
        createGoldFeatures(goldPackage);
        
        // Create Platinum Package
        VendorPackage platinumPackage = createPlatinumPackage();
        platinumPackage = vendorPackageRepository.save(platinumPackage);
        createPlatinumFeatures(platinumPackage);
        
        // Create Diamond Package
        VendorPackage diamondPackage = createDiamondPackage();
        diamondPackage = vendorPackageRepository.save(diamondPackage);
        createDiamondFeatures(diamondPackage);
    }
    
    private VendorPackage createSilverPackage() {
        return VendorPackage.builder()
                .name("SILVER")
                .displayName("Silver Plan")
                .description("Perfect for small businesses getting started with B2B marketplace")
                .price(new BigDecimal("2999"))
                .discountedPrice(new BigDecimal("2499"))
                .durationDays(30)
                .durationType(VendorPackage.DurationType.MONTHLY)
                .planType(Subscription.PlanType.SILVER)
                .badge("STARTER")
                .color("#C0C0C0")
                .icon("silver-star")
                .maxProducts(50)
                .maxLeads(100)
                .maxOrders(200)
                .maxQuotations(50)
                .maxProductImages(5)
                .featuredListing(false)
                .prioritySupport(false)
                .analyticsAccess(false)
                .chatbotPriority(false)
                .customBranding(false)
                .bulkImportExport(true)
                .apiAccess(false)
                .multiLocationSupport(false)
                .inventoryManagement(true)
                .customerInsights(false)
                .marketplaceIntegration(true)
                .socialMediaIntegration(false)
                .gstCompliance(true)
                .invoiceGeneration(true)
                .paymentGateway(false)
                .shippingIntegration(false)
                .returnManagement(false)
                .loyaltyProgram(false)
                .searchRanking(4)
                .storageLimit(1)
                .bandwidthLimit(10)
                .apiCallLimit(1000)
                .setupFee(BigDecimal.ZERO)
                .monthlyPrice(new BigDecimal("2999"))
                .yearlyPrice(new BigDecimal("29990"))
                .trialDays(7)
                .offerText("Save 30% on annual plan")
                .isActive(true)
                .isPopular(false)
                .sortOrder(1)
                .build();
    }
    
    private VendorPackage createGoldPackage() {
        return VendorPackage.builder()
                .name("GOLD")
                .displayName("Gold Plan")
                .description("Ideal for growing businesses with enhanced features and priority support")
                .price(new BigDecimal("5999"))
                .discountedPrice(new BigDecimal("4999"))
                .durationDays(30)
                .durationType(VendorPackage.DurationType.MONTHLY)
                .planType(Subscription.PlanType.GOLD)
                .badge("POPULAR")
                .color("#FFD700")
                .icon("gold-crown")
                .maxProducts(200)
                .maxLeads(500)
                .maxOrders(1000)
                .maxQuotations(200)
                .maxProductImages(10)
                .featuredListing(true)
                .prioritySupport(true)
                .analyticsAccess(true)
                .chatbotPriority(false)
                .customBranding(false)
                .bulkImportExport(true)
                .apiAccess(true)
                .multiLocationSupport(true)
                .inventoryManagement(true)
                .customerInsights(true)
                .marketplaceIntegration(true)
                .socialMediaIntegration(true)
                .gstCompliance(true)
                .invoiceGeneration(true)
                .paymentGateway(true)
                .shippingIntegration(true)
                .returnManagement(true)
                .loyaltyProgram(false)
                .searchRanking(3)
                .storageLimit(5)
                .bandwidthLimit(50)
                .apiCallLimit(5000)
                .setupFee(BigDecimal.ZERO)
                .monthlyPrice(new BigDecimal("5999"))
                .yearlyPrice(new BigDecimal("59990"))
                .trialDays(14)
                .offerText("Most Popular - Save 35% on annual plan")
                .isActive(true)
                .isPopular(true)
                .sortOrder(2)
                .build();
    }
    
    private VendorPackage createPlatinumPackage() {
        return VendorPackage.builder()
                .name("PLATINUM")
                .displayName("Platinum Plan")
                .description("Premium solution for established businesses with advanced analytics and custom branding")
                .price(new BigDecimal("9999"))
                .discountedPrice(new BigDecimal("8499"))
                .durationDays(30)
                .durationType(VendorPackage.DurationType.MONTHLY)
                .planType(Subscription.PlanType.PLATINUM)
                .badge("PREMIUM")
                .color("#E5E4E2")
                .icon("platinum-diamond")
                .maxProducts(1000)
                .maxLeads(2000)
                .maxOrders(5000)
                .maxQuotations(1000)
                .maxProductImages(25)
                .featuredListing(true)
                .prioritySupport(true)
                .analyticsAccess(true)
                .chatbotPriority(true)
                .customBranding(true)
                .bulkImportExport(true)
                .apiAccess(true)
                .multiLocationSupport(true)
                .inventoryManagement(true)
                .customerInsights(true)
                .marketplaceIntegration(true)
                .socialMediaIntegration(true)
                .gstCompliance(true)
                .invoiceGeneration(true)
                .paymentGateway(true)
                .shippingIntegration(true)
                .returnManagement(true)
                .loyaltyProgram(true)
                .searchRanking(2)
                .storageLimit(20)
                .bandwidthLimit(200)
                .apiCallLimit(20000)
                .setupFee(BigDecimal.ZERO)
                .monthlyPrice(new BigDecimal("9999"))
                .yearlyPrice(new BigDecimal("99990"))
                .trialDays(30)
                .offerText("Enterprise Ready - Save 40% on annual plan")
                .isActive(true)
                .isPopular(false)
                .sortOrder(3)
                .build();
    }
    
    private VendorPackage createDiamondPackage() {
        return VendorPackage.builder()
                .name("DIAMOND")
                .displayName("Diamond Plan")
                .description("Ultimate enterprise solution with unlimited features and dedicated account manager")
                .price(new BigDecimal("19999"))
                .discountedPrice(new BigDecimal("16999"))
                .durationDays(30)
                .durationType(VendorPackage.DurationType.MONTHLY)
                .planType(Subscription.PlanType.DIAMOND)
                .badge("ENTERPRISE")
                .color("#B9F2FF")
                .icon("diamond-premium")
                .maxProducts(null) // Unlimited
                .maxLeads(null) // Unlimited
                .maxOrders(null) // Unlimited
                .maxQuotations(null) // Unlimited
                .maxProductImages(null) // Unlimited
                .featuredListing(true)
                .prioritySupport(true)
                .analyticsAccess(true)
                .chatbotPriority(true)
                .customBranding(true)
                .bulkImportExport(true)
                .apiAccess(true)
                .multiLocationSupport(true)
                .inventoryManagement(true)
                .customerInsights(true)
                .marketplaceIntegration(true)
                .socialMediaIntegration(true)
                .gstCompliance(true)
                .invoiceGeneration(true)
                .paymentGateway(true)
                .shippingIntegration(true)
                .returnManagement(true)
                .loyaltyProgram(true)
                .searchRanking(1)
                .storageLimit(null) // Unlimited
                .bandwidthLimit(null) // Unlimited
                .apiCallLimit(null) // Unlimited
                .setupFee(BigDecimal.ZERO)
                .monthlyPrice(new BigDecimal("19999"))
                .yearlyPrice(new BigDecimal("199990"))
                .trialDays(30)
                .offerText("Unlimited Everything - Save 45% on annual plan")
                .isActive(true)
                .isPopular(false)
                .sortOrder(4)
                .build();
    }
    
    private void createSilverFeatures(VendorPackage silverPackage) {
        List<VendorPackageFeature> features = new ArrayList<>();
        
        features.add(createFeature(silverPackage, "Basic Dashboard", "Access to basic vendor dashboard", VendorPackageFeature.FeatureType.CORE, null, true, false, 1));
        features.add(createFeature(silverPackage, "Product Listing", "List up to 50 products", VendorPackageFeature.FeatureType.CORE, "50", true, true, 2));
        features.add(createFeature(silverPackage, "Lead Management", "Manage up to 100 leads", VendorPackageFeature.FeatureType.CORE, "100", true, true, 3));
        features.add(createFeature(silverPackage, "Basic Support", "Email support during business hours", VendorPackageFeature.FeatureType.SUPPORT, null, true, false, 4));
        features.add(createFeature(silverPackage, "GST Compliance", "Generate GST compliant invoices", VendorPackageFeature.FeatureType.BUSINESS, null, true, false, 5));
        features.add(createFeature(silverPackage, "Inventory Management", "Basic inventory tracking", VendorPackageFeature.FeatureType.BUSINESS, null, true, false, 6));
        features.add(createFeature(silverPackage, "Bulk Import/Export", "CSV import/export for products", VendorPackageFeature.FeatureType.TECHNICAL, null, true, false, 7));
        features.add(createFeature(silverPackage, "Marketplace Integration", "Connect with B2B marketplaces", VendorPackageFeature.FeatureType.BUSINESS, null, true, false, 8));
        features.add(createFeature(silverPackage, "1 GB Storage", "Store product images and documents", VendorPackageFeature.FeatureType.LIMIT, "1 GB", true, false, 9));
        features.add(createFeature(silverPackage, "Mobile App Access", "iOS and Android app support", VendorPackageFeature.FeatureType.BENEFIT, null, true, false, 10));
        
        vendorPackageFeatureRepository.saveAll(features);
    }
    
    private void createGoldFeatures(VendorPackage goldPackage) {
        List<VendorPackageFeature> features = new ArrayList<>();
        
        features.add(createFeature(goldPackage, "Advanced Dashboard", "Enhanced analytics dashboard", VendorPackageFeature.FeatureType.CORE, null, true, false, 1));
        features.add(createFeature(goldPackage, "Product Listing", "List up to 200 products", VendorPackageFeature.FeatureType.CORE, "200", true, true, 2));
        features.add(createFeature(goldPackage, "Lead Management", "Manage up to 500 leads", VendorPackageFeature.FeatureType.CORE, "500", true, true, 3));
        features.add(createFeature(goldPackage, "Priority Support", "Priority phone and email support", VendorPackageFeature.FeatureType.SUPPORT, null, true, true, 4));
        features.add(createFeature(goldPackage, "Featured Listings", "Featured product placement", VendorPackageFeature.FeatureType.PREMIUM, null, true, true, 5));
        features.add(createFeature(goldPackage, "Analytics Access", "Detailed business analytics", VendorPackageFeature.FeatureType.PREMIUM, null, true, true, 6));
        features.add(createFeature(goldPackage, "API Access", "RESTful API integration", VendorPackageFeature.FeatureType.TECHNICAL, "5000 calls/month", true, false, 7));
        features.add(createFeature(goldPackage, "Multi-location Support", "Manage multiple business locations", VendorPackageFeature.FeatureType.BUSINESS, null, true, false, 8));
        features.add(createFeature(goldPackage, "Customer Insights", "Advanced customer analytics", VendorPackageFeature.FeatureType.PREMIUM, null, true, false, 9));
        features.add(createFeature(goldPackage, "Social Media Integration", "Connect social media channels", VendorPackageFeature.FeatureType.BUSINESS, null, true, false, 10));
        features.add(createFeature(goldPackage, "Payment Gateway", "Integrated payment processing", VendorPackageFeature.FeatureType.BUSINESS, null, true, false, 11));
        features.add(createFeature(goldPackage, "Shipping Integration", "Connect with shipping partners", VendorPackageFeature.FeatureType.BUSINESS, null, true, false, 12));
        features.add(createFeature(goldPackage, "Return Management", "Handle product returns efficiently", VendorPackageFeature.FeatureType.BUSINESS, null, true, false, 13));
        features.add(createFeature(goldPackage, "5 GB Storage", "Extended storage for media", VendorPackageFeature.FeatureType.LIMIT, "5 GB", true, false, 14));
        features.add(createFeature(goldPackage, "24/7 Customer Portal", "Self-service customer portal", VendorPackageFeature.FeatureType.BENEFIT, null, true, false, 15));
        
        vendorPackageFeatureRepository.saveAll(features);
    }
    
    private void createPlatinumFeatures(VendorPackage platinumPackage) {
        List<VendorPackageFeature> features = new ArrayList<>();
        
        features.add(createFeature(platinumPackage, "Enterprise Dashboard", "Full-featured enterprise dashboard", VendorPackageFeature.FeatureType.CORE, null, true, false, 1));
        features.add(createFeature(platinumPackage, "Product Listing", "List up to 1000 products", VendorPackageFeature.FeatureType.CORE, "1000", true, true, 2));
        features.add(createFeature(platinumPackage, "Lead Management", "Manage up to 2000 leads", VendorPackageFeature.FeatureType.CORE, "2000", true, true, 3));
        features.add(createFeature(platinumPackage, "Dedicated Support", "Dedicated account manager", VendorPackageFeature.FeatureType.SUPPORT, null, true, true, 4));
        features.add(createFeature(platinumPackage, "Custom Branding", "White-label platform with your branding", VendorPackageFeature.FeatureType.PREMIUM, null, true, true, 5));
        features.add(createFeature(platinumPackage, "Advanced Analytics", "AI-powered business insights", VendorPackageFeature.FeatureType.PREMIUM, null, true, true, 6));
        features.add(createFeature(platinumPackage, "Chatbot Priority", "AI chatbot with priority training", VendorPackageFeature.FeatureType.PREMIUM, null, true, false, 7));
        features.add(createFeature(platinumPackage, "API Access", "Extended API with webhooks", VendorPackageFeature.FeatureType.TECHNICAL, "20000 calls/month", true, false, 8));
        features.add(createFeature(platinumPackage, "Loyalty Program", "Customer loyalty and rewards system", VendorPackageFeature.FeatureType.BUSINESS, null, true, false, 9));
        features.add(createFeature(platinumPackage, "Advanced Inventory", "Real-time inventory across channels", VendorPackageFeature.FeatureType.BUSINESS, null, true, false, 10));
        features.add(createFeature(platinumPackage, "Custom Integrations", "Bespoke third-party integrations", VendorPackageFeature.FeatureType.TECHNICAL, null, true, false, 11));
        features.add(createFeature(platinumPackage, "Advanced Reports", "Custom report builder", VendorPackageFeature.FeatureType.PREMIUM, null, true, false, 12));
        features.add(createFeature(platinumPackage, "Priority Ranking", "Enhanced search visibility", VendorPackageFeature.FeatureType.PREMIUM, null, true, false, 13));
        features.add(createFeature(platinumPackage, "20 GB Storage", "Premium storage allocation", VendorPackageFeature.FeatureType.LIMIT, "20 GB", true, false, 14));
        features.add(createFeature(platinumPackage, "Training & Onboarding", "Personal training sessions", VendorPackageFeature.FeatureType.BENEFIT, null, true, false, 15));
        
        vendorPackageFeatureRepository.saveAll(features);
    }
    
    private void createDiamondFeatures(VendorPackage diamondPackage) {
        List<VendorPackageFeature> features = new ArrayList<>();
        
        features.add(createFeature(diamondPackage, "Ultimate Dashboard", "Fully customizable enterprise dashboard", VendorPackageFeature.FeatureType.CORE, null, true, false, 1));
        features.add(createFeature(diamondPackage, "Unlimited Products", "No limit on product listings", VendorPackageFeature.FeatureType.CORE, "Unlimited", true, true, 2));
        features.add(createFeature(diamondPackage, "Unlimited Leads", "No limit on lead management", VendorPackageFeature.FeatureType.CORE, "Unlimited", true, true, 3));
        features.add(createFeature(diamondPackage, "Dedicated Team", "Dedicated success team", VendorPackageFeature.FeatureType.SUPPORT, null, true, true, 4));
        features.add(createFeature(diamondPackage, "Complete White-label", "Fully branded platform", VendorPackageFeature.FeatureType.PREMIUM, null, true, true, 5));
        features.add(createFeature(diamondPackage, "AI-Powered Insights", "Machine learning analytics", VendorPackageFeature.FeatureType.PREMIUM, null, true, true, 6));
        features.add(createFeature(diamondPackage, "Custom Development", "Bespoke feature development", VendorPackageFeature.FeatureType.PREMIUM, null, true, true, 7));
        features.add(createFeature(diamondPackage, "Unlimited API", "No limits on API usage", VendorPackageFeature.FeatureType.TECHNICAL, "Unlimited", true, false, 8));
        features.add(createFeature(diamondPackage, "Enterprise Integrations", "SAP, Oracle, and custom ERP integration", VendorPackageFeature.FeatureType.TECHNICAL, null, true, false, 9));
        features.add(createFeature(diamondPackage, "Global Marketplace", "Multi-country marketplace access", VendorPackageFeature.FeatureType.BUSINESS, null, true, false, 10));
        features.add(createFeature(diamondPackage, "Advanced AI Chatbot", "Custom AI chatbot with NLP", VendorPackageFeature.FeatureType.PREMIUM, null, true, false, 11));
        features.add(createFeature(diamondPackage, "Enterprise Security", "SOC 2 compliance and advanced security", VendorPackageFeature.FeatureType.TECHNICAL, null, true, false, 12));
        features.add(createFeature(diamondPackage, "Top Priority Ranking", "Highest search and visibility priority", VendorPackageFeature.FeatureType.PREMIUM, null, true, false, 13));
        features.add(createFeature(diamondPackage, "Unlimited Storage", "No storage limitations", VendorPackageFeature.FeatureType.LIMIT, "Unlimited", true, false, 14));
        features.add(createFeature(diamondPackage, "24/7 Premium Support", "Round-the-clock premium support", VendorPackageFeature.FeatureType.SUPPORT, null, true, false, 15));
        features.add(createFeature(diamondPackage, "Quarterly Reviews", "Business performance reviews", VendorPackageFeature.FeatureType.BENEFIT, null, true, false, 16));
        features.add(createFeature(diamondPackage, "Custom SLA", "Guaranteed service level agreements", VendorPackageFeature.FeatureType.BENEFIT, null, true, false, 17));
        
        vendorPackageFeatureRepository.saveAll(features);
    }
    
    private VendorPackageFeature createFeature(VendorPackage vendorPackage, String name, String description, 
                                               VendorPackageFeature.FeatureType type, String value, 
                                               boolean included, boolean highlighted, int order) {
        return VendorPackageFeature.builder()
                .vendorPackage(vendorPackage)
                .featureName(name)
                .description(description)
                .featureType(type)
                .value(value)
                .isIncluded(included)
                .isHighlighted(highlighted)
                .displayOrder(order)
                .build();
    }
}
