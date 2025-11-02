package com.itech.itech_backend.modules.support.service;

import com.itech.marketplace.dto.*;
import com.itech.marketplace.entity.KnowledgeBaseArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface KnowledgeBaseService {
    Page<KnowledgeBaseArticle> getArticles(String category, String search, Boolean published, Pageable pageable);
    KnowledgeBaseArticle getArticleById(Long id);
    KnowledgeBaseArticle getArticleBySlug(String slug);
    void incrementViewCount(Long id);
    KnowledgeBaseArticle createArticle(KnowledgeBaseArticleDto dto);
    KnowledgeBaseArticle updateArticle(Long id, KnowledgeBaseArticleDto dto);
    void deleteArticle(Long id);
    void submitFeedback(Long id, boolean helpful);
    List<String> getAllCategories();
    List<KnowledgeBaseArticle> getFeaturedArticles();
    List<KnowledgeBaseArticle> getPopularArticles();
    List<AutoResponseSuggestionDto> suggestAutoResponses(SuggestResponseDto dto);
}

