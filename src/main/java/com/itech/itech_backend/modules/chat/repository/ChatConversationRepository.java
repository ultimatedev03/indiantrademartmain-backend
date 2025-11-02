package com.itech.itech_backend.modules.chat.repository;

import com.itech.itech_backend.modules.chat.model.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatConversationRepository extends JpaRepository<ChatConversation, Long> {
    
    Optional<ChatConversation> findByConversationId(String conversationId);
    
    @Query("SELECT cc FROM ChatConversation cc WHERE " +
           "(cc.participant1Id = :userId OR cc.participant2Id = :userId) " +
           "ORDER BY cc.lastMessageTime DESC")
    List<ChatConversation> findConversationsByUserId(@Param("userId") String userId);
    
    @Query("SELECT cc FROM ChatConversation cc WHERE " +
           "((cc.participant1Id = :user1 AND cc.participant2Id = :user2) OR " +
           "(cc.participant1Id = :user2 AND cc.participant2Id = :user1))")
    Optional<ChatConversation> findConversationBetweenUsers(@Param("user1") String user1, 
                                                           @Param("user2") String user2);
    
    @Query("SELECT cc FROM ChatConversation cc WHERE cc.type = :type " +
           "ORDER BY cc.lastMessageTime DESC")
    List<ChatConversation> findByType(@Param("type") ChatConversation.ConversationType type);
    
    @Query("SELECT COUNT(cc) FROM ChatConversation cc WHERE " +
           "((cc.participant1Id = :userId AND cc.unreadCountP1 > 0) OR " +
           "(cc.participant2Id = :userId AND cc.unreadCountP2 > 0))")
    Long countUnreadConversations(@Param("userId") String userId);
}
