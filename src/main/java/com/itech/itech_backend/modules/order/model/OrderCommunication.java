package com.itech.itech_backend.modules.order.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_communications", indexes = {
    @Index(name = "idx_order_comm_order", columnList = "order_id"),
    @Index(name = "idx_order_comm_type", columnList = "communication_type"),
    @Index(name = "idx_order_comm_created", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCommunication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "communication_type", nullable = false)
    private CommunicationType communicationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction")
    private Direction direction;

    @Column(name = "sender", length = 100)
    private String sender;

    @Column(name = "recipient", length = 100)
    private String recipient;

    @Column(name = "subject", length = 200)
    private String subject;

    @Column(name = "message", length = 2000)
    private String message;

    @Column(name = "external_reference", length = 100)
    private String externalReference;

    @Column(name = "is_internal")
    private Boolean isInternal = false;

    @Column(name = "priority")
    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.NORMAL;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    public enum CommunicationType {
        EMAIL,
        SMS,
        PHONE_CALL,
        CHAT,
        INTERNAL_NOTE,
        SYSTEM_MESSAGE,
        NOTIFICATION
    }

    public enum Direction {
        INBOUND,
        OUTBOUND
    }

    public enum Priority {
        LOW,
        NORMAL,
        HIGH,
        URGENT
    }
}

