package com.itech.itech_backend.modules.company.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "company_images", indexes = {
    @Index(name = "idx_company_image", columnList = "company_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "image_type")
    @Builder.Default
    private ImageType imageType = ImageType.OFFICE;

    @Column(name = "alt_text", length = 200)
    private String altText;

    public enum ImageType {
        LOGO, BANNER, OFFICE, PRODUCT, CERTIFICATE
    }
}
