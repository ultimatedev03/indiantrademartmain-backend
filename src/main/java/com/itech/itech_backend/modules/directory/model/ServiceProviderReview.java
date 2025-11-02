package com.itech.itech_backend.modules.directory.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_provider_reviews")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ServiceProviderReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_provider_id", nullable = false)
    private ServiceProvider serviceProvider;

    @Column(name = "reviewer_name", nullable = false)
    private String reviewerName;

    @Column(name = "reviewer_email")
    private String reviewerEmail;

    @Column(name = "reviewer_phone")
    private String reviewerPhone;

    @Column(name = "rating", nullable = false)
    private Integer rating; // 1-5 stars

    @Column(name = "title")
    private String title;

    @Column(name = "review_text", columnDefinition = "TEXT")
    private String reviewText;

    @Column(name = "service_used")
    private String serviceUsed;

    @Column(name = "project_value")
    private Double projectValue;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;

    @Column(name = "verified")
    private Boolean verified;

    @Column(name = "helpful_count")
    private Integer helpfulCount;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ReviewStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum ReviewStatus {
        PENDING,
        APPROVED,
        REJECTED,
        FLAGGED
    }

    // Constructors
    public ServiceProviderReview() {}

    public ServiceProviderReview(ServiceProvider serviceProvider, String reviewerName, 
                               Integer rating, String reviewText) {
        this.serviceProvider = serviceProvider;
        this.reviewerName = reviewerName;
        this.rating = rating;
        this.reviewText = reviewText;
        this.verified = false;
        this.helpfulCount = 0;
        this.status = ReviewStatus.PENDING;
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

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public String getReviewerEmail() {
        return reviewerEmail;
    }

    public void setReviewerEmail(String reviewerEmail) {
        this.reviewerEmail = reviewerEmail;
    }

    public String getReviewerPhone() {
        return reviewerPhone;
    }

    public void setReviewerPhone(String reviewerPhone) {
        this.reviewerPhone = reviewerPhone;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public String getServiceUsed() {
        return serviceUsed;
    }

    public void setServiceUsed(String serviceUsed) {
        this.serviceUsed = serviceUsed;
    }

    public Double getProjectValue() {
        return projectValue;
    }

    public void setProjectValue(Double projectValue) {
        this.projectValue = projectValue;
    }

    public LocalDateTime getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDateTime completionDate) {
        this.completionDate = completionDate;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Integer getHelpfulCount() {
        return helpfulCount;
    }

    public void setHelpfulCount(Integer helpfulCount) {
        this.helpfulCount = helpfulCount;
    }

    public ReviewStatus getStatus() {
        return status;
    }

    public void setStatus(ReviewStatus status) {
        this.status = status;
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
}
