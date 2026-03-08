package com.laba.news_aggregator.dto;

import java.util.List;

public record UserDto(
        Long id,
        String username,
        String email,
        List<ArticleDto> savedArticles
) {
}