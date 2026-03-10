package com.laba.news_aggregator.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(senderEmail);

        message.setTo(toEmail);
        message.setSubject("Код підтвердження | News Aggregator");
        message.setText("Вітаємо у нашому стартапі!\n\n" +
                "Ваш код підтвердження: " + code + "\n\n" +
                "Нікому його не передавайте.");

        mailSender.send(message);
    }
}