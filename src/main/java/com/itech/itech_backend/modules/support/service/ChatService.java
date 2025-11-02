package com.itech.itech_backend.modules.support.service;

import com.itech.itech_backend.modules.shared.dto.ChatMessageDto;
import com.itech.itech_backend.enums.MessageType;
import com.itech.itech_backend.modules.support.model.Chat;
import com.itech.itech_backend.modules.buyer.model.Inquiry;
import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.vendor.model.Vendors;
import com.itech.itech_backend.modules.support.repository.ChatRepository;
import com.itech.itech_backend.modules.buyer.repository.InquiryRepository;
import com.itech.itech_backend.modules.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.itech.itech_backend.modules.support.model.ChatAttachment;
import com.itech.itech_backend.modules.support.repository.ChatAttachmentRepository;
import com.itech.itech_backend.modules.shared.service.FileUploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final InquiryRepository inquiryRepository;
    private final ChatAttachmentRepository chatAttachmentRepository;
    private final FileUploadService fileUploadService;
    private final SimpMessagingTemplate messagingTemplate;

    public Chat sendMessage(Long senderId, Long receiverId, String message, Long inquiryId) {
        Optional<User> sender = userRepository.findById(senderId);
        Optional<User> receiver = userRepository.findById(receiverId);

        if (sender.isEmpty() || receiver.isEmpty()) {
            throw new IllegalArgumentException("Sender or Receiver not found");
        }

        Chat chat = Chat.builder()
                .sender(sender.get())
                .receiver(receiver.get())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();

        if (inquiryId != null) {
            Optional<Inquiry> inquiry = inquiryRepository.findById(inquiryId);
            inquiry.ifPresent(chat::setInquiry);
        }

        Chat savedChat = chatRepository.save(chat);

        // Send real-time message via WebSocket
        messagingTemplate.convertAndSendToUser(
            receiver.get().getEmail(),
            "/queue/messages",
            savedChat
        );

        return savedChat;
    }

    public List<Chat> getChatBetweenUsers(Long userId1, Long userId2) {
        return chatRepository.findChatBetweenUsers(userId1, userId2);
    }

    public List<Chat> getInquiryChat(Long inquiryId) {
        return chatRepository.findByInquiryId(inquiryId);
    }

    public void markMessagesAsRead(Long userId, Long partnerId) {
        List<Chat> unreadMessages = chatRepository.findUnreadMessages(userId);
        for (Chat chat : unreadMessages) {
            if (chat.getSender().getId().equals(partnerId)) {
                chat.setRead(true);
                chat.setReadAt(LocalDateTime.now());
                chatRepository.save(chat);
            }
        }
    }

    public long getUnreadMessageCount(Long userId) {
        return chatRepository.countUnreadMessages(userId);
    }

    public List<User> getChatPartners(Long userId) {
        return chatRepository.findChatPartners(userId);
    }

    @Transactional
    public Chat sendMessage(ChatMessageDto messageDto) {
        try {
            User sender = userRepository.findById(messageDto.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));
            User receiver = userRepository.findById(messageDto.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

            Chat chat = Chat.builder()
                .sender(sender)
                .receiver(receiver)
                .message(messageDto.getMessage())
                .messageType(messageDto.getMessageType() != null ? 
                    MessageType.valueOf(messageDto.getMessageType().toUpperCase()) : 
                    MessageType.TEXT)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

            if (messageDto.getInquiryId() != null) {
                Inquiry inquiry = inquiryRepository.findById(messageDto.getInquiryId())
                    .orElseThrow(() -> new RuntimeException("Inquiry not found"));
                chat.setInquiry(inquiry);
            }

            Chat savedChat = chatRepository.save(chat);

            // Send real-time notification
            sendRealTimeMessage(savedChat);

            log.info("Message sent from user {} to user {}", sender.getId(), receiver.getId());
            return savedChat;

        } catch (Exception e) {
            log.error("Error sending message", e);
            throw new RuntimeException("Failed to send message: " + e.getMessage());
        }
    }

    public Page<Chat> getChatHistory(Long userId1, Long userId2, Pageable pageable) {
        return chatRepository.findChatHistoryBetweenUsers(userId1, userId2, pageable);
    }

    public List<Chat> getRecentChats(Long userId, int limit) {
        return chatRepository.findRecentChatsByUser(userId, limit);
    }

    @Transactional
    public void markConversationAsRead(Long userId, Long partnerId) {
        try {
            List<Chat> unreadMessages = chatRepository.findUnreadMessagesBetweenUsers(userId, partnerId);
            for (Chat chat : unreadMessages) {
                chat.setRead(true);
                chat.setReadAt(LocalDateTime.now());
            }
            chatRepository.saveAll(unreadMessages);
            log.info("Marked {} messages as read between users {} and {}", unreadMessages.size(), userId, partnerId);
        } catch (Exception e) {
            log.error("Error marking messages as read", e);
            throw new RuntimeException("Failed to mark messages as read: " + e.getMessage());
        }
    }

    public Map<String, Object> getChatSummary(Long userId) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("unreadCount", getUnreadMessageCount(userId));
        summary.put("totalChats", chatRepository.countChatsByUser(userId));
        summary.put("recentPartners", getChatPartners(userId));
        return summary;
    }

    public boolean deleteChatMessage(Long messageId, Long userId) {
        try {
            Chat chat = chatRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
            
            // Only sender can delete the message
            if (!chat.getSender().getId().equals(userId)) {
                throw new RuntimeException("Unauthorized to delete this message");
            }
            
            chatRepository.delete(chat);
            log.info("Message {} deleted by user {}", messageId, userId);
            return true;
        } catch (Exception e) {
            log.error("Error deleting message", e);
            throw new RuntimeException("Failed to delete message: " + e.getMessage());
        }
    }

    private void sendRealTimeMessage(Chat chat) {
        try {
            // Send to receiver's personal queue
            messagingTemplate.convertAndSendToUser(
                chat.getReceiver().getEmail(),
                "/queue/messages",
                convertToDto(chat)
            );

            // Also send to inquiry-specific channel if applicable
            if (chat.getInquiry() != null) {
                messagingTemplate.convertAndSend(
                    "/topic/inquiry/" + chat.getInquiry().getId(),
                    convertToDto(chat)
                );
            }
        } catch (Exception e) {
            log.error("Error sending real-time message", e);
        }
    }

    private ChatMessageDto convertToDto(Chat chat) {
        return ChatMessageDto.builder()
            .id(chat.getId())
            .senderId(chat.getSender().getId())
            .senderName(chat.getSender().getName())
            .receiverId(chat.getReceiver().getId())
            .receiverName(chat.getReceiver().getName())
            .message(chat.getMessage())
            .messageTypeEnum(chat.getMessageType())
            .messageType(chat.getMessageType() != null ? chat.getMessageType().name() : null)
            .inquiryId(chat.getInquiry() != null ? chat.getInquiry().getId() : null)
            .isRead(chat.isRead())
            .createdAt(chat.getCreatedAt())
            .readAt(chat.getReadAt())
            .build();
    }

    // Additional methods for ChatController
    @Transactional
    public Chat sendMessageWithAttachments(ChatMessageDto messageDto) {
        // Implementation for sending message with attachments
        throw new RuntimeException("Not implemented yet");
    }

    public List<ChatAttachment> getChatAttachments(Long chatId) {
        // Implementation for getting chat attachments
        return chatAttachmentRepository.findByChatId(chatId);
    }

    public ResponseEntity<byte[]> downloadAttachment(Long attachmentId) {
        // Implementation for downloading attachment
        throw new RuntimeException("Not implemented yet");
    }

    public Chat editMessage(Long messageId, String newMessage, Long userId) {
        // Implementation for editing message
        throw new RuntimeException("Not implemented yet");
    }

    public Chat forwardMessage(Long messageId, Long senderId, Long receiverId) {
        // Implementation for forwarding message
        throw new RuntimeException("Not implemented yet");
    }
}


