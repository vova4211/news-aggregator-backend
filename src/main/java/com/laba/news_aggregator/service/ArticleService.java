package com.laba.news_aggregator.service;

import com.laba.news_aggregator.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.laba.news_aggregator.dto.ArticleDto;
import com.laba.news_aggregator.repository.ArticleRepository;
import org.springframework.stereotype.Service;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public Page<ArticleDto> getArticlesPaginated(int page, int size, String search, String category) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<Article> articlePage;

        if (search != null && !search.isBlank()) {
            articlePage = articleRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(search, search, pageable);
        } else if (category != null && !category.isBlank()) {
            articlePage = articleRepository.findByCategory_NameIgnoreCase(category, pageable);
        } else {
            articlePage = articleRepository.findAll(pageable);
        }

        return articlePage.map(article -> new ArticleDto(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getSourceUrl(),
                article.getImageUrl(),
                article.getPublishedAt(),
                article.getCategory() != null ? article.getCategory().getName() : "Без категорії"
        ));
    }
}

