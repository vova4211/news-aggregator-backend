package com.laba.news_aggregator.service;

import com.laba.news_aggregator.entity.Article;
import com.laba.news_aggregator.entity.Category;
import com.laba.news_aggregator.repository.ArticleRepository;
import com.laba.news_aggregator.repository.CategoryRepository;
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

    public NewsParserService(CategoryRepository categoryRepository, ArticleRepository articleRepository) {
        this.categoryRepository = categoryRepository;
        this.articleRepository = articleRepository;
    }

    @PostConstruct
    @Scheduled(fixedDelay = 600000)
    public void parseNews() {
        try {
            System.out.println("⏳ Починаю фоновий збір новин...");

            String[][] feeds = {
                    {"Спорт", "http://feeds.bbci.co.uk/sport/rss.xml"},
                    {"Спорт", "https://rss.nytimes.com/services/xml/rss/nyt/Sports.xml"},
                    {"Спорт", "https://www.espn.com/espn/rss/news"},
                    {"Ігри", "https://www.polygon.com/rss/index.xml"},
                    {"Ігри", "https://www.ign.com/rss/articles/feed"},
                    {"Розваги", "http://feeds.bbci.co.uk/news/entertainment_and_arts/rss.xml"},
                    {"Розваги", "https://www.buzzfeed.com/entertainment.xml"},
                    {"Здоров'я", "http://feeds.bbci.co.uk/news/health/rss.xml"},
                    {"Освіта", "http://feeds.bbci.co.uk/news/education/rss.xml"},
                    {"Наука", "http://feeds.bbci.co.uk/news/science_and_environment/rss.xml"},
                    {"Наука", "https://rss.nytimes.com/services/xml/rss/nyt/Science.xml"},
                    {"Бізнес", "http://feeds.bbci.co.uk/news/business/rss.xml"},
                    {"Бізнес", "https://rss.nytimes.com/services/xml/rss/nyt/Business.xml"},
                    {"Політика", "http://feeds.bbci.co.uk/news/politics/rss.xml"},
                    {"Політика", "https://rss.politico.com/politics-news.xml"},
                    {"Технології", "http://feeds.bbci.co.uk/news/technology/rss.xml"},
                    {"Технології", "https://rss.nytimes.com/services/xml/rss/nyt/Technology.xml"},
                    {"Технології", "https://techcrunch.com/feed/"},
                    {"Технології", "https://www.theverge.com/rss/index.xml"},
                    {"Світ", "http://feeds.bbci.co.uk/news/world/rss.xml"},
                    {"Світ", "https://rss.nytimes.com/services/xml/rss/nyt/World.xml"},
                    {"Світ", "http://rss.cnn.com/rss/edition_world.rss"},
                    {"Головні новини", "http://feeds.bbci.co.uk/news/rss.xml"},
                    {"Головні новини", "http://rss.cnn.com/rss/edition.rss"}
            };

            int newArticlesCount = 0;

            for (String[] feed : feeds) {
                String categoryName = feed[0];
                String rssUrl = feed[1];

                Category category = categoryRepository.findByName(categoryName)
                        .orElseGet(() -> categoryRepository.save(new Category(categoryName)));

                Document doc = Jsoup.connect(rssUrl).parser(Parser.xmlParser()).timeout(10000).get();
                Elements items = doc.select("item");

                for (int i = 0; i < Math.min(items.size(), 30); i++) {
                    Element item = items.get(i);
                    String link = item.select("link").text();

                    if (!articleRepository.existsBySourceUrl(link)) {
                        String title = item.select("title").text();

                        String rawDescription = item.select("description").text();
                        String cleanDescription = Jsoup.parse(rawDescription).text();

                        String imageUrl = findImageUrl(item, rawDescription); // Передаємо сирий опис для пошуку картинки

                        Article article = new Article(title, cleanDescription, link, imageUrl, category);
                        articleRepository.save(article);
                        newArticlesCount++;
                    }
                }
            }

            System.out.println("Парсинг завершено! Додано нових статей: " + newArticlesCount);

        } catch (Exception e) {
            System.err.println("Помилка під час парсингу: " + e.getMessage());
        }
    }

    private String findImageUrl(Element item, String rawDescription) {
        String url;

        url = item.select("media|thumbnail").attr("url");
        if (!url.isEmpty()) return url;

        Elements mediaContent = item.select("media|content");
        for (Element content : mediaContent) {
            String type = content.attr("type");
            if (type.isEmpty() || type.startsWith("image/")) {
                url = content.attr("url");
                if (!url.isEmpty()) return url;
            }
        }

        url = item.select("media|group media|content").first() != null ? item.select("media|group media|content").attr("url") : "";
        if (!url.isEmpty()) return url;

        url = item.select("enclosure[type^=image]").attr("url");
        if (!url.isEmpty()) return url;

        Document descDoc = Jsoup.parse(rawDescription);
        Element img = descDoc.select("img").first();
        if (img != null) {
            url = img.attr("src");
            if (!url.isEmpty()) return url;
        }

        return "https://images.unsplash.com/photo-1495020689067-958852a7765e?w=600&q=80";
    }
}