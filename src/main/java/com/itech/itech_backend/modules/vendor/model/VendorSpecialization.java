package com.itech.itech_backend.modules.vendor.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vendor_specializations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorSpecialization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private VendorProfile vendor;

    @Column(name = "specialization_name", nullable = false, length = 100)
    private String specializationName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Column(name = "is_primary_specialization")
    @Builder.Default
    private Boolean isPrimarySpecialization = false;
}
