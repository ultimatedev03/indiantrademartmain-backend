package com.itech.itech_backend.modules.support.service;

import com.itech.itech_backend.modules.shared.dto.ChatbotRequestDto;
import com.itech.itech_backend.modules.shared.dto.ChatbotResponseDto;
import com.itech.itech_backend.enums.VendorType;
import com.itech.itech_backend.modules.shared.model.*;
import com.itech.itech_backend.modules.shared.repository.*;
import com.itech.itech_backend.modules.vendor.model.Vendors;
import com.itech.itech_backend.modules.buyer.model.Product;
import com.itech.itech_backend.modules.buyer.model.Category;
import com.itech.itech_backend.modules.vendor.model.VendorRanking;
import com.itech.itech_backend.modules.support.model.ChatbotMessage;
import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.support.repository.ChatbotMessageRepository;
import com.itech.itech_backend.modules.core.repository.UserRepository;
import com.itech.itech_backend.modules.buyer.repository.BuyerProductRepository;
import com.itech.itech_backend.modules.buyer.repository.BuyerCategoryRepository;
import com.itech.itech_backend.modules.vendor.repository.VendorRankingRepository;
import com.itech.itech_backend.modules.shared.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService {

    private final ChatbotMessageRepository chatbotMessageRepository;
    private final UserRepository userRepository;
    private final BuyerProductRepository productRepository;
    private final BuyerCategoryRepository categoryRepository;
    private final VendorRankingRepository vendorRankingRepository;
    private final OpenAiService openAiService;

    public ChatbotResponseDto processMessage(ChatbotRequestDto request) {
        log.info("Processing chatbot message: {}", request.getMessage());
        
        String userMessage = request.getMessage().toLowerCase().trim();
        String sessionId = request.getSessionId();
        String userRole = request.getUserRole() != null ? request.getUserRole() : "NON_LOGGED";
        
        // Generate session ID if not provided
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString();
        }
        
        // Process the message and generate response
        ChatbotResponseDto response = generateResponse(userMessage, sessionId, userRole);
        
        // Save the conversation
        saveChatMessage(request, response);
        
        return response;
    }

    /**
     * Enhanced role-based message processing
     */
    public ChatbotResponseDto processRoleBasedMessage(ChatbotRequestDto request) {
        log.info("Processing role-based chatbot message: {} from user role: {}", request.getMessage(), request.getUserRole());
        
        String userMessage = request.getMessage().trim();
        String sessionId = request.getSessionId();
        String userRole = request.getUserRole() != null ? request.getUserRole() : "NON_LOGGED";
        
        // Generate session ID if not provided
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString();
        }
        
        // Process with enhanced role-based logic
        ChatbotResponseDto response = generateRoleBasedResponse(userMessage, sessionId, userRole, request.getUserId());
        
        // Save the conversation
        saveChatMessage(request, response);
        
        return response;
    }

    /**
     * Generate role-based response with enhanced logic
     */
    private ChatbotResponseDto generateRoleBasedResponse(String userMessage, String sessionId, String userRole, Long userId) {
        ChatbotResponseDto response;
        
        // Handle different user roles
        if (userRole == null || userRole.isEmpty() || "NON_LOGGED".equalsIgnoreCase(userRole)) {
            // Non-logged user
            response = handleNonLoggedUserQuery(userMessage, sessionId);
        } else if ("VENDOR".equalsIgnoreCase(userRole)) {
            // Vendor user
            response = handleVendorQuery(userMessage, sessionId, userId);
        } else if ("BUYER".equalsIgnoreCase(userRole) || "USER".equalsIgnoreCase(userRole)) {
            // Buyer/User
            response = handleBuyerQuery(userMessage, sessionId, userId);
        } else {
            // Default response
            response = generateResponse(userMessage, sessionId, userRole);
        }
        
        return response;
    }

    private ChatbotResponseDto generateResponse(String userMessage, String sessionId, String userRole) {
        // Use OpenAI service with role-based prompts
        String responseText = openAiService.generateRoleBasedResponse(userMessage, userRole);
        
        List<ChatbotResponseDto.VendorRecommendationDto> recommendations = new ArrayList<>();
        
        // Add vendor recommendations if it's a product/service query
        if (isProductQuery(userMessage) || isServiceQuery(userMessage)) {
            recommendations = findRecommendedVendors(userMessage);
        }
        
        return ChatbotResponseDto.builder()
                .response(responseText)
                .sessionId(sessionId)
                .recommendations(recommendations)
                .hasRecommendations(!recommendations.isEmpty())
                .build();
    }

    private ChatbotResponseDto generateResponse(String userMessage, String sessionId) {
        List<ChatbotResponseDto.VendorRecommendationDto> recommendations = new ArrayList<>();
        String responseText;
        
        // Check if user is asking about products or services
        if (isProductQuery(userMessage)) {
            recommendations = findRecommendedVendors(userMessage);
            responseText = generateProductResponse(userMessage, recommendations);
        } else if (isServiceQuery(userMessage)) {
            recommendations = findServiceProviders(userMessage);
            responseText = generateServiceResponse(userMessage, recommendations);
        } else if (isGreeting(userMessage)) {
            responseText = generateGreetingResponse();
        } else if (isHelpQuery(userMessage)) {
            responseText = generateHelpResponse();
        } else {
            responseText = openAiService.generateResponse(userMessage);
        }
        
        return ChatbotResponseDto.builder()
                .response(responseText)
                .sessionId(sessionId)
                .recommendations(recommendations)
                .hasRecommendations(!recommendations.isEmpty())
                .build();
    }

    private List<ChatbotResponseDto.VendorRecommendationDto> findRecommendedVendors(String query) {
        try {
            // Extract keywords from query
            List<String> keywords = extractKeywords(query);
            log.debug("Extracted keywords: {}", keywords);
            
            if (keywords.isEmpty()) {
                log.debug("No keywords found, returning empty recommendations");
                return new ArrayList<>();
            }
            
            // Find products matching keywords
            List<Product> matchingProducts = productRepository.findAll().stream()
                    .filter(product -> matchesKeywords(product, keywords))
                    .collect(Collectors.toList());
            
            log.debug("Found {} matching products", matchingProducts.size());
            
            // Get vendors from matching products
            Set<Vendors> vendors = matchingProducts.stream()
                    .map(Product::getVendor)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            
            log.debug("Found {} unique vendors", vendors.size());
            
            // Sort vendors by premium type and performance
            return vendors.stream()
                    .sorted(this::compareVendorsByPriority)
                    .limit(5) // Limit to top 5 recommendations
                    .map(vendor -> buildVendorRecommendation(vendor, matchingProducts))
                    .filter(Objects::nonNull) // Filter out any failed builds
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding recommended vendors", e);
            return new ArrayList<>();
        }
    }

    private List<ChatbotResponseDto.VendorRecommendationDto> findServiceProviders(String query) {
        // For service queries, find vendors by categories
        List<String> keywords = extractKeywords(query);
        
        // Find categories matching keywords
        List<Category> matchingCategories = categoryRepository.findAll().stream()
                .filter(category -> matchesKeywords(category.getName(), keywords))
                .collect(Collectors.toList());
        
        // Find products in these categories
        List<Product> categoryProducts = productRepository.findAll().stream()
                .filter(product -> matchingCategories.contains(product.getCategory()))
                .collect(Collectors.toList());
        
        // Get vendors from category products
        Set<Vendors> serviceProviders = categoryProducts.stream()
                .map(Product::getVendor)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        
        return serviceProviders.stream()
                .sorted(this::compareVendorsByPriority)
                .limit(5)
                .map(vendor -> buildVendorRecommendation(vendor, categoryProducts))
                .collect(Collectors.toList());
    }

    private ChatbotResponseDto.VendorRecommendationDto buildVendorRecommendation(Vendors vendor, List<Product> products) {
        // Get vendor's products
        List<Product> vendorProducts = products.stream()
                .filter(product -> product.getVendor().getId().equals(vendor.getId()))
                .collect(Collectors.toList());
        
        // Get vendor's categories
        List<String> categories = vendorProducts.stream()
                .map(product -> product.getCategory().getName())
                .distinct()
                .collect(Collectors.toList());
        
        // Get vendor's product names
        List<String> productNames = vendorProducts.stream()
                .map(Product::getName)
                .distinct()
                .collect(Collectors.toList());
        
        // Get performance score
        Double performanceScore = vendorRankingRepository.findByVendorId(vendor.getId())
                .map(VendorRanking::getPerformanceScore)
                .orElse(0.0);
        
        // Generate recommendation reason
        String reason = generateRecommendationReason(vendor, vendorProducts.size(), categories.size());
        
        // Generate contact and profile URLs
        String contactUrl = "/contact/vendor/" + vendor.getId();
        String profileUrl = "/vendor/profile/" + vendor.getId();
        
        return ChatbotResponseDto.VendorRecommendationDto.builder()
                .vendorId(vendor.getId())
                .vendorName(vendor.getName())
                .vendorEmail(vendor.getEmail())
                .vendorPhone(vendor.getPhone())
                .vendorType(vendor.getVendorType() != null ? vendor.getVendorType().name() : "BASIC")
                .performanceScore(performanceScore)
                .products(productNames)
                .categories(categories)
                .reason(reason)
                .contactUrl(contactUrl)
                .profileUrl(profileUrl)
                .build();
    }

    private String generateRecommendationReason(Vendors vendor, int productCount, int categoryCount) {
        StringBuilder reason = new StringBuilder();
        
        // Premium vendor type
        if (vendor.getVendorType() != null && vendor.getVendorType() != VendorType.BASIC) {
            reason.append("Premium ").append(vendor.getVendorType().name()).append(" vendor");
        } else {
              reason.append("Verified vendor");
        }
        
        // Product diversity
        if (productCount > 1) {
            reason.append(" with ").append(productCount).append(" products");
        }
        
        // Category expertise
        if (categoryCount > 1) {
            reason.append(" across ").append(categoryCount).append(" categories");
        }
        
        return reason.toString();
    }

    private int compareVendorsByPriority(Vendors v1, Vendors v2) {
        // First priority: Vendor type (premium vendors first)
        VendorType type1 = v1.getVendorType() != null ? v1.getVendorType() : VendorType.BASIC;
        VendorType type2 = v2.getVendorType() != null ? v2.getVendorType() : VendorType.BASIC;
        int typeComparison = getVendorTypePriority(type1) - getVendorTypePriority(type2);
        if (typeComparison != 0) {
            return typeComparison;
        }
        
        // Second priority: Performance score
        Double score1 = vendorRankingRepository.findByVendorId(v1.getId())
                .map(VendorRanking::getPerformanceScore)
                .orElse(0.0);
        Double score2 = vendorRankingRepository.findByVendorId(v2.getId())
                .map(VendorRanking::getPerformanceScore)
                .orElse(0.0);
        
        return Double.compare(score2, score1); // Higher score first
    }

    private int getVendorTypePriority(VendorType vendorType) {
        if (vendorType == null) {
            return 5;
        }
        switch (vendorType) {
            case DIAMOND: return 1;
            case PLATINUM: return 2;
            case GOLD: return 3;
            case BASIC: return 4;
            default: return 5;
        }    }

    private List<String> extractKeywords(String query) {
        return Arrays.stream(query.toLowerCase().split("\\s+"))
                .filter(word -> word.length() > 2) // Filter out short words
                .filter(word -> !isStopWord(word))
                .collect(Collectors.toList());
    }

    private boolean isStopWord(String word) {
        Set<String> stopWords = Set.of("the", "and", "or", "but", "for", "with", "from", "can", "you", "what", "where", "how", "when", "why", "need", "want", "looking", "find", "get", "have", "has", "are", "is", "was", "were", "been", "being", "will", "would", "could", "should");
        return stopWords.contains(word);
    }

    private boolean matchesKeywords(Product product, List<String> keywords) {
        String productText = (product.getName() + " " + product.getDescription() + " " + product.getCategory().getName()).toLowerCase();
        return keywords.stream().anyMatch(productText::contains);
    }

    private boolean matchesKeywords(String text, List<String> keywords) {
        String lowerText = text.toLowerCase();
        return keywords.stream().anyMatch(lowerText::contains);
    }

    private boolean isProductQuery(String message) {
        String[] productKeywords = {"product", "item", "buy", "purchase", "price", "cost", "sell", "selling", "available", "stock"};
        return Arrays.stream(productKeywords).anyMatch(message::contains);
    }

    private boolean isServiceQuery(String message) {
        String[] serviceKeywords = {"service", "provider", "company", "business", "offer", "provides", "specializes", "expert", "professional"};
        return Arrays.stream(serviceKeywords).anyMatch(message::contains);
    }

    private boolean isGreeting(String message) {
        String[] greetings = {"hi", "hello", "hey", "good morning", "good afternoon", "good evening"};
        return Arrays.stream(greetings).anyMatch(message::contains);
    }

    private boolean isHelpQuery(String message) {
        String[] helpKeywords = {"help", "assist", "support", "how to", "what can", "options", "menu"};
        return Arrays.stream(helpKeywords).anyMatch(message::contains);
    }

    private String generateProductResponse(String query, List<ChatbotResponseDto.VendorRecommendationDto> recommendations) {
        if (recommendations.isEmpty()) {
            return "I couldn't find any vendors for your product query. Please try with different keywords or browse our categories.";
        }
        
        StringBuilder response = new StringBuilder();
        response.append("Here are the top recommended vendors for your product query:\n\n");
        
        for (int i = 0; i < recommendations.size(); i++) {
            ChatbotResponseDto.VendorRecommendationDto vendor = recommendations.get(i);
            response.append(String.format("%d. **%s** (%s)\n", i + 1, vendor.getVendorName(), vendor.getVendorType()));
            response.append(String.format("   📧 %s | 📞 %s\n", vendor.getVendorEmail(), vendor.getVendorPhone()));
            response.append(String.format("   🏆 %s\n", vendor.getReason()));
            response.append(String.format("   📦 Products: %s\n\n", String.join(", ", vendor.getProducts())));
        }
        
        response.append("Would you like more information about any of these vendors?");
        return response.toString();
    }

    private String generateServiceResponse(String query, List<ChatbotResponseDto.VendorRecommendationDto> recommendations) {
        if (recommendations.isEmpty()) {
            return "I couldn't find any service providers for your query. Please try with different keywords or browse our categories.";
        }
        
        StringBuilder response = new StringBuilder();
        response.append("Here are the top recommended service providers:\n\n");
        
        for (int i = 0; i < recommendations.size(); i++) {
            ChatbotResponseDto.VendorRecommendationDto vendor = recommendations.get(i);
            response.append(String.format("%d. **%s** (%s)\n", i + 1, vendor.getVendorName(), vendor.getVendorType()));
            response.append(String.format("   📧 %s | 📞 %s\n", vendor.getVendorEmail(), vendor.getVendorPhone()));
            response.append(String.format("   🏆 %s\n", vendor.getReason()));
            response.append(String.format("   🎯 Services: %s\n\n", String.join(", ", vendor.getCategories())));
        }
        
        response.append("Would you like to connect with any of these service providers?");
        return response.toString();
    }

    private String generateGreetingResponse() {
        return "Hello! 👋 Welcome to iTech! I'm here to help you find the best vendors and products on our platform.\n\n" +
               "You can ask me about:\n" +
               "• Products you're looking for\n" +
               "• Services you need\n" +
               "• Vendor recommendations\n" +
               "• Categories and pricing\n\n" +
               "What can I help you with today?";
    }

    private String generateHelpResponse() {
        return "I can help you with:\n\n" +
               "🔍 **Finding Products**: Ask me about specific products you're looking for\n" +
               "🏢 **Service Providers**: Find vendors who offer specific services\n" +
               "⭐ **Vendor Recommendations**: Get premium vendor suggestions\n" +
               "📊 **Categories**: Browse products by category\n\n" +
               "**Example queries:**\n" +
               "• \"I need electronics products\"\n" +
               "• \"Who provides web development services?\"\n" +
               "• \"Show me premium vendors for furniture\"\n" +
               "• \"Find vendors in Mumbai\"\n\n" +
               "What would you like to know?";
    }

    private String generateDefaultResponse() {
        return "I'm here to help you find the best vendors and products! Try asking me about:\n\n" +
               "• Specific products you need\n" +
               "• Services you're looking for\n" +
               "• Vendor recommendations\n\n" +
               "For example: \"I need electronics\" or \"Who provides web services?\"";
    }

    private void saveChatMessage(ChatbotRequestDto request, ChatbotResponseDto response) {
        try {
            User user = null;
            if (request.getUserId() != null) {
                user = userRepository.findById(request.getUserId()).orElse(null);
            }
            
            ChatbotMessage chatMessage = ChatbotMessage.builder()
                    .sessionId(request.getSessionId())
                    .userMessage(request.getMessage())
                    .botResponse(response.getResponse())
                    .userIp(request.getUserIp())
                    .user(user)
                    .build();
            
            chatbotMessageRepository.save(chatMessage);
        } catch (Exception e) {
            log.error("Error saving chat message: {}", e.getMessage());
        }
    }

    public List<ChatbotMessage> getChatHistory(String sessionId) {
        return chatbotMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
    }
    
    public ChatbotResponseDto startSession(ChatbotRequestDto requestDto) {
        String sessionId = requestDto.getSessionId();

        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString();
        }

        String greeting = generateGreetingResponse(); // reuse your own method

        return ChatbotResponseDto.builder()
                .sessionId(sessionId)
                .response(greeting)
                .hasRecommendations(false)
                .recommendations(Collections.emptyList())
                .build();
    }
    
    
    private ChatbotResponseDto handleNonLoggedUserQuery(String userMessage, String sessionId) {
        List<ChatbotResponseDto.VendorRecommendationDto> recommendations = new ArrayList<>();
        String responseText;
        String responseType = "GENERAL";
        String suggestedAction = null;
        
        if (isProductQuery(userMessage) || isServiceQuery(userMessage)) {
            recommendations = findRecommendedVendors(userMessage);
            responseText = generateNonLoggedProductResponse(userMessage, recommendations);
            responseType = "VENDOR_RECOMMENDATIONS";
            suggestedAction = "LOGIN";
        } else if (isGreeting(userMessage)) {
            responseText = "Hello! 👋 Welcome to iTech! I can help you find vendors and products. " +
                         "Ask me about any product or service you need, and I'll show you our best vendors. " +
                         "Please note: You'll need to login to contact vendors directly.";
            suggestedAction = "REGISTER_OR_LOGIN";
        } else {
            responseText = "I can help you find vendors and products! Try asking me about:\n\n" +
                         "• Electronics, machinery, textiles\n" +
                         "• Industrial supplies and equipment\n" +
                         "• Manufacturing services\n\n" +
                         "What are you looking for today?";
        }
        
        return ChatbotResponseDto.builder()
                .response(responseText)
                .sessionId(sessionId)
                .recommendations(recommendations)
                .hasRecommendations(!recommendations.isEmpty())
                .leadRecommendations(new ArrayList<>())
                .hasLeadRecommendations(false)
                .responseType(responseType)
                .userRole("NON_LOGGED")
                .requiresLogin(!recommendations.isEmpty())
                .suggestedAction(suggestedAction)
                .build();
    }
    
    private ChatbotResponseDto handleVendorQuery(String userMessage, String sessionId, Long vendorId) {
        String responseText;
        List<ChatbotResponseDto.VendorRecommendationDto> recommendations = new ArrayList<>();
        List<ChatbotResponseDto.LeadRecommendationDto> leadRecommendations = new ArrayList<>();
        String responseType = "GENERAL";
        String suggestedAction = null;
        
        if (isProductQuery(userMessage) || isServiceQuery(userMessage)) {
            // For vendors, show leads instead of other vendors
            responseText = generateVendorLeadResponse(userMessage, vendorId);
            // In future: leadRecommendations = findPotentialLeads(userMessage, vendorId);
            responseType = "LEAD_RECOMMENDATIONS";
            suggestedAction = "VIEW_LEADS";
        } else if (isGreeting(userMessage)) {
            responseText = "Hello! 👋 Welcome to your iTech vendor portal! I can help you with:\n\n" +
                         "• Finding leads for your products/services\n" +
                         "• Managing your product listings\n" +
                         "• Tracking your performance\n" +
                         "• Understanding market demand\n\n" +
                         "What would you like to know?";
            suggestedAction = "EXPLORE_VENDOR_DASHBOARD";
        } else {
            responseText = "As a vendor, I can help you:\n\n" +
                         "🎯 **Find Leads**: Ask about products you sell to see potential customers\n" +
                         "📊 **Analytics**: Get insights about your business performance\n" +
                         "📦 **Product Management**: Help with listing optimization\n" +
                         "💬 **Customer Inquiries**: Manage and respond to buyer questions\n\n" +
                         "Try asking: 'Show me leads for electronics' or 'How is my business performing?'";
        }
        
        return ChatbotResponseDto.builder()
                .response(responseText)
                .sessionId(sessionId)
                .recommendations(recommendations)
                .hasRecommendations(!recommendations.isEmpty())
                .leadRecommendations(leadRecommendations)
                .hasLeadRecommendations(!leadRecommendations.isEmpty())
                .responseType(responseType)
                .userRole("VENDOR")
                .requiresLogin(false)
                .suggestedAction(suggestedAction)
                .build();
    }
    
    private ChatbotResponseDto handleBuyerQuery(String userMessage, String sessionId, Long buyerId) {
        List<ChatbotResponseDto.VendorRecommendationDto> recommendations = new ArrayList<>();
        String responseText;
        String responseType = "GENERAL";
        String suggestedAction = null;
        
        if (isProductQuery(userMessage) || isServiceQuery(userMessage)) {
            recommendations = findRecommendedVendors(userMessage);
            responseText = generateBuyerProductResponse(userMessage, recommendations);
            responseType = "VENDOR_RECOMMENDATIONS";
            suggestedAction = "CONTACT_VENDOR";
        } else if (isGreeting(userMessage)) {
            responseText = "Hello! 👋 Welcome back to iTech! I can help you:\n\n" +
                         "• Find products and services\n" +
                         "• Connect with verified vendors\n" +
                         "• Track your orders and inquiries\n" +
                         "• Get quotes and pricing\n\n" +
                         "What are you looking for today?";
            suggestedAction = "EXPLORE_MARKETPLACE";
        } else {
            responseText = "I can help you find exactly what you need! Try asking:\n\n" +
                         "🔍 **Product Search**: 'I need laptops' or 'Who sells steel pipes?'\n" +
                         "🏤 **Service Providers**: 'Find manufacturing services' or 'Web development companies'\n" +
                         "📋 **Order Status**: 'Track my order' or 'My recent purchases'\n" +
                         "💰 **Pricing**: 'Get quotes for bulk orders'\n\n" +
                         "What can I help you find?";
        }
        
        return ChatbotResponseDto.builder()
                .response(responseText)
                .sessionId(sessionId)
                .recommendations(recommendations)
                .hasRecommendations(!recommendations.isEmpty())
                .leadRecommendations(new ArrayList<>())
                .hasLeadRecommendations(false)
                .responseType(responseType)
                .userRole("BUYER")
                .requiresLogin(false)
                .suggestedAction(suggestedAction)
                .build();
    }
    
    private String generateNonLoggedProductResponse(String query, List<ChatbotResponseDto.VendorRecommendationDto> recommendations) {
        if (recommendations.isEmpty()) {
            return "I couldn't find vendors for your query, but we have many verified suppliers! " +
                   "Please register or login to access our complete vendor directory and contact them directly.";
        }
        
        StringBuilder response = new StringBuilder();
        response.append("Great! I found some excellent vendors for your needs:\n\n");
        
        for (int i = 0; i < Math.min(recommendations.size(), 3); i++) {
            ChatbotResponseDto.VendorRecommendationDto vendor = recommendations.get(i);
            response.append(String.format("%d. **%s** (%s)\n", i + 1, vendor.getVendorName(), vendor.getVendorType()));
            response.append(String.format("   🏆 %s\n", vendor.getReason()));
            response.append(String.format("   📦 Specializes in: %s\n\n", 
                String.join(", ", vendor.getCategories().subList(0, Math.min(2, vendor.getCategories().size())))));
        }
        
        response.append("🔐 **To contact these vendors and get quotes, please login or register first.**\n\n");
        response.append("📞 Ready to connect with suppliers? Click the login button above!");
        
        return response.toString();
    }
    
    private String generateBuyerProductResponse(String query, List<ChatbotResponseDto.VendorRecommendationDto> recommendations) {
        if (recommendations.isEmpty()) {
            return "I couldn't find vendors for your specific query. Try browsing our categories or contact our support team for assistance.";
        }
        
        StringBuilder response = new StringBuilder();
        response.append("Perfect! Here are the top verified vendors for your needs:\n\n");
        
        for (int i = 0; i < recommendations.size(); i++) {
            ChatbotResponseDto.VendorRecommendationDto vendor = recommendations.get(i);
            response.append(String.format("%d. **%s** (%s)\n", i + 1, vendor.getVendorName(), vendor.getVendorType()));
            response.append(String.format("   📧 %s | 📞 %s\n", vendor.getVendorEmail(), vendor.getVendorPhone()));
            response.append(String.format("   🏆 %s\n", vendor.getReason()));
            response.append(String.format("   📦 Products: %s\n\n", String.join(", ", vendor.getProducts())));
        }
        
        response.append("💡 **Click on any vendor above to view their profile and send inquiries!**");
        return response.toString();
    }
    
    private String generateVendorLeadResponse(String query, Long vendorId) {
        // In a real implementation, you'd search for buyer leads matching the vendor's products
        return "🎯 **Potential Leads Found!**\n\n" +
               "Based on your query, here are recent buyer inquiries that match your expertise:\n\n" +
               "1. **ABC Industries** - Looking for " + query + "\n" +
               "   📍 Location: Mumbai\n" +
               "   💰 Budget: ₹2-5 Lakhs\n" +
               "   ⏰ Timeline: 2 weeks\n\n" +
               "2. **XYZ Manufacturing** - Bulk order inquiry\n" +
               "   📍 Location: Delhi\n" +
               "   💰 Budget: ₹5-10 Lakhs\n" +
               "   ⏰ Timeline: 1 month\n\n" +
               "💼 **Want to respond to these leads?** Go to your Leads tab in the vendor dashboard!";
    }
}

