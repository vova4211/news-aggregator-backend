package com.laba.news_aggregator.repository;

import com.laba.news_aggregator.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Spring автоматично згенерує всі методи (save, findAll, findById, delete)
}