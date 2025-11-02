package com.itech.itech_backend.modules.buyer.repository;

import com.itech.itech_backend.modules.buyer.model.Product;
import com.itech.itech_backend.modules.buyer.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProduct(Product product);
    List<ProductImage> findByProductId(Long productId);
    void deleteByProduct(Product product);
    void deleteByProductId(Long productId);
}

