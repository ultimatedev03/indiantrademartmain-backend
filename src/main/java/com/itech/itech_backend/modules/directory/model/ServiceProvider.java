package com.itech.itech_backend.modules.directory.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "service_providers")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ServiceProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "business_name", nullable = false)
    private String businessName;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "review_count")
    private Integer reviewCount;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Column(name = "completed_projects")
    private Integer completedProjects;

    @Column(name = "response_time")
    private String responseTime;

    @Column(name = "verified")
    private Boolean verified;

    // Location Information
    @Column(name = "address")
    private String address;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "pincode")
    private String pincode;

    @Column(name = "area")
    private String area;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    // Contact Information
    @Column(name = "mobile", nullable = false)
    private String mobile;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "website")
    private String website;

    @Column(name = "whatsapp")
    private String whatsapp;

    // Services (JSON array stored as string)
    @Column(name = "services", columnDefinition = "TEXT")
    private String services;

    // Business Hours
    @Column(name = "business_hours")
    private String businessHours;

    // Additional Information
    @Column(name = "certifications")
    private String certifications;

    @Column(name = "specializations")
    private String specializations;

    @Column(name = "languages_spoken")
    private String languagesSpoken;

    @Column(name = "service_areas")
    private String serviceAreas;

    @Column(name = "min_project_value")
    private Double minProjectValue;

    @Column(name = "max_project_value")
    private Double maxProjectValue;

    // Status and Verification
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ProviderStatus status;

    @Column(name = "kyc_verified")
    private Boolean kycVerified;

    @Column(name = "gst_number")
    private String gstNumber;

    @Column(name = "pan_number")
    private String panNumber;

    // SEO and Marketing
    @Column(name = "slug")
    private String slug;

    @Column(name = "meta_title")
    private String metaTitle;

    @Column(name = "meta_description")
    private String metaDescription;

    @Column(name = "keywords")
    private String keywords;

    // Analytics
    @Column(name = "profile_views")
    private Long profileViews;

    @Column(name = "contact_requests")
    private Long contactRequests;

    @Column(name = "last_active")
    private LocalDateTime lastActive;

    // Timestamps
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "serviceProvider", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ServiceProviderImage> images;

    @OneToMany(mappedBy = "serviceProvider", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ServiceProviderReview> reviews;

    @OneToMany(mappedBy = "serviceProvider", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ContactInquiry> inquiries;

    // Enums
    public enum ProviderStatus {
        ACTIVE,
        INACTIVE,
        PENDING,
        SUSPENDED,
        REJECTED
    }

    // Constructors
    public ServiceProvider() {}

    public ServiceProvider(String businessName, String ownerName, String category, 
                          String city, String state, String mobile) {
        this.businessName = businessName;
        this.ownerName = ownerName;
        this.category = category;
        this.city = city;
        this.state = state;
        this.mobile = mobile;
        this.rating = 0.0;
        this.reviewCount = 0;
        this.verified = false;
        this.kycVerified = false;
        this.status = ProviderStatus.PENDING;
        this.profileViews = 0L;
        this.contactRequests = 0L;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public Integer getCompletedProjects() {
        return completedProjects;
    }

    public void setCompletedProjects(Integer completedProjects) {
        this.completedProjects = completedProjects;
    }

    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
    }

    public String getBusinessHours() {
        return businessHours;
    }

    public void setBusinessHours(String businessHours) {
        this.businessHours = businessHours;
    }

    public String getCertifications() {
        return certifications;
    }

    public void setCertifications(String certifications) {
        this.certifications = certifications;
    }

    public String getSpecializations() {
        return specializations;
    }

    public void setSpecializations(String specializations) {
        this.specializations = specializations;
    }

    public String getLanguagesSpoken() {
        return languagesSpoken;
    }

    public void setLanguagesSpoken(String languagesSpoken) {
        this.languagesSpoken = languagesSpoken;
    }

    public String getServiceAreas() {
        return serviceAreas;
    }

    public void setServiceAreas(String serviceAreas) {
        this.serviceAreas = serviceAreas;
    }

    public Double getMinProjectValue() {
        return minProjectValue;
    }

    public void setMinProjectValue(Double minProjectValue) {
        this.minProjectValue = minProjectValue;
    }

    public Double getMaxProjectValue() {
        return maxProjectValue;
    }

    public void setMaxProjectValue(Double maxProjectValue) {
        this.maxProjectValue = maxProjectValue;
    }

    public ProviderStatus getStatus() {
        return status;
    }

    public void setStatus(ProviderStatus status) {
        this.status = status;
    }

    public Boolean getKycVerified() {
        return kycVerified;
    }

    public void setKycVerified(Boolean kycVerified) {
        this.kycVerified = kycVerified;
    }

    public String getGstNumber() {
        return gstNumber;
    }

    public void setGstNumber(String gstNumber) {
        this.gstNumber = gstNumber;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public Long getProfileViews() {
        return profileViews;
    }

    public void setProfileViews(Long profileViews) {
        this.profileViews = profileViews;
    }

    public Long getContactRequests() {
        return contactRequests;
    }

    public void setContactRequests(Long contactRequests) {
        this.contactRequests = contactRequests;
    }

    public LocalDateTime getLastActive() {
        return lastActive;
    }

    public void setLastActive(LocalDateTime lastActive) {
        this.lastActive = lastActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<ServiceProviderImage> getImages() {
        return images;
    }

    public void setImages(List<ServiceProviderImage> images) {
        this.images = images;
    }

    public List<ServiceProviderReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<ServiceProviderReview> reviews) {
        this.reviews = reviews;
    }

    public List<ContactInquiry> getInquiries() {
        return inquiries;
    }

    public void setInquiries(List<ContactInquiry> inquiries) {
        this.inquiries = inquiries;
    }

    // Utility methods
    public void incrementProfileViews() {
        this.profileViews = this.profileViews == null ? 1L : this.profileViews + 1;
    }

    public void incrementContactRequests() {
        this.contactRequests = this.contactRequests == null ? 1L : this.contactRequests + 1;
    }

    @Override
    public String toString() {
        return "ServiceProvider{" +
                "id=" + id +
                ", businessName='" + businessName + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", category='" + category + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", verified=" + verified +
                ", status=" + status +
                '}';
    }
}
