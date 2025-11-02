package com.itech.itech_backend.modules.shared.service;

import com.itech.itech_backend.modules.buyer.model.*;
import com.itech.itech_backend.modules.vendor.model.*;
import com.itech.itech_backend.modules.buyer.repository.*;
import com.itech.itech_backend.modules.vendor.repository.VendorsRepository;
import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataInitializationService implements CommandLineRunner {

    private final BuyerCategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final MicroCategoryRepository microCategoryRepository;
    private final BuyerProductRepository productRepository;
    private final VendorsRepository vendorsRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Initializing sample data...");
        
        // Check if data already exists
        if (categoryRepository.count() > 0) {
            log.info("Data already exists, skipping initialization");
            return;
        }
        
        try {
            initializeSampleData();
            log.info("Sample data initialization completed successfully");
        } catch (Exception e) {
            log.error("Error initializing sample data: {}", e.getMessage(), e);
        }
    }

    private void initializeSampleData() {
        // Create sample vendor first
        Vendors vendor = createSampleVendor();
        
        // Create categories hierarchy
        Category electronics = createCategory("Electronics", "Electronic goods and devices", 1);
        Category clothing = createCategory("Clothing", "Fashion and clothing items", 2);
        Category homeGarden = createCategory("Home & Garden", "Home improvement and garden supplies", 3);

        // Create subcategories
        SubCategory mobilePhones = createSubCategory("Mobile Phones", "Smartphones and mobile devices", 1, electronics);
        SubCategory laptops = createSubCategory("Laptops", "Laptops and notebooks", 2, electronics);
        SubCategory mensClothing = createSubCategory("Men's Clothing", "Clothing for men", 1, clothing);
        SubCategory womensClothing = createSubCategory("Women's Clothing", "Clothing for women", 2, clothing);
        SubCategory furniture = createSubCategory("Furniture", "Home furniture and decor", 1, homeGarden);

        // Create microcategories
        MicroCategory androidPhones = createMicroCategory("Android Phones", "Android-based smartphones", 1, mobilePhones);
        MicroCategory iphones = createMicroCategory("iPhone", "Apple iPhone series", 2, mobilePhones);
        MicroCategory gamingLaptops = createMicroCategory("Gaming Laptops", "High-performance gaming laptops", 1, laptops);
        MicroCategory businessLaptops = createMicroCategory("Business Laptops", "Professional laptops for business", 2, laptops);
        MicroCategory tshirts = createMicroCategory("T-Shirts", "Men's t-shirts and casual wear", 1, mensClothing);
        MicroCategory dresses = createMicroCategory("Dresses", "Women's dresses and formal wear", 1, womensClothing);
        MicroCategory sofas = createMicroCategory("Sofas", "Living room sofas and seating", 1, furniture);

        // Create sample products
        createProduct("Samsung Galaxy S24", "Latest Samsung Galaxy smartphone with advanced features", 
                     75000.0, 80000.0, "Samsung", "Galaxy S24", androidPhones, vendor);
        
        createProduct("iPhone 15 Pro", "Apple iPhone 15 Pro with titanium design", 
                     134900.0, 139900.0, "Apple", "iPhone 15 Pro", iphones, vendor);
        
        createProduct("ASUS ROG Strix G15", "High-performance gaming laptop with RTX graphics", 
                     89999.0, 94999.0, "ASUS", "ROG Strix G15", gamingLaptops, vendor);
        
        createProduct("ThinkPad X1 Carbon", "Business laptop with premium build quality", 
                     125000.0, 130000.0, "Lenovo", "ThinkPad X1 Carbon", businessLaptops, vendor);
        
        createProduct("Casual Cotton T-Shirt", "Comfortable cotton t-shirt for everyday wear", 
                     599.0, 799.0, "FashionBrand", "Classic Tee", tshirts, vendor);
    }

    private Vendors createSampleVendor() {
        if (vendorsRepository.findByEmail("vendor@example.com").isPresent()) {
            return vendorsRepository.findByEmail("vendor@example.com").get();
        }
        
        // First create a User
        User user = User.builder()
                .name("Sample Vendor")
                .email("vendor@example.com")
                .phone("+91-9876543210")
                .password("$2a$10$encrypted_password_here")
                .isVerified(true)
                .role(User.UserRole.SELLER)
                .isActive(true)
                .address("123 Main Street")
                .city("Mumbai")
                .state("Maharashtra")
                .pincode("400001")
                .country("India")
                .createdAt(LocalDateTime.now())
                .build();
        
        user = userRepository.save(user);
        
        // Then create a Vendor linked to the User
        Vendors vendor = Vendors.builder()
                .user(user)
                .name("Sample Vendor")
                .email("vendor@example.com")
                .phone("+91-9876543210")
                .password("$2a$10$encrypted_password_here")
                .verified(true)
                .role("SELLER")
                .businessName("Sample Electronics Store")
                .businessAddress("123 Main Street")
                .city("Mumbai")
                .state("Maharashtra")
                .pincode("400001")
                .createdAt(LocalDateTime.now())
                .build();
        
        return vendorsRepository.save(vendor);
    }

    private Category createCategory(String name, String description, int displayOrder) {
        Category category = Category.builder()
                .name(name)
                .description(description)
                .displayOrder(displayOrder)
                .isActive(true)
                .metaTitle(name + " - Buy Latest " + name)
                .metaDescription("Shop for latest " + description.toLowerCase())
                .slug(name.toLowerCase().replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", "-"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        return categoryRepository.save(category);
    }

    private SubCategory createSubCategory(String name, String description, int displayOrder, Category category) {
        SubCategory subCategory = SubCategory.builder()
                .name(name)
                .description(description)
                .displayOrder(displayOrder)
                .isActive(true)
                .metaTitle(name + " - Latest " + name)
                .metaDescription("Discover latest " + description.toLowerCase())
                .slug(name.toLowerCase().replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", "-"))
                .category(category)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        return subCategoryRepository.save(subCategory);
    }

    private MicroCategory createMicroCategory(String name, String description, int displayOrder, SubCategory subCategory) {
        MicroCategory microCategory = MicroCategory.builder()
                .name(name)
                .description(description)
                .displayOrder(displayOrder)
                .isActive(true)
                .metaTitle(name + " - Premium " + name)
                .metaDescription("Shop for premium " + description.toLowerCase())
                .slug(name.toLowerCase().replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", "-"))
                .subCategory(subCategory)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        return microCategoryRepository.save(microCategory);
    }

    private Product createProduct(String name, String description, double price, double originalPrice, 
                                String brand, String model, MicroCategory microCategory, Vendors vendor) {
        Product product = Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .originalPrice(originalPrice)
                .brand(brand)
                .model(model)
                .sku(brand.substring(0, 3).toUpperCase() + "-" + model.replaceAll("\\s", "") + "-001")
                .stock(50)
                .minOrderQuantity(1)
                .unit("piece")
                .imageUrls(name.toLowerCase().replaceAll("\\s", "-") + "-1.jpg," + 
                          name.toLowerCase().replaceAll("\\s", "-") + "-2.jpg")
                .specifications("{\"brand\": \"" + brand + "\", \"model\": \"" + model + "\"}")
                .metaTitle(name + " - Buy " + name)
                .metaDescription("Buy " + name + " with " + description.toLowerCase())
                .tags(brand.toLowerCase() + "," + model.toLowerCase())
                .isActive(true)
                .isApproved(true)
                .isFeatured(price > 50000)
                .microCategory(microCategory)
                .vendor(vendor)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        return productRepository.save(product);
    }
}

