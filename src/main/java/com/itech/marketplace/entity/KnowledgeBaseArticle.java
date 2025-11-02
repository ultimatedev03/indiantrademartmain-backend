package com.itech.marketplace.entity;

import com.itech.itech_backend.modules.core.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "knowledge_base_articles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KnowledgeBaseArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 10000, nullable = false)
    private String content;

    @Column(nullable = false)
    private String category;

    @ElementCollection
    @CollectionTable(name = "article_tags", joinColumns = @JoinColumn(name = "article_id"))
    @Column(name = "tag")
    private List<String> tags;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isPublished = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isFeatured = false;

    @Builder.Default
    private Integer viewCount = 0;

    @Builder.Default
    private Integer helpfulCount = 0;

    @Builder.Default
    private Integer notHelpfulCount = 0;

    // SEO fields
    private String metaDescription;
    private String slug;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    private LocalDateTime publishedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        if (this.slug == null || this.slug.isEmpty()) {
            this.slug = generateSlug(this.title);
        }
    }

    private String generateSlug(String title) {
        return title.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }
}

