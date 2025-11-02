package com.itech.itech_backend.modules.support.controller;

import com.itech.itech_backend.modules.shared.dto.ChatMessageDto;
import com.itech.itech_backend.modules.support.model.Chat;
import com.itech.itech_backend.modules.support.model.ChatAttachment;
import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.support.service.ChatService;
import com.itech.itech_backend.modules.shared.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = {"http://localhost:3000", "https://your-frontend-domain.com"})
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;
    private final FileUploadService fileUploadService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody ChatMessageDto messageDto) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Chat savedMessage = chatService.sendMessage(
                messageDto.getSenderId(),
                messageDto.getReceiverId(),
                messageDto.getMessage(),
                messageDto.getInquiryId()
            );
            
            response.put("success", true);
            response.put("message", "Message sent successfully");
            response.put("chat", savedMessage);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to send message: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/conversation/{userId1}/{userId2}")
    public ResponseEntity<List<Chat>> getConversation(
            @PathVariable Long userId1,
            @PathVariable Long userId2) {
        List<Chat> messages = chatService.getChatBetweenUsers(userId1, userId2);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/inquiry/{inquiryId}")
    public ResponseEntity<List<Chat>> getInquiryChat(@PathVariable Long inquiryId) {
        List<Chat> messages = chatService.getInquiryChat(inquiryId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/mark-read")
    public ResponseEntity<Map<String, Object>> markMessagesAsRead(
            @RequestParam Long userId,
            @RequestParam Long partnerId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            chatService.markMessagesAsRead(userId, partnerId);
            response.put("success", true);
            response.put("message", "Messages marked as read");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to mark messages as read");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/unread-count/{userId}")
    public ResponseEntity<Map<String, Object>> getUnreadCount(@PathVariable Long userId) {
        long count = chatService.getUnreadMessageCount(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("unreadCount", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/partners/{userId}")
    public ResponseEntity<List<User>> getChatPartners(@PathVariable Long userId) {
        List<User> partners = chatService.getChatPartners(userId);
        return ResponseEntity.ok(partners);
    }

    // Enhanced message sending with DTO
    @PostMapping("/send-v2")
    @PreAuthorize("hasRole('USER') or hasRole('VENDOR')")
    public ResponseEntity<Map<String, Object>> sendMessageV2(@RequestBody ChatMessageDto messageDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            Chat savedMessage = chatService.sendMessage(messageDto);
            response.put("success", true);
            response.put("message", "Message sent successfully");
            response.put("data", savedMessage);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to send message: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Get paginated chat history
    @GetMapping("/history/{userId1}/{userId2}")
    @PreAuthorize("hasRole('USER') or hasRole('VENDOR')")
    public ResponseEntity<Page<Chat>> getChatHistory(
            @PathVariable Long userId1,
            @PathVariable Long userId2,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Chat> chatHistory = chatService.getChatHistory(userId1, userId2, pageable);
        return ResponseEntity.ok(chatHistory);
    }

    // Get recent chats for user
    @GetMapping("/recent/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('VENDOR')")
    public ResponseEntity<List<Chat>> getRecentChats(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        List<Chat> recentChats = chatService.getRecentChats(userId, limit);
        return ResponseEntity.ok(recentChats);
    }

    // Mark conversation as read
    @PostMapping("/conversation/{userId}/{partnerId}/mark-read")
    @PreAuthorize("hasRole('USER') or hasRole('VENDOR')")
    public ResponseEntity<Map<String, Object>> markConversationAsRead(
            @PathVariable Long userId,
            @PathVariable Long partnerId) {
        Map<String, Object> response = new HashMap<>();
        try {
            chatService.markConversationAsRead(userId, partnerId);
            response.put("success", true);
            response.put("message", "Conversation marked as read");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to mark conversation as read: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Get chat summary
    @GetMapping("/summary/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('VENDOR')")
    public ResponseEntity<Map<String, Object>> getChatSummary(@PathVariable Long userId) {
        Map<String, Object> summary = chatService.getChatSummary(userId);
        return ResponseEntity.ok(summary);
    }

    // Delete chat message
    @DeleteMapping("/message/{messageId}")
    @PreAuthorize("hasRole('USER') or hasRole('VENDOR')")
    public ResponseEntity<Map<String, Object>> deleteChatMessage(
            @PathVariable Long messageId,
            @RequestParam Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean deleted = chatService.deleteChatMessage(messageId, userId);
            response.put("success", deleted);
            response.put("message", deleted ? "Message deleted successfully" : "Failed to delete message");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error deleting message: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // WebSocket endpoint for real-time messaging
    @MessageMapping("/chat.send")
    public void sendWebSocketMessage(@Payload ChatMessageDto messageDto) {
        try {
            Chat savedMessage = chatService.sendMessage(messageDto);
            
            // Send to receiver's personal queue
            messagingTemplate.convertAndSendToUser(
                String.valueOf(messageDto.getReceiverId()),
                "/queue/messages",
                savedMessage
            );
        } catch (Exception e) {
            // Handle WebSocket error
            messagingTemplate.convertAndSendToUser(
                String.valueOf(messageDto.getSenderId()),
                "/queue/errors",
                "Failed to send message: " + e.getMessage()
            );
        }
    }

    // WebSocket endpoint for joining chat room
    @MessageMapping("/chat.join")
    public void joinChatRoom(@Payload Map<String, Object> payload) {
        String userId = payload.get("userId").toString();
        String roomId = payload.get("roomId").toString();
        
        messagingTemplate.convertAndSend(
            "/topic/chat/" + roomId,
            "User " + userId + " joined the chat"
        );
    }

    // WebSocket endpoint for leaving chat room
    @MessageMapping("/chat.leave")
    public void leaveChatRoom(@Payload Map<String, Object> payload) {
        String userId = payload.get("userId").toString();
        String roomId = payload.get("roomId").toString();
        
        messagingTemplate.convertAndSend(
            "/topic/chat/" + roomId,
            "User " + userId + " left the chat"
        );
    }

    // Send message with file attachments
    @PostMapping(value = "/send-with-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('VENDOR')")
    public ResponseEntity<Map<String, Object>> sendMessageWithFiles(
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) Long inquiryId,
            @RequestParam(required = false) String threadId,
            @RequestParam(required = false) Long replyToMessageId,
            @RequestParam("files") List<MultipartFile> files) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("Sending message with {} files from user {} to user {}", 
                    files.size(), senderId, receiverId);
            
            ChatMessageDto messageDto = ChatMessageDto.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .message(message)
                .inquiryId(inquiryId)
                .threadId(threadId)
                .replyToMessageId(replyToMessageId)
                .attachments(files)
                .build();
            
            Chat savedMessage = chatService.sendMessageWithAttachments(messageDto);
            
            response.put("success", true);
            response.put("message", "Message with attachments sent successfully");
            response.put("data", savedMessage);
            
            // Send real-time notification
            messagingTemplate.convertAndSendToUser(
                String.valueOf(receiverId),
                "/queue/messages",
                savedMessage
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error sending message with files", e);
            response.put("success", false);
            response.put("message", "Failed to send message with attachments: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Get chat attachments
    @GetMapping("/attachments/{chatId}")
    @PreAuthorize("hasRole('USER') or hasRole('VENDOR')")
    public ResponseEntity<List<ChatAttachment>> getChatAttachments(@PathVariable Long chatId) {
        try {
            List<ChatAttachment> attachments = chatService.getChatAttachments(chatId);
            return ResponseEntity.ok(attachments);
        } catch (Exception e) {
            log.error("Error getting chat attachments", e);
            return ResponseEntity.badRequest().build();
        }
    }

    // Download chat attachment
    @GetMapping("/attachment/{attachmentId}/download")
    @PreAuthorize("hasRole('USER') or hasRole('VENDOR')")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable Long attachmentId) {
        try {
            return chatService.downloadAttachment(attachmentId);
        } catch (Exception e) {
            log.error("Error downloading attachment", e);
            return ResponseEntity.notFound().build();
        }
    }

    // Edit message
    @PutMapping("/message/{messageId}")
    @PreAuthorize("hasRole('USER') or hasRole('VENDOR')")
    public ResponseEntity<Map<String, Object>> editMessage(
            @PathVariable Long messageId,
            @RequestParam String newMessage,
            @RequestParam Long userId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Chat editedMessage = chatService.editMessage(messageId, newMessage, userId);
            
            response.put("success", true);
            response.put("message", "Message edited successfully");
            response.put("data", editedMessage);
            
            // Notify both users about message edit
            messagingTemplate.convertAndSendToUser(
                String.valueOf(editedMessage.getSender().getId()),
                "/queue/message-updates",
                editedMessage
            );
            messagingTemplate.convertAndSendToUser(
                String.valueOf(editedMessage.getReceiver().getId()),
                "/queue/message-updates",
                editedMessage
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error editing message", e);
            response.put("success", false);
            response.put("message", "Failed to edit message: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Forward message
    @PostMapping("/message/{messageId}/forward")
    @PreAuthorize("hasRole('USER') or hasRole('VENDOR')")
    public ResponseEntity<Map<String, Object>> forwardMessage(
            @PathVariable Long messageId,
            @RequestParam Long receiverId,
            @RequestParam Long senderId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Chat forwardedMessage = chatService.forwardMessage(messageId, senderId, receiverId);
            
            response.put("success", true);
            response.put("message", "Message forwarded successfully");
            response.put("data", forwardedMessage);
            
            // Send real-time notification
            messagingTemplate.convertAndSendToUser(
                String.valueOf(receiverId),
                "/queue/messages",
                forwardedMessage
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error forwarding message", e);
            response.put("success", false);
            response.put("message", "Failed to forward message: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Typing indicator
    @PostMapping("/typing")
    @PreAuthorize("hasRole('USER') or hasRole('VENDOR')")
    public ResponseEntity<Void> sendTypingIndicator(
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam boolean isTyping) {
        
        try {
            Map<String, Object> typingData = new HashMap<>();
            typingData.put("senderId", senderId);
            typingData.put("isTyping", isTyping);
            
            messagingTemplate.convertAndSendToUser(
                String.valueOf(receiverId),
                "/queue/typing",
                typingData
            );
            
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            log.error("Error sending typing indicator", e);
            return ResponseEntity.badRequest().build();
        }
    }
}

