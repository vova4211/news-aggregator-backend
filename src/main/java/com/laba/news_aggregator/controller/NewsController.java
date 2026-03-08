package com.laba.news_aggregator.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NewsController {
    @GetMapping("/ping")
    public String ping() {
        return "Привіт! Сервер для агрегатора новин успішно працює!";
    }
}
