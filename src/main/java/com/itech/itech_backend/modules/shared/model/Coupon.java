package com.itech.itech_backend.modules.shared.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String discountType; // PERCENTAGE, FIXED_AMOUNT

    @Column(nullable = false)
    private BigDecimal discountValue;

    private BigDecimal minimumOrderAmount;
    private BigDecimal maximumDiscountAmount;

    @Builder.Default
    private int usageLimit = 0; // 0 means unlimited

    @Builder.Default
    private int usedCount = 0;

    private LocalDateTime validFrom;
    private LocalDateTime validUntil;

    @Builder.Default
    private boolean isActive = true;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

