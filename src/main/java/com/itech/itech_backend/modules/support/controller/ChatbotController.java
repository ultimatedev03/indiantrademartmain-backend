package com.itech.itech_backend.modules.support.controller;

import com.itech.itech_backend.modules.shared.dto.ChatbotRequestDto;
import com.itech.itech_backend.modules.shared.dto.ChatbotResponseDto;
import com.itech.itech_backend.modules.support.model.ChatbotMessage;
import com.itech.itech_backend.modules.support.service.ChatbotService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
@Slf4j
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/chat")
    public ResponseEntity<ChatbotResponseDto> chat(
            @RequestBody ChatbotRequestDto request,
            HttpServletRequest httpRequest) {
        
        try {
            log.info("Received chat request: {} from user role: {}", request.getMessage(), request.getUserRole());
            
            // Set user IP if not provided
            if (request.getUserIp() == null || request.getUserIp().isEmpty()) {
                request.setUserIp(getClientIpAddress(httpRequest));
            }
            
            ChatbotResponseDto response = chatbotService.processMessage(request);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error processing chat request: {}", e.getMessage(), e);
            
            ChatbotResponseDto errorResponse = ChatbotResponseDto.builder()
                    .response("I'm sorry, I'm experiencing some technical difficulties. Please try again later.")
                    .sessionId(request.getSessionId())
                    .hasRecommendations(false)
                    .build();
            
            return ResponseEntity.ok(errorResponse);
        }
    }
    
    // Add support endpoint with role-based routing
    @PostMapping("/support/chat")
    public ResponseEntity<ChatbotResponseDto> supportChat(
            @RequestBody ChatbotRequestDto request,
            HttpServletRequest httpRequest) {
        
        try {
            log.info("Received support chat request: {} from user role: {}", request.getMessage(), request.getUserRole());
            
            // Set user IP if not provided
            if (request.getUserIp() == null || request.getUserIp().isEmpty()) {
                request.setUserIp(getClientIpAddress(httpRequest));
            }
            
            ChatbotResponseDto response = chatbotService.processRoleBasedMessage(request);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error processing support chat request: {}", e.getMessage(), e);
            
            ChatbotResponseDto errorResponse = ChatbotResponseDto.builder()
                    .response("I'm sorry, I'm experiencing some technical difficulties. Please try again later.")
                    .sessionId(request.getSessionId())
                    .hasRecommendations(false)
                    .build();
            
            return ResponseEntity.ok(errorResponse);
        }
    }

    @GetMapping("/history/{sessionId}")
    public ResponseEntity<List<ChatbotMessage>> getChatHistory(@PathVariable String sessionId) {
        try {
            List<ChatbotMessage> history = chatbotService.getChatHistory(sessionId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            log.error("Error fetching chat history: {}", e.getMessage(), e);
            return ResponseEntity.ok(List.of());
        }
    }

    @PostMapping("/start-session")
    public ResponseEntity<ChatbotResponseDto> startSession(HttpServletRequest httpRequest) {
        try {
            ChatbotRequestDto request = ChatbotRequestDto.builder()
                    .message("hello")
                    .sessionId(null) // Will be generated
                    .userIp(getClientIpAddress(httpRequest))
                    .build();
            
            ChatbotResponseDto response = chatbotService.processMessage(request);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error starting chat session: {}", e.getMessage(), e);
            
            ChatbotResponseDto errorResponse = ChatbotResponseDto.builder()
                    .response("Welcome to iTech! I'm here to help you find the best vendors and products.")
                    .sessionId(java.util.UUID.randomUUID().toString())
                    .hasRecommendations(false)
                    .build();
            
            return ResponseEntity.ok(errorResponse);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Chatbot service is running");
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            return xForwardedForHeader.split(",")[0];
        }
    }
}

