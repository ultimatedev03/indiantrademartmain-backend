package com.itech.itech_backend.modules.buyer.service;

import com.itech.itech_backend.modules.shared.dto.ProductDto;
import com.itech.itech_backend.modules.shared.dto.ProductCategoryDto;
import com.itech.itech_backend.modules.buyer.model.*;
import com.itech.itech_backend.modules.vendor.model.*;
import com.itech.itech_backend.modules.buyer.repository.BuyerProductRepository;
import com.itech.itech_backend.modules.buyer.repository.BuyerCategoryRepository;
import com.itech.itech_backend.modules.buyer.repository.SubCategoryRepository;
import com.itech.itech_backend.modules.buyer.repository.MicroCategoryRepository;
import com.itech.itech_backend.modules.core.repository.UserRepository;
import com.itech.itech_backend.modules.vendor.repository.VendorsRepository;
import com.itech.itech_backend.modules.shared.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final BuyerProductRepository productRepo;
    private final BuyerCategoryRepository categoryRepo;
    private final VendorsRepository vendorsRepo;
    private final UserRepository userRepository;
    private final SubCategoryRepository subCategoryRepo;
    private final MicroCategoryRepository microCategoryRepo;
    private final FileUploadService fileUploadService;

    public Product addProduct(ProductDto dto) {
        // Validate required fields
        if (dto.getCategoryId() == null) {
            throw new IllegalArgumentException("Category ID is required");
        }
        if (dto.getVendorId() == null) {
            throw new IllegalArgumentException("Vendor ID is required");
        }

        Category category = categoryRepo.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + dto.getCategoryId()));
        Vendors vendor = vendorsRepo.findById(dto.getVendorId())
                .orElseThrow(() -> new IllegalArgumentException("Vendor not found with ID: " + dto.getVendorId()));

        Product product = Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .originalPrice(dto.getOriginalPrice())
                .category(category)
                .vendor(vendor)
                .stock(dto.getStock())
                .brand(dto.getBrand())
                .model(dto.getModel())
                .sku(dto.getSku())
                .minOrderQuantity(dto.getMinOrderQuantity() != null ? dto.getMinOrderQuantity() : 1)
                .unit(dto.getUnit())
                .specifications(dto.getSpecifications())
                .tags(dto.getTags())
                .gstRate(dto.getGstRate())
                .weight(dto.getWeight() != null ? dto.getWeight().doubleValue() : null)
                .length(dto.getLength() != null ? dto.getLength().doubleValue() : null)
                .width(dto.getWidth() != null ? dto.getWidth().doubleValue() : null)
                .height(dto.getHeight() != null ? dto.getHeight().doubleValue() : null)
                .freeShipping(dto.getFreeShipping() != null ? dto.getFreeShipping() : false)
                .shippingCharge(dto.getShippingCharge() != null ? dto.getShippingCharge().doubleValue() : null)
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .isApproved(true)  // Auto-approve products for now
                .build();

        return productRepo.save(product);
    }

    public List<Product> getProductsByVendor(Long vendorId) {
        Vendors vendor = vendorsRepo.findById(vendorId).orElseThrow();
        return productRepo.findByVendor(vendor);
    }

public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    // Add Data Entry for Categories, Subcategories, Microcategories, Products
    public Product addDataEntry(ProductDto dto) {
        // Validate and find Category
        Category category = categoryRepo.findById(dto.getCategoryId())
            .orElseThrow(() -> new IllegalArgumentException("Category not found"));

    ProductCategoryDto categoryDto = new ProductCategoryDto();
    categoryDto.setCategoryId(dto.getCategoryId());
    categoryDto.setSubCategoryId(dto.getSubCategoryId());
    categoryDto.setMicroCategoryId(dto.getMicroCategoryId());

    SubCategory subCategory = subCategoryRepo.findById(categoryDto.getSubCategoryId())
        .orElseThrow(() -> new IllegalArgumentException("SubCategory not found"));

    MicroCategory microCategory = microCategoryRepo.findById(categoryDto.getMicroCategoryId())
        .orElseThrow(() -> new IllegalArgumentException("MicroCategory not found"));

        Product product = new Product();
        product.setName(dto.getName());
        product.setCategory(category);
product.setDescription(dto.getDescription());

        return productRepo.save(product);
    }

    // Get Filtered Products
@Cacheable("filteredProducts")
    public List<Product> getFilteredProducts(String category, String subCategory, String microCategory, Double minPrice, Double maxPrice, String location) {
        // Optimize filter logic
        return productRepo.findByVariousFilters(category, subCategory, microCategory, minPrice, maxPrice, location);
    }

    // New methods to fix compilation errors
    public Page<Product> getProducts(Pageable pageable, String category, String search, Double minPrice, Double maxPrice, String sortBy, String sortDir) {
        // For now, return a basic implementation
        List<Product> products = productRepo.findAll();
        // Filter by approved and active products
        products = products.stream()
                .filter(product -> product.isApproved() && product.isActive())
                .collect(Collectors.toList());
        
        // Apply filters if provided
        if (category != null && !category.isEmpty()) {
            products = products.stream()
                    .filter(product -> product.getCategory() != null && product.getCategory().getName().equalsIgnoreCase(category))
                    .collect(Collectors.toList());
        }
        
        if (search != null && !search.isEmpty()) {
            products = products.stream()
                    .filter(product -> product.getName().toLowerCase().contains(search.toLowerCase()) || 
                                     (product.getDescription() != null && product.getDescription().toLowerCase().contains(search.toLowerCase())))
                    .collect(Collectors.toList());
        }
        
        if (minPrice != null) {
            products = products.stream()
                    .filter(product -> product.getPrice() >= minPrice)
                    .collect(Collectors.toList());
        }
        
        if (maxPrice != null) {
            products = products.stream()
                    .filter(product -> product.getPrice() <= maxPrice)
                    .collect(Collectors.toList());
        }
        
        // Simple pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), products.size());
        List<Product> pageContent = products.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, products.size());
    }

    public Product getProductById(Long productId) {
        return productRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));
    }

    public void incrementViewCount(Long productId) {
        Product product = getProductById(productId);
        product.setViewCount(product.getViewCount() + 1);
        productRepo.save(product);
    }

    public Page<Product> searchProducts(String query, Pageable pageable) {
        List<Product> products = productRepo.findAll();
        products = products.stream()
                .filter(product -> product.isApproved() && product.isActive())
                .filter(product -> product.getName().toLowerCase().contains(query.toLowerCase()) || 
                                 (product.getDescription() != null && product.getDescription().toLowerCase().contains(query.toLowerCase())))
                .collect(Collectors.toList());
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), products.size());
        List<Product> pageContent = products.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, products.size());
    }

    public Page<Product> getProductsByCategory(Long categoryId, Pageable pageable) {
        List<Product> products = productRepo.findAll();
        products = products.stream()
                .filter(product -> product.isApproved() && product.isActive())
                .filter(product -> product.getCategory() != null && product.getCategory().getId().equals(categoryId))
                .collect(Collectors.toList());
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), products.size());
        List<Product> pageContent = products.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, products.size());
    }

    public Page<Product> getProductsByVendor(Long vendorId, Pageable pageable) {
        List<Product> products = getProductsByVendor(vendorId);
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), products.size());
        List<Product> pageContent = products.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, products.size());
    }

    public List<Product> getFeaturedProducts(int limit) {
        List<Product> products = productRepo.findAll();
        return products.stream()
                .filter(product -> product.isApproved() && product.isActive() && product.isFeatured())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public Product addProduct(Long vendorId, ProductDto dto) {
        dto.setVendorId(vendorId);
        return addProduct(dto);
    }

    public List<String> uploadProductImages(Long productId, Long vendorId, MultipartFile[] images) {
        log.info("Uploading images for product ID: {} by vendor: {}", productId, vendorId);
        
        // Validate product ownership
        Product product = getProductById(productId);
        if (!product.getVendor().getId().equals(vendorId)) {
            throw new IllegalArgumentException("You can only upload images for your own products");
        }
        
        List<String> uploadedImageUrls = new ArrayList<>();
        
        // Process each image
        for (int i = 0; i < images.length; i++) {
            MultipartFile image = images[i];
            log.info("Processing image {}: {} (size: {} bytes)", i + 1, image.getOriginalFilename(), image.getSize());
            
            // Validate image
            if (image.isEmpty()) {
                log.warn("Skipping empty image at index {}", i);
                continue;
            }
            
            // For now, create mock URLs - replace with actual file upload service later
            String imageUrl = String.format("/uploads/products/%d/image_%d_%s", 
                productId, System.currentTimeMillis(), image.getOriginalFilename());
            uploadedImageUrls.add(imageUrl);
            
            log.info("Mock uploaded image {}: {}", i + 1, imageUrl);
        }
        
        // Update product with image URLs (concatenate with existing ones)
        String existingUrls = product.getImageUrls();
        String allUrls = existingUrls != null ? existingUrls + "," + String.join(",", uploadedImageUrls) 
                                             : String.join(",", uploadedImageUrls);
        product.setImageUrls(allUrls);
        productRepo.save(product);
        
        log.info("Successfully processed {} images for product {}", uploadedImageUrls.size(), productId);
        return uploadedImageUrls;
    }
    
    public List<String> updateProductImages(Long productId, Long vendorId, MultipartFile[] images) {
        log.info("Updating images for product ID: {} by vendor: {}", productId, vendorId);
        
        // Validate product ownership
        Product product = getProductById(productId);
        if (!product.getVendor().getId().equals(vendorId)) {
            throw new IllegalArgumentException("You can only update images for your own products");
        }
        
        try {
            // Delete existing images first
            String existingUrls = product.getImageUrls();
            if (existingUrls != null && !existingUrls.trim().isEmpty()) {
                String[] existingImageUrls = existingUrls.split(",");
                for (String imageUrl : existingImageUrls) {
                    if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                        // Remove /uploads/ prefix for deletion
                        String imagePath = imageUrl.trim().replaceFirst("^/uploads/", "");
                        fileUploadService.deleteImageAndThumbnail(imagePath);
                        log.info("Deleted existing image: {}", imagePath);
                    }
                }
            }
            
            // Upload new images
            String subDirectory = "products/" + productId;
            List<String> newImageUrls = fileUploadService.uploadProductImages(images, subDirectory);
            
            // Convert to full URLs with /uploads/ prefix
            List<String> fullImageUrls = newImageUrls.stream()
                .map(url -> "/uploads/" + url)
                .toList();
            
            // Replace all existing images with new ones
            product.setImageUrls(String.join(",", fullImageUrls));
            productRepo.save(product);
            
            log.info("Successfully updated {} images for product {}", fullImageUrls.size(), productId);
            return fullImageUrls;
            
        } catch (Exception e) {
            log.error("Failed to update images for product {}: {}", productId, e.getMessage(), e);
            throw new RuntimeException("Failed to update images: " + e.getMessage(), e);
        }
    }

    public Product updateProduct(Long productId, Long vendorId, ProductDto dto) {
        Product product = getProductById(productId);
        if (!product.getVendor().getId().equals(vendorId)) {
            throw new IllegalArgumentException("You can only update your own products");
        }
        
        // Update product fields
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        
        if (dto.getCategoryId() != null) {
            Category category = categoryRepo.findById(dto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            product.setCategory(category);
        }
        
        return productRepo.save(product);
    }

    public void deleteProduct(Long productId, Long vendorId) {
        Product product = getProductById(productId);
        if (!product.getVendor().getId().equals(vendorId)) {
            throw new IllegalArgumentException("You can only delete your own products");
        }
        
        productRepo.delete(product);
    }

    public Product updateProductStatus(Long productId, Long vendorId, boolean isActive) {
        Product product = getProductById(productId);
        if (!product.getVendor().getId().equals(vendorId)) {
            throw new IllegalArgumentException("You can only update your own products");
        }
        
        product.setActive(isActive);
        return productRepo.save(product);
    }

    public Product approveProduct(Long productId) {
        Product product = getProductById(productId);
        product.setApproved(true);
        return productRepo.save(product);
    }

    public Product setFeaturedStatus(Long productId, boolean featured) {
        Product product = getProductById(productId);
        product.setFeatured(featured);
        return productRepo.save(product);
    }

    public Page<Product> getPendingApprovalProducts(Pageable pageable) {
        List<Product> products = productRepo.findAll();
        products = products.stream()
                .filter(product -> !product.isApproved())
                .collect(Collectors.toList());
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), products.size());
        List<Product> pageContent = products.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, products.size());
    }
}

