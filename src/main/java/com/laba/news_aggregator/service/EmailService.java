package com.laba.news_aggregator.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Код підтвердження | News Aggregator");
        message.setText("Вітаємо у нашому стартапі!\n\n" +
                "Ваш код підтвердження: " + code + "\n\n" +
                "Нікому його не передавайте.");

        mailSender.send(message);
    }
}