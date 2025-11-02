package com.itech.itech_backend.modules.vendor.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vendor_categories", indexes = {
    @Index(name = "idx_vendor_cat", columnList = "vendor_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private VendorProfile vendor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    public enum Category {
        MANUFACTURER,
        DISTRIBUTOR,
        WHOLESALER,
        RETAILER,
        SERVICE_PROVIDER,
        IMPORTER,
        EXPORTER,
        TRADER
    }
}
