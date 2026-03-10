package com.laba.news_aggregator.controller;

import com.laba.news_aggregator.service.ArticleService;
import com.laba.news_aggregator.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

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

        System.out.println("СПРОБА ВІДМАЛЮВАТИ СТОРІНКУ. ЗНАЙДЕНО НОВИН В БАЗІ: " + articlePage.getTotalElements());
        System.out.println("ПЕРЕДАЄМО НА ФРОНТЕНД ШТУК: " + articlePage.getContent().size());

        Map<String, String> categoryIcons = Map.ofEntries(
                Map.entry("Спорт", "bi-trophy"),
                Map.entry("Ігри", "bi-controller"),
                Map.entry("Бізнес", "bi-briefcase"),
                Map.entry("Головні новини", "bi-newspaper"),
                Map.entry("Здоров'я", "bi-heart-pulse"),
                Map.entry("Наука", "bi-lightbulb"),
                Map.entry("Освіта", "bi-book"),
                Map.entry("Політика", "bi-bank"),
                Map.entry("Розваги", "bi-film"),
                Map.entry("Світ", "bi-globe-americas"),
                Map.entry("Технології", "bi-laptop")
        );

        model.addAttribute("articles", articlePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articlePage.getTotalPages());
        model.addAttribute("search", search);
        model.addAttribute("category", category);
        model.addAttribute("allCategories", categoryService.getAllCategories());
        model.addAttribute("icons", categoryIcons);

        return "index";
    }
}