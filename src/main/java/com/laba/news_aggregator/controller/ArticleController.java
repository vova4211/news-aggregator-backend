package com.laba.news_aggregator.controller;

import com.laba.news_aggregator.dto.ArticleDto;
import com.laba.news_aggregator.service.ArticleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public List<ArticleDto> getAllArticles() {
        return articleService.getAllArticles();
    }
}
