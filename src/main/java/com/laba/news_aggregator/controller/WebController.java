package com.laba.news_aggregator.controller;

import com.laba.news_aggregator.service.ArticleService;
import com.laba.news_aggregator.service.CategoryService; // ДОДАНО
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {

    private final ArticleService articleService;
    private final CategoryService categoryService;

    public WebController(ArticleService articleService, CategoryService categoryService) {
        this.articleService = articleService;
        this.categoryService = categoryService;
    }

    @GetMapping("/")
    public String homePage(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category
    ) {
        var articlePage = articleService.getArticlesPaginated(page, size, search, category);

        model.addAttribute("articles", articlePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articlePage.getTotalPages());
        model.addAttribute("search", search);
        model.addAttribute("category", category);
        // Передаємо всі категорії на фронтенд для меню
        model.addAttribute("allCategories", categoryService.getAllCategories());

        return "index";
    }
}