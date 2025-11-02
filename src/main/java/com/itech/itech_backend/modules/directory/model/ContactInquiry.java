package com.itech.itech_backend.modules.directory.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "contact_inquiries")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ContactInquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_provider_id", nullable = false)
    private ServiceProvider serviceProvider;

    // Contact Information
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "mobile", nullable = false)
    private String mobile;

    @Column(name = "email")
    private String email;

    @Column(name = "company")
    private String company;

    // Inquiry Details
    @Column(name = "service_required")
    private String serviceRequired;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "location")
    private String location;

    @Column(name = "timeline")
    private String timeline;

    @Column(name = "budget")
    private String budget;

    // Status and Tracking
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private InquiryStatus status;

    @Column(name = "priority")
    @Enumerated(EnumType.STRING)
    private InquiryPriority priority;

    @Column(name = "source")
    private String source;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    // Response Tracking
    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "response_message", columnDefinition = "TEXT")
    private String responseMessage;

    @Column(name = "follow_up_date")
    private LocalDateTime followUpDate;

    // Additional Fields
    @Column(name = "urgent")
    private Boolean urgent;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    // Timestamps
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Enums
    public enum InquiryStatus {
        NEW,
        CONTACTED,
        IN_PROGRESS,
        QUOTED,
        CONVERTED,
        CLOSED,
        REJECTED
    }

    public enum InquiryPriority {
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }

    // Constructors
    public ContactInquiry() {}

    public ContactInquiry(ServiceProvider serviceProvider, String name, String mobile, 
                         String serviceRequired, String message) {
        this.serviceProvider = serviceProvider;
        this.name = name;
        this.mobile = mobile;
        this.serviceRequired = serviceRequired;
        this.message = message;
        this.status = InquiryStatus.NEW;
        this.priority = InquiryPriority.MEDIUM;
        this.urgent = false;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getServiceRequired() {
        return serviceRequired;
    }

    public void setServiceRequired(String serviceRequired) {
        this.serviceRequired = serviceRequired;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTimeline() {
        return timeline;
    }

    public void setTimeline(String timeline) {
        this.timeline = timeline;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public InquiryStatus getStatus() {
        return status;
    }

    public void setStatus(InquiryStatus status) {
        this.status = status;
    }

    public InquiryPriority getPriority() {
        return priority;
    }

    public void setPriority(InquiryPriority priority) {
        this.priority = priority;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public LocalDateTime getFollowUpDate() {
        return followUpDate;
    }

    public void setFollowUpDate(LocalDateTime followUpDate) {
        this.followUpDate = followUpDate;
    }

    public Boolean getUrgent() {
        return urgent;
    }

    public void setUrgent(Boolean urgent) {
        this.urgent = urgent;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
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

    @Override
    public String toString() {
        return "ContactInquiry{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", mobile='" + mobile + '\'' +
                ", serviceRequired='" + serviceRequired + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", createdAt=" + createdAt +
                '}';
    }
}
