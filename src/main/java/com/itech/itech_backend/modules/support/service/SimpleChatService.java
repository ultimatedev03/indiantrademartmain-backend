package com.itech.itech_backend.modules.support.service;

import com.itech.itech_backend.modules.support.model.Chat;
import com.itech.itech_backend.modules.support.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimpleChatService {

    private final ChatRepository chatRepository;

    public Chat sendMessage(Chat chat) {
        try {
            log.info("Sending message from {} to {}", chat.getSenderId(), chat.getReceiverId());
            
            chat.setCreatedAt(LocalDateTime.now());
            chat.setRead(false);
            
            return chatRepository.save(chat);
            
        } catch (Exception e) {
            log.error("Error sending message", e);
            throw new RuntimeException("Failed to send message");
        }
    }

    public List<Chat> getChatHistory(Long userId1, Long userId2) {
        try {
            return chatRepository.findChatsBetweenUsers(userId1, userId2);
        } catch (Exception e) {
            log.error("Error getting chat history", e);
            return List.of();
        }
    }

    public Page<Chat> getInboxMessages(Long userId, Pageable pageable) {
        try {
            return chatRepository.findByReceiverIdOrderByCreatedAtDesc(userId, pageable);
        } catch (Exception e) {
            log.error("Error getting inbox messages", e);
            return Page.empty();
        }
    }

    public Page<Chat> getSentMessages(Long userId, Pageable pageable) {
        try {
            return chatRepository.findBySenderIdOrderByCreatedAtDesc(userId, pageable);
        } catch (Exception e) {
            log.error("Error getting sent messages", e);
            return Page.empty();
        }
    }

    public void markAsRead(Long messageId) {
        try {
            Chat chat = chatRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
            
            chat.setRead(true);
            chatRepository.save(chat);
            
        } catch (Exception e) {
            log.error("Error marking message as read", e);
        }
    }

    public long getUnreadMessageCount(Long userId) {
        try {
            return chatRepository.countUnreadMessages(userId);
        } catch (Exception e) {
            log.error("Error counting unread messages", e);
            return 0L;
        }
    }
}
