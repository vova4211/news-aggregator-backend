package com.laba.news_aggregator.service;

import com.laba.news_aggregator.dto.ArticleDto;
import com.laba.news_aggregator.dto.UserDto;
import com.laba.news_aggregator.entity.Article;
import com.laba.news_aggregator.entity.User;
import com.laba.news_aggregator.exception.ResourceNotFoundException;
import com.laba.news_aggregator.repository.ArticleRepository;
import com.laba.news_aggregator.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;

    public UserService(UserRepository userRepository, ArticleRepository articleRepository) {
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
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

    @Transactional
    public UserDto registerUser(String username, String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Користувач з email " + email + " вже існує!");
        }

        User newUser = new User(username, email);
        userRepository.save(newUser);

        return getUserByEmail(email);
    }

    @Transactional
    public void toggleBookmark(String email, Long articleId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Користувача не знайдено"));
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Новину не знайдено"));

        if (user.getSavedArticles().contains(article)) {
            user.removeArticle(article);
        } else {
            user.addArticle(article);
        }

        userRepository.save(user);
    }
}
