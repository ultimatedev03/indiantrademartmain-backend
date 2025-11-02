package com.itech.itech_backend.modules.shared.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.itech.itech_backend.modules.buyer.model.Product;
import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.buyer.repository.BuyerProductRepository;
import com.itech.itech_backend.modules.buyer.repository.OrderRepository;
import com.itech.itech_backend.modules.buyer.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAiService {

    @Value("${openai.api.key:}")
    private String apiKey;

    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;
    
    @Autowired
    private BuyerProductRepository productRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;

    /**
     * Generate role-specific responses for Indian Trade Mart platform
     */
    public String generateRoleBasedResponse(String userMessage, String userRole) {
        String systemPrompt = buildSystemPrompt(userRole);
        return generateResponse(userMessage, systemPrompt);
    }

    /**
     * Generate general response
     */
    public String generateResponse(String userMessage) {
        return generateResponse(userMessage, buildSystemPrompt("NON_LOGGED"));
    }

    /**
     * Core method to generate responses using OpenAI
     */
    private String generateResponse(String userMessage, String systemPrompt) {
        // Check if OpenAI is configured
        if (apiKey == null || apiKey.trim().isEmpty() || apiKey.contains("your-openai-api-key")) {
            log.warn("OpenAI API key not configured, using fallback responses");
            return generateFallbackResponse(userMessage, systemPrompt);
        }

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));
        messages.add(Map.of("role", "user", "content", userMessage));

        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 500);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            log.debug("Sending request to OpenAI: {}", userMessage);
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);
            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            if (responseBody != null) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    if (message != null) {
                        String aiResponse = (String) message.get("content");
                        log.debug("OpenAI response received: {}", aiResponse);
                        return aiResponse;
                    }
                }
            }
        } catch (Exception e) {
            log.error("OpenAI API error: {}", e.getMessage());
        }

        return generateFallbackResponse(userMessage, systemPrompt);
    }

    /**
     * Build role-specific system prompts for different user types
     */
    private String buildSystemPrompt(String userRole) {
        String basePrompt = "You are iTech Assistant, a helpful AI chatbot for Indian Trade Mart (iTech), India's leading B2B technology marketplace. \n\n" +
                "PLATFORM INFORMATION:\n" +
                "- Indian Trade Mart connects buyers and suppliers across various industries\n" +
                "- We offer a comprehensive B2B marketplace for technology products and services\n" +
                "- Key categories: Electronics, Machinery, Medical Equipment, IT Hardware, Software, Industrial Equipment\n" +
                "- Services: Product listings, lead generation, order management, vendor verification\n" +
                "- Vendor packages: Basic, Silver, Gold, Platinum, Diamond (with increasing benefits)\n" +
                "- Features: Advanced search, bulk orders, custom quotes, secure payments\n\n";

        switch (userRole.toUpperCase()) {
            case "VENDOR":
                return basePrompt + 
                       "USER CONTEXT: You are assisting a VENDOR on our platform.\n\n" +
                       "VENDOR-SPECIFIC GUIDANCE:\n" +
                       "- Help with product listing, inventory management, order processing\n" +
                       "- Explain vendor packages (Basic to Diamond) and their benefits\n" +
                       "- Assist with lead management and buyer inquiries\n" +
                       "- Guide on vendor dashboard features and analytics\n" +
                       "- Provide tips for improving vendor ranking and visibility\n" +
                       "- Explain payment processing and commission structure\n" +
                       "- Help with promotional tools and featured listings\n\n" +
                       "Always encourage upgrading to premium packages for better visibility and features.";

            case "BUYER":
            case "USER":
                return basePrompt +
                       "USER CONTEXT: You are assisting a BUYER/USER on our platform.\n\n" +
                       "BUYER-SPECIFIC GUIDANCE:\n" +
                       "- Help find products and suppliers based on requirements\n" +
                       "- Explain how to place orders, request quotes, and manage purchases\n" +
                       "- Guide through product categories and advanced search features\n" +
                       "- Assist with vendor verification and reviews\n" +
                       "- Explain bulk order benefits and corporate accounts\n" +
                       "- Help with order tracking and customer support\n" +
                       "- Provide tips for getting best prices and deals\n\n" +
                       "Always focus on finding the right suppliers and products for their business needs.";

            case "ADMIN":
                return basePrompt +
                       "USER CONTEXT: You are assisting an ADMIN user.\n\n" +
                       "ADMIN-SPECIFIC GUIDANCE:\n" +
                       "- Help with platform management and oversight\n" +
                       "- Explain admin dashboard features and analytics\n" +
                       "- Assist with user and vendor management\n" +
                       "- Guide on content moderation and quality control\n" +
                       "- Help with system monitoring and reporting\n" +
                       "- Provide insights on platform performance metrics\n\n" +
                       "Focus on administrative functions and platform optimization.";

            default: // NON_LOGGED
                return basePrompt +
                       "USER CONTEXT: You are assisting a VISITOR (not logged in).\n\n" +
                       "VISITOR GUIDANCE:\n" +
                       "- Introduce the platform and its benefits\n" +
                       "- Encourage registration as buyer or vendor based on their needs\n" +
                       "- Explain platform features and how to get started\n" +
                       "- Highlight success stories and testimonials\n" +
                       "- Guide through product categories and sample listings\n" +
                       "- Explain the verification process and trust factors\n" +
                       "- Promote the benefits of joining our B2B community\n\n" +
                       "Always encourage registration and explain the value proposition for their business.";
        }
    }

    /**
     * Generate smart fallback responses when OpenAI is not available
     */
    private String generateFallbackResponse(String userMessage, String systemPrompt) {
        String lowerMessage = userMessage.toLowerCase();
        
        // Greeting responses
        if (lowerMessage.contains("hello") || lowerMessage.contains("hi") || lowerMessage.contains("hey")) {
            if (systemPrompt.contains("VENDOR")) {
                return "Hello! Welcome to your vendor dashboard at Indian Trade Mart. I'm here to help you manage your products, process orders, and grow your business. How can I assist you today?";
            } else if (systemPrompt.contains("BUYER")) {
                return "Hi there! Welcome to Indian Trade Mart, your trusted B2B marketplace. I can help you find suppliers, products, and manage your orders. What are you looking for today?";
            } else {
                return "Hello! Welcome to Indian Trade Mart - India's leading B2B marketplace. Whether you're looking to buy or sell, I'm here to help. Are you a buyer looking for products or a supplier wanting to list your products?";
            }
        }
        
        // Product-related queries
        if (lowerMessage.contains("product") || lowerMessage.contains("buy") || lowerMessage.contains("purchase")) {
            return "I can help you find the right products and suppliers on our platform. We have thousands of verified vendors across categories like Electronics, Machinery, Medical Equipment, IT Hardware, and more. Would you like me to help you search for something specific?";
        }
        
        // Vendor-related queries
        if (lowerMessage.contains("vendor") || lowerMessage.contains("supplier") || lowerMessage.contains("sell")) {
            if (systemPrompt.contains("VENDOR")) {
                return "As a vendor on our platform, you can list unlimited products, manage orders, access leads, and use our analytics tools. Would you like help with product listing, order management, or upgrading your vendor package?";
            } else {
                return "Great! We'd love to have you as a vendor on Indian Trade Mart. You can list your products, connect with buyers, and grow your business. We offer various vendor packages from Basic to Diamond. Would you like to know more about becoming a vendor?";
            }
        }
        
        // Help queries
        if (lowerMessage.contains("help") || lowerMessage.contains("support") || lowerMessage.contains("how")) {
            return "I'm here to help! You can ask me about:\n" +
                   "✓ Finding products and suppliers\n" +
                   "✓ Placing orders and getting quotes\n" +
                   "✓ Becoming a vendor\n" +
                   "✓ Vendor packages and features\n" +
                   "✓ Platform features and benefits\n\n" +
                   "What would you like to know more about?";
        }
        
        // Default response
        return "I'm here to help you with Indian Trade Mart! Whether you're looking to buy products, become a vendor, or need assistance with our platform, I'm happy to guide you. What specific information can I provide for you?";
    }
    
    /**
     * Generate AI-powered product recommendations based on user preferences
     */
    public List<Product> getAIProductRecommendations(User user, String query, int limit) {
        try {
            // Get user's purchase history for context
            String userContext = buildUserContext(user);
            
            // Create AI prompt for product recommendations
            String prompt = String.format(
                "Based on the following user context and query, recommend products from our marketplace:\n\n" +
                "User Context: %s\n\n" +
                "User Query: %s\n\n" +
                "Available Product Categories: Medical Equipment, Laboratory Supplies, Pharmaceuticals, Surgical Instruments, Diagnostic Tools\n\n" +
                "Please provide product recommendations in JSON format with reasoning.",
                userContext, query
            );
            
            String aiResponse = generateResponse(prompt);
            
            // Parse AI response and match with actual products
            List<Product> recommendations = matchProductsFromAIResponse(aiResponse, limit);
            
            // If AI recommendations are insufficient, fall back to collaborative filtering
            if (recommendations.size() < limit) {
                List<Product> fallbackProducts = getCollaborativeFilteringRecommendations(user, limit - recommendations.size());
                recommendations.addAll(fallbackProducts);
            }
            
            return recommendations.stream().distinct().limit(limit).collect(Collectors.toList());
            
        } catch (Exception e) {
            log.error("Error generating AI recommendations: {}", e.getMessage());
            // Fallback to basic recommendations
            return getBasicRecommendations(limit);
        }
    }
    
    /**
     * Generate smart search suggestions using AI
     */
    public List<String> getAISearchSuggestions(String partialQuery) {
        try {
            String prompt = String.format(
                "Given the partial search query '%s' for a B2B medical equipment marketplace, " +
                "suggest 5 complete search terms that buyers might be looking for. " +
                "Focus on medical equipment, laboratory supplies, pharmaceuticals, and healthcare products. " +
                "Return only the suggestions, one per line.",
                partialQuery
            );
            
            String response = generateResponse(prompt);
            
            return List.of(response.split("\n"))
                .stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .limit(5)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Error generating search suggestions: {}", e.getMessage());
            return getBasicSearchSuggestions(partialQuery);
        }
    }
    
    /**
     * Generate product descriptions using AI
     */
    public String generateProductDescription(String productName, String category, String features) {
        try {
            String prompt = String.format(
                "Generate a professional product description for a B2B medical marketplace:\n\n" +
                "Product Name: %s\n" +
                "Category: %s\n" +
                "Key Features: %s\n\n" +
                "Create a compelling, professional description that highlights benefits for healthcare professionals. " +
                "Include technical specifications and compliance information where relevant. " +
                "Keep it between 150-250 words.",
                productName, category, features
            );
            
            return generateResponse(prompt);
            
        } catch (Exception e) {
            log.error("Error generating product description: {}", e.getMessage());
            return "Professional medical equipment designed for healthcare facilities.";
        }
    }
    
    private String buildUserContext(User user) {
        StringBuilder context = new StringBuilder();
        
        // Add user role and preferences
        context.append("User Role: ").append(user.getRole() != null ? user.getRole() : "USER").append("\n");
        
        // Add purchase history context (simplified)
        try {
            long orderCount = orderRepository.countByUserId(user.getId());
            context.append("Previous Orders: ").append(orderCount).append("\n");
            
            // Add recent product categories if available
            // This would need more specific repository methods
            context.append("Preferred Categories: Medical Equipment, Laboratory Supplies\n");
            
        } catch (Exception e) {
            log.warn("Could not build complete user context: {}", e.getMessage());
        }
        
        return context.toString();
    }
    
    private List<Product> matchProductsFromAIResponse(String aiResponse, int limit) {
        List<Product> products = new ArrayList<>();
        
        try {
            // Extract product keywords from AI response
            String[] keywords = extractKeywordsFromResponse(aiResponse);
            
            // Search for products matching these keywords
            for (String keyword : keywords) {
                List<Product> matchingProducts = productRepository.findByNameContainingIgnoreCase(keyword);
                products.addAll(matchingProducts);
                
                if (products.size() >= limit) {
                    break;
                }
            }
            
        } catch (Exception e) {
            log.error("Error matching products from AI response: {}", e.getMessage());
        }
        
        return products.stream().distinct().limit(limit).collect(Collectors.toList());
    }
    
    private String[] extractKeywordsFromResponse(String response) {
        // Simple keyword extraction - in a real implementation, this would be more sophisticated
        return response.toLowerCase()
            .replaceAll("[^a-zA-Z\s]", "")
            .split("\s+");
    }
    
    private List<Product> getCollaborativeFilteringRecommendations(User user, int limit) {
        // Simplified collaborative filtering - recommend popular products
        try {
            return productRepository.findTopRatedProducts(PageRequest.of(0, limit));
        } catch (Exception e) {
            log.error("Error in collaborative filtering: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private List<Product> getBasicRecommendations(int limit) {
        try {
            return productRepository.findTopRatedProducts(PageRequest.of(0, limit));
        } catch (Exception e) {
            log.error("Error getting basic recommendations: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private List<String> getBasicSearchSuggestions(String partialQuery) {
        // Basic fallback suggestions
        return List.of(
            "Medical Equipment",
            "Laboratory Supplies",
            "Surgical Instruments",
            "Diagnostic Tools",
            "Pharmaceutical Products"
        ).stream()
            .filter(s -> s.toLowerCase().contains(partialQuery.toLowerCase()))
            .collect(Collectors.toList());
    }
}

