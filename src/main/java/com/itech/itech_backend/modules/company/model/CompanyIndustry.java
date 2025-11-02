package com.itech.itech_backend.modules.company.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "company_industries", indexes = {
    @Index(name = "idx_company_industry", columnList = "company_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyIndustry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Industry industry;

    public enum Industry {
        AGRICULTURE,
        AUTOMOTIVE, 
        CHEMICALS,
        CONSTRUCTION,
        ELECTRONICS,
        ENERGY,
        FOOD_BEVERAGE,
        HEALTHCARE,
        IT,
        MANUFACTURING,
        MINING,
        PHARMACEUTICALS,
        TEXTILES,
        TRANSPORTATION,
        TELECOMMUNICATIONS,
        EDUCATION,
        FINANCE,
        REAL_ESTATE
    }
}
