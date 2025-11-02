package com.itech.itech_backend.modules.buyer.model;

import com.itech.itech_backend.modules.vendor.model.Vendors;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "quotes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendors vendor;

    @ManyToOne
    @JoinColumn(name = "inquiry_id", nullable = false)
    private Inquiry inquiry;

    @Column(nullable = false)
    private String response;

    private BigDecimal price;
    
    private String currency;
    
    private Integer quantity;
    
    private String deliveryTime;
    
    private String paymentTerms;
    
    private String validityPeriod;
    
    private String additionalNotes;

    @Builder.Default
    private boolean isAccepted = false;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

