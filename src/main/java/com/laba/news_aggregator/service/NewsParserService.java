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

    @PostConstruct
    public void parseNewsOnStartup() {
        try {
            String[][] feeds = {
                    {"Технології", "http://feeds.bbci.co.uk/news/technology/rss.xml"},
                    {"Світ", "http://feeds.bbci.co.uk/news/world/rss.xml"},
                    {"Бізнес", "http://feeds.bbci.co.uk/news/business/rss.xml"}
            };

            for (String[] feed : feeds) {
                String categoryName = feed[0];
                String rssUrl = feed[1];

                Category category = new Category(categoryName);
                category = categoryRepository.save(category);

                Document doc = Jsoup.connect(rssUrl).get();
                Elements items = doc.select("item");

                for (int i = 0; i < Math.min(items.size(), 5); i++) {
                    Element item = items.get(i);

                    Article article = new Article(
                            item.select("title").text(),
                            item.select("description").text(),
                            item.select("link").text(),
                            category
                    );
                    articleRepository.save(article);
                }
            }

            System.out.println("Парсинг успішно завершено! Кілька категорій додано до бази даних.");

        } catch (Exception e) {
            System.err.println("Помилка під час парсингу новин: " + e.getMessage());
        }
    }
}
