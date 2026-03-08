package com.laba.news_aggregator.repository;

import com.laba.news_aggregator.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    // Тут ми отримуємо всі CRUD операції для Article
}
