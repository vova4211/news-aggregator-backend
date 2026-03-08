package com.laba.news_aggregator.service;

import com.laba.news_aggregator.dto.CategoryDto;
import com.laba.news_aggregator.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(category -> new CategoryDto(category.getId(), category.getName()))
                .toList();
    }
}
