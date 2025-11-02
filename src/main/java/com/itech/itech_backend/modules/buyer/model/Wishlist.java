package com.itech.itech_backend.modules.buyer.model;

import com.itech.itech_backend.modules.buyer.model.Product;
import com.itech.itech_backend.modules.core.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "wishlist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}

