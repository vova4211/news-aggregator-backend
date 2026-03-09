package com.laba.news_aggregator.service;

import com.laba.news_aggregator.entity.Article;
import com.laba.news_aggregator.entity.Category;
import com.laba.news_aggregator.entity.User;
import com.laba.news_aggregator.repository.ArticleRepository;
import com.laba.news_aggregator.repository.CategoryRepository;
import com.laba.news_aggregator.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import jakarta.annotation.PostConstruct;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class NewsParserService {

    private final CategoryRepository categoryRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    public NewsParserService(CategoryRepository categoryRepository, ArticleRepository articleRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;

    }

    @PostConstruct
    @Scheduled(fixedDelay = 600000)
    public void parseNews() {
        try {
            System.out.println("⏳ Починаю фоновий збір новин...");

            String[][] feeds = {
                    {"Головні новини", "http://feeds.bbci.co.uk/news/rss.xml"},
                    {"Світ", "http://feeds.bbci.co.uk/news/world/rss.xml"},
                    {"Бізнес", "http://feeds.bbci.co.uk/news/business/rss.xml"},
                    {"Політика", "http://feeds.bbci.co.uk/news/politics/rss.xml"},
                    {"Технології", "http://feeds.bbci.co.uk/news/technology/rss.xml"},
                    {"Наука", "http://feeds.bbci.co.uk/news/science_and_environment/rss.xml"},
                    {"Здоров'я", "http://feeds.bbci.co.uk/news/health/rss.xml"},
                    {"Освіта", "http://feeds.bbci.co.uk/news/education/rss.xml"},
                    {"Розваги", "http://feeds.bbci.co.uk/news/entertainment_and_arts/rss.xml"},
                    {"Спорт", "http://feeds.bbci.co.uk/sport/rss.xml"}
            };

            int newArticlesCount = 0;

            for (String[] feed : feeds) {
                String categoryName = feed[0];
                String rssUrl = feed[1];

                Category category = categoryRepository.findByName(categoryName)
                        .orElseGet(() -> categoryRepository.save(new Category(categoryName)));

                Document doc = Jsoup.connect(rssUrl).parser(Parser.xmlParser()).get();
                Elements items = doc.select("item");

                for (int i = 0; i < Math.min(items.size(), 30); i++) {
                    Element item = items.get(i);
                    String link = item.select("link").text();

                    if (!articleRepository.existsBySourceUrl(link)) {
                        String title = item.select("title").text();
                        String description = item.select("description").text();

                        String imageUrl = item.select("media|thumbnail").attr("url");
                        if (imageUrl.isEmpty()) {
                            imageUrl = "https://via.placeholder.com/600x400?text=No+Image";
                        }

                        Article article = new Article(title, description, link, imageUrl, category);
                        articleRepository.save(article);
                        newArticlesCount++;
                    }
                }
            }

            if (userRepository.findByEmail("test@laba.com").isEmpty()) {
                User testUser = new User("test_user", "test@laba.com");
                testUser = userRepository.save(testUser);

                if (articleRepository.count() >= 2) {
                    testUser.addArticle(articleRepository.findAll().get(0));
                    testUser.addArticle(articleRepository.findAll().get(1));
                    userRepository.save(testUser);
                }
            }

            System.out.println("✅ Парсинг завершено! Додано нових статей: " + newArticlesCount);

        } catch (Exception e) {
            System.err.println("❌ Помилка під час парсингу: " + e.getMessage());
        }
    }
}