package com.itech.itech_backend.modules.vendor.model;

import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.vendor.model.Vendors;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "vendor_reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendors vendor;

    @Column(nullable = false)
    private int rating; // 1 to 5 stars

    @Column(length = 1000)
    private String comment;

    @Builder.Default
    private boolean isApproved = false;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

