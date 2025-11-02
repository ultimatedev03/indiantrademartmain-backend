package com.itech.itech_backend.modules.shared.service;

import com.itech.itech_backend.modules.shared.model.enums.NotificationType;
import com.itech.itech_backend.modules.shared.model.Notification;
import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.shared.repository.NotificationRepository;
import com.itech.itech_backend.modules.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public Notification createNotification(Long userId, String title, String message, 
                                         NotificationType type, String relatedEntityId) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

            Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .relatedEntityId(relatedEntityId)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

            Notification savedNotification = notificationRepository.save(notification);

            // Send real-time notification via WebSocket
            sendRealTimeNotification(savedNotification);

            log.info("Notification created for user {}: {}", userId, title);
            return savedNotification;

        } catch (Exception e) {
            log.error("Error creating notification", e);
            throw new RuntimeException("Failed to create notification: " + e.getMessage());
        }
    }

    public Page<Notification> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    public long getUnreadNotificationCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public Notification markAsRead(Long notificationId) {
        try {
            Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());

            return notificationRepository.save(notification);

        } catch (Exception e) {
            log.error("Error marking notification as read", e);
            throw new RuntimeException("Failed to mark notification as read: " + e.getMessage());
        }
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        try {
            List<Notification> unreadNotifications = getUnreadNotifications(userId);
            for (Notification notification : unreadNotifications) {
                notification.setRead(true);
                notification.setReadAt(LocalDateTime.now());
            }
            notificationRepository.saveAll(unreadNotifications);

            log.info("Marked all notifications as read for user {}", userId);

        } catch (Exception e) {
            log.error("Error marking all notifications as read", e);
            throw new RuntimeException("Failed to mark all notifications as read: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        try {
            Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

            // Ensure user can only delete their own notifications
            if (!notification.getUser().getId().equals(userId)) {
                throw new RuntimeException("Unauthorized to delete this notification");
            }

            notificationRepository.delete(notification);
            log.info("Notification {} deleted by user {}", notificationId, userId);

        } catch (Exception e) {
            log.error("Error deleting notification", e);
            throw new RuntimeException("Failed to delete notification: " + e.getMessage());
        }
    }

    // Specific notification creation methods
    public void notifyInquiryReceived(Long vendorId, String inquiryId, String productName) {
        createNotification(
            vendorId,
            "New Inquiry Received",
            "You have received a new inquiry for " + productName,
            NotificationType.INQUIRY,
            inquiryId
        );
    }

    public void notifyQuoteReceived(Long userId, String quoteId, String vendorName) {
        createNotification(
            userId,
            "New Quote Received",
            "You have received a new quote from " + vendorName,
            NotificationType.QUOTE,
            quoteId
        );
    }

    public void notifyQuoteAccepted(Long vendorId, String quoteId, String buyerName) {
        createNotification(
            vendorId,
            "Quote Accepted",
            buyerName + " has accepted your quote",
            NotificationType.QUOTE_ACCEPTED,
            quoteId
        );
    }

    public void notifyOrderPlaced(Long vendorId, String orderId, String orderNumber) {
        createNotification(
            vendorId,
            "New Order Placed",
            "New order " + orderNumber + " has been placed",
            NotificationType.ORDER,
            orderId
        );
    }

    public void notifyOrderStatusUpdate(Long userId, String orderId, String status, String orderNumber) {
        createNotification(
            userId,
            "Order Status Updated",
            "Your order " + orderNumber + " status has been updated to " + status,
            NotificationType.ORDER_UPDATE,
            orderId
        );
    }

    public void notifyPaymentReceived(Long vendorId, String paymentId, double amount) {
        createNotification(
            vendorId,
            "Payment Received",
            "Payment of ₹" + amount + " has been received",
            NotificationType.PAYMENT,
            paymentId
        );
    }

    public void notifyKycStatusUpdate(Long vendorId, String kycId, String status) {
        createNotification(
            vendorId,
            "KYC Status Updated",
            "Your KYC verification status has been updated to " + status,
            NotificationType.KYC_UPDATE,
            kycId
        );
    }

    public void notifyNewMessage(Long userId, String chatId, String senderName) {
        createNotification(
            userId,
            "New Message",
            "You have received a new message from " + senderName,
            NotificationType.MESSAGE,
            chatId
        );
    }

    public void notifyTicketUpdate(Long userId, String ticketId, String ticketNumber, String status) {
        createNotification(
            userId,
            "Support Ticket Update",
            "Your support ticket " + ticketNumber + " status has been updated to " + status,
            NotificationType.SUPPORT_TICKET,
            ticketId
        );
    }

    public void notifyProductReview(Long vendorId, String reviewId, String productName, int rating) {
        createNotification(
            vendorId,
            "New Product Review",
            "Your product " + productName + " has received a " + rating + " star review",
            NotificationType.REVIEW,
            reviewId
        );
    }

    public void notifySubscriptionExpiry(Long vendorId, String subscriptionId, int daysLeft) {
        createNotification(
            vendorId,
            "Subscription Expiring Soon",
            "Your subscription will expire in " + daysLeft + " days. Please renew to continue.",
            NotificationType.SUBSCRIPTION,
            subscriptionId
        );
    }

    public void notifySystemMaintenance(Long userId, String maintenanceStart, String maintenanceEnd) {
        createNotification(
            userId,
            "Scheduled Maintenance",
            "System maintenance scheduled from " + maintenanceStart + " to " + maintenanceEnd,
            NotificationType.SYSTEM,
            null
        );
    }

    private void sendRealTimeNotification(Notification notification) {
        try {
            Map<String, Object> notificationData = new HashMap<>();
            notificationData.put("id", notification.getId());
            notificationData.put("title", notification.getTitle());
            notificationData.put("message", notification.getMessage());
            notificationData.put("type", notification.getType());
            notificationData.put("relatedEntityId", notification.getRelatedEntityId());
            notificationData.put("createdAt", notification.getCreatedAt());

            // Send to user's personal notification queue
            messagingTemplate.convertAndSendToUser(
                notification.getUser().getEmail(),
                "/queue/notifications",
                notificationData
            );

            // Also send to general notification topic for the user
            messagingTemplate.convertAndSend(
                "/topic/notifications/" + notification.getUser().getId(),
                notificationData
            );

        } catch (Exception e) {
            log.error("Error sending real-time notification", e);
        }
    }

    public Map<String, Object> getNotificationSummary(Long userId) {
        Map<String, Object> summary = new HashMap<>();
        
        long unreadCount = getUnreadNotificationCount(userId);
        long totalCount = notificationRepository.countByUserId(userId);
        
        // Count by type (unread)
        Map<String, Long> unreadByType = new HashMap<>();
        for (NotificationType type : NotificationType.values()) {
            long count = notificationRepository.countByUserIdAndTypeAndIsReadFalse(userId, type);
            if (count > 0) {
                unreadByType.put(type.name(), count);
            }
        }
        
        summary.put("unreadCount", unreadCount);
        summary.put("totalCount", totalCount);
        summary.put("unreadByType", unreadByType);
        
        return summary;
    }

    public void sendBulkNotification(List<Long> userIds, String title, String message, NotificationType type) {
        try {
            for (Long userId : userIds) {
                createNotification(userId, title, message, type, null);
            }
            log.info("Bulk notification sent to {} users", userIds.size());
        } catch (Exception e) {
            log.error("Error sending bulk notification", e);
            throw new RuntimeException("Failed to send bulk notification: " + e.getMessage());
        }
    }
    
    // Additional methods for disabled services compatibility
    public void notifyVendor(String vendorId, String title, String message) {
        log.info("Vendor notification - ID: {}, Title: {}, Message: {}", vendorId, title, message);
        // Could convert vendorId to userId and send notification if needed
    }
    
    public void notifyBuyer(String buyerId, String title, String message) {
        log.info("Buyer notification - ID: {}, Title: {}, Message: {}", buyerId, title, message);
        // Could convert buyerId to userId and send notification if needed
    }
    
    public void notifyAdmin(String title, String message) {
        log.info("Admin notification - Title: {}, Message: {}", title, message);
        // Could send to all admin users if needed
    }
    
    public void sendRFQNotification(String vendorId, Long rfqId, String message) {
        log.info("Sending RFQ notification to vendor {} for RFQ {}: {}", vendorId, rfqId, message);
        // Implementation for sending notifications
    }
    
    public void sendBidNotification(String buyerId, Long rfqId, String message) {
        log.info("Sending bid notification to buyer {} for RFQ {}: {}", buyerId, rfqId, message);
        // Implementation for sending bid notifications
    }
    
    public void sendEmailNotification(String email, String subject, String message) {
        log.info("Sending email notification to {}: {}", email, subject);
        // Implementation for email notifications
    }
    
    public void sendSMSNotification(String phoneNumber, String message) {
        log.info("Sending SMS notification to {}: {}", phoneNumber, message);
        // Implementation for SMS notifications
    }
}

