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
public class VendorRanking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Vendors vendor;

    private int totalLeadsGenerated;
    private double performanceScore;
}

