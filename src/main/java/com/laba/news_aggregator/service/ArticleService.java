package com.laba.news_aggregator.service;

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

    public Page<ArticleDto> getArticlesPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        return articleRepository.findAll(pageable)
                .map(article -> new ArticleDto(
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

