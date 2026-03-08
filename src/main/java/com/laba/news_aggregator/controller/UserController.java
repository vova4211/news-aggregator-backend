package com.laba.news_aggregator.controller;

import com.laba.news_aggregator.dto.UserDto;
import com.laba.news_aggregator.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{email}")
    public UserDto getUserProfile(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }
}
