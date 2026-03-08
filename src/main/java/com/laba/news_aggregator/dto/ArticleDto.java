package com.laba.news_aggregator.dto;

import java.time.LocalDateTime;

public record ArticleDto(
        Long id,
        String title,
        String content,
        String sourceUrl,
        String imageUrl,
        LocalDateTime publishedAt,
        String categoryName
) {
}
