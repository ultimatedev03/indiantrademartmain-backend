package com.itech.itech_backend.modules.directory.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_provider_images")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ServiceProviderImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_provider_id", nullable = false)
    private ServiceProvider serviceProvider;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "image_type")
    @Enumerated(EnumType.STRING)
    private ImageType imageType;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "is_primary")
    private Boolean isPrimary;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum ImageType {
        PROFILE,
        GALLERY,
        WORK_SAMPLE,
        CERTIFICATE,
        PROJECT
    }

    // Constructors
    public ServiceProviderImage() {}

    public ServiceProviderImage(ServiceProvider serviceProvider, String imageUrl, ImageType imageType) {
        this.serviceProvider = serviceProvider;
        this.imageUrl = imageUrl;
        this.imageType = imageType;
        this.isPrimary = false;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ImageType getImageType() {
        return imageType;
    }

    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
