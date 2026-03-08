package com.laba.news_aggregator.service;

import com.laba.news_aggregator.entity.Article;
import com.laba.news_aggregator.entity.Category;
import com.laba.news_aggregator.repository.ArticleRepository;
import com.laba.news_aggregator.repository.CategoryRepository;
import jakarta.annotation.PostConstruct;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class NewsParserService {

    private final CategoryRepository categoryRepository;
    private final ArticleRepository articleRepository;

    public NewsParserService(CategoryRepository categoryRepository, ArticleRepository articleRepository) {
        this.categoryRepository = categoryRepository;
        this.articleRepository = articleRepository;
    }

    // Анотація @PostConstruct змушує Spring виконати цей метод автоматично під час запуску сервера
    @PostConstruct
    public void parseNewsOnStartup() {
        try {
            Category techCategory = new Category("Технології (BBC)");
            techCategory = categoryRepository.save(techCategory);

            String rssUrl = "http://feeds.bbci.co.uk/news/technology/rss.xml";
            Document doc = Jsoup.connect(rssUrl).get();

            Elements items = doc.select("item");

            for (int i = 0; i < Math.min(items.size(), 10); i++) {
                Element item = items.get(i);

                String title = item.select("title").text();
                String link = item.select("link").text();
                String description = item.select("description").text();

                Article article = new Article(title, description, link, techCategory);
                articleRepository.save(article);
            }

            System.out.println("Парсинг успішно завершено! Новини додано до бази даних.");

        } catch (Exception e) {
            System.err.println("Помилка під час парсингу новин: " + e.getMessage());
        }
    }
}
