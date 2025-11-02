package com.itech.itech_backend.modules.support.repository;

import com.itech.itech_backend.modules.support.model.Chat;
import com.itech.itech_backend.modules.core.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    
    @Query("SELECT c FROM Chat c WHERE (c.sender.id = :userId1 AND c.receiver.id = :userId2) OR (c.sender.id = :userId2 AND c.receiver.id = :userId1) ORDER BY c.createdAt ASC")
    List<Chat> findChatBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    
    @Query("SELECT c FROM Chat c WHERE c.inquiry.id = :inquiryId ORDER BY c.createdAt ASC")
    List<Chat> findByInquiryId(@Param("inquiryId") Long inquiryId);
    
    @Query("SELECT c FROM Chat c WHERE c.receiver.id = :userId AND c.isRead = false")
    List<Chat> findUnreadMessages(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(c) FROM Chat c WHERE c.receiver.id = :userId AND c.isRead = false")
    long countUnreadMessages(@Param("userId") Long userId);
    
    @Query("SELECT DISTINCT CASE WHEN c.sender.id = :userId THEN c.receiver ELSE c.sender END FROM Chat c WHERE c.sender.id = :userId OR c.receiver.id = :userId")
    List<User> findChatPartners(@Param("userId") Long userId);
    
    @Query("SELECT c FROM Chat c WHERE (c.sender.id = :userId OR c.receiver.id = :userId) ORDER BY c.createdAt DESC")
    Page<Chat> findUserChats(@Param("userId") Long userId, Pageable pageable);
    
    // Additional methods for chat management
    @Query("SELECT c FROM Chat c WHERE (c.sender.id = :userId1 AND c.receiver.id = :userId2) OR (c.sender.id = :userId2 AND c.receiver.id = :userId1) ORDER BY c.createdAt DESC")
    Page<Chat> findChatHistoryBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2, Pageable pageable);
    
    @Query("SELECT c FROM Chat c WHERE c.sender.id = :userId OR c.receiver.id = :userId ORDER BY c.createdAt DESC LIMIT :limit")
    List<Chat> findRecentChatsByUser(@Param("userId") Long userId, @Param("limit") int limit);
    
    @Query("SELECT c FROM Chat c WHERE (c.sender.id = :userId1 AND c.receiver.id = :userId2) OR (c.sender.id = :userId2 AND c.receiver.id = :userId1) AND c.isRead = false")
    List<Chat> findUnreadMessagesBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    
    @Query("SELECT COUNT(c) FROM Chat c WHERE c.sender.id = :userId OR c.receiver.id = :userId")
    long countChatsByUser(@Param("userId") Long userId);
    
    // Vendor analytics methods
    @Query("SELECT COUNT(c) FROM Chat c WHERE c.receiver.id = :vendorId AND c.isRead = false")
    long countUnreadMessagesByVendorId(@Param("vendorId") Long vendorId);
    
    // Additional methods required by SimpleChatService
    @Query("SELECT c FROM Chat c WHERE (c.sender.id = :userId1 AND c.receiver.id = :userId2) OR (c.sender.id = :userId2 AND c.receiver.id = :userId1) ORDER BY c.createdAt ASC")
    List<Chat> findChatsBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    
    Page<Chat> findByReceiverIdOrderByCreatedAtDesc(Long receiverId, Pageable pageable);
    
    Page<Chat> findBySenderIdOrderByCreatedAtDesc(Long senderId, Pageable pageable);
}

