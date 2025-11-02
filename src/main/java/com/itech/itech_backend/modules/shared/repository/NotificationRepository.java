package com.itech.itech_backend.modules.shared.repository;

import com.itech.itech_backend.modules.shared.model.Notification;
import com.itech.itech_backend.modules.shared.model.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdAndIsReadFalse(Long userId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
    long countUnreadNotifications(Long userId);

    List<Notification> findByUserId(Long userId);

    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotifications(Long userId);
    
    // Additional methods for notification management
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);
    long countByUserIdAndIsReadFalse(Long userId);
    long countByUserId(Long userId);
    long countByUserIdAndTypeAndIsReadFalse(@Param("userId") Long userId, @Param("type") NotificationType type);

}

