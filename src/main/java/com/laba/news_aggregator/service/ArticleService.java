package com.laba.news_aggregator.service;

import com.laba.news_aggregator.dto.ArticleDto;
import com.laba.news_aggregator.repository.ArticleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public List<ArticleDto> getAllArticles() {
        return articleRepository.findAll()
                .stream()
                .map(article -> new ArticleDto(
                        article.getId(),
                        article.getTitle(),
                        article.getContent(),
                        article.getSourceUrl(),
                        article.getPublishedAt(),
                        article.getCategory() != null ? article.getCategory().getName() : "Без категорії"
                ))
                .toList();
    }
}
