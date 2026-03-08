package com.laba.news_aggregator.service;

import com.laba.news_aggregator.dto.ArticleDto;
import com.laba.news_aggregator.dto.UserDto;
import com.laba.news_aggregator.exception.ResourceNotFoundException;
import com.laba.news_aggregator.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(user -> new UserDto(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getSavedArticles().stream()
                                .map(article -> new ArticleDto(
                                        article.getId(),
                                        article.getTitle(),
                                        article.getContent(),
                                        article.getSourceUrl(),
                                        article.getImageUrl(),
                                        article.getPublishedAt(),
                                        article.getCategory() != null ? article.getCategory().getName() : "Без категорії"
                                ))
                                .toList()
                ))
                .orElseThrow(() -> new ResourceNotFoundException("Користувача з email " + email + " не знайдено"));
    }
}
