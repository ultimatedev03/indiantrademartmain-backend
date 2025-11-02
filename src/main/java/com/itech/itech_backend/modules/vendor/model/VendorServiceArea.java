package com.itech.itech_backend.modules.vendor.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vendor_service_areas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorServiceArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private VendorProfile vendor;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "pincode", length = 10)
    private String pincode;

    @Column(name = "service_radius_km")
    private Integer serviceRadiusKm;

    @Column(name = "delivery_available")
    @Builder.Default
    private Boolean deliveryAvailable = false;

    @Column(name = "installation_available")
    @Builder.Default
    private Boolean installationAvailable = false;
}
