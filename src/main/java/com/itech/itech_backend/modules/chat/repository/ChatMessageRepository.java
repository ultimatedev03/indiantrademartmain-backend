package com.itech.itech_backend.modules.chat.repository;

import com.itech.itech_backend.modules.chat.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    List<ChatMessage> findByConversationIdOrderByCreatedAtAsc(String conversationId);
    
    List<ChatMessage> findByConversationIdAndCreatedAtAfterOrderByCreatedAtAsc(
            String conversationId, LocalDateTime after);
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.conversationId = :conversationId " +
           "ORDER BY cm.createdAt DESC")
    List<ChatMessage> findRecentMessagesByConversationId(@Param("conversationId") String conversationId);
    
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.conversationId = :conversationId " +
           "AND cm.receiverId = :receiverId AND cm.read = false")
    Long countUnreadMessages(@Param("conversationId") String conversationId, 
                            @Param("receiverId") String receiverId);
    
    List<ChatMessage> findByReceiverIdAndReadFalse(String receiverId);
    
    void deleteByConversationId(String conversationId);
}
