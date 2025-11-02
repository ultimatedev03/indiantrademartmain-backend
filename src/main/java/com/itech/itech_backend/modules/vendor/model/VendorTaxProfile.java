package com.itech.itech_backend.modules.vendor.model;

import com.itech.itech_backend.modules.vendor.model.Vendors;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorTaxProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "vendor_id", referencedColumnName = "id", nullable = false, unique = true)
    private Vendors vendor;

    private String panNumber;
    private String gstNumber;
    private String legalName;
    private String businessType;
    private String status;
}

