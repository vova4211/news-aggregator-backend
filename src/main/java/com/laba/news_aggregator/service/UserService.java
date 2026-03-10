package com.laba.news_aggregator.service;

import com.laba.news_aggregator.dto.ArticleDto;
import com.laba.news_aggregator.dto.UserDto;
import com.laba.news_aggregator.entity.Article;
import com.laba.news_aggregator.entity.User;
import java.util.Random;
import com.laba.news_aggregator.exception.ResourceNotFoundException;
import com.laba.news_aggregator.repository.ArticleRepository;
import com.laba.news_aggregator.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, ArticleRepository articleRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
        this.emailService = emailService;
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
    public void registerUser(String username, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Користувач з email " + email + " вже існує!");
        }

        String code = String.format("%06d", new Random().nextInt(999999));

        User newUser = new User(username, email, password);
        newUser.setVerificationCode(code);
        newUser.setVerified(false);

        userRepository.save(newUser);

        emailService.sendVerificationCode(email, code);
    }

    @Transactional
    public UserDto verifyCode(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Користувача не знайдено"));

        if (user.getVerificationCode() != null && user.getVerificationCode().equals(code)) {
            user.setVerified(true);
            user.setVerificationCode(null);
            userRepository.save(user);
            return getUserByEmail(email);
        }
        throw new RuntimeException("Невірний код підтвердження!");
    }

    @Transactional(readOnly = true)
    public UserDto login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Користувача не знайдено"));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Невірний пароль!");
        }
        if (!user.isVerified()) {
            throw new RuntimeException("Акаунт не підтверджено! Перевірте пошту.");
        }
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
