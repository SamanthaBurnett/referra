package com.sbproject.referra.controller;

import com.sbproject.referra.dto.UserResponse;
import com.sbproject.referra.model.User;
import com.sbproject.referra.service.CurrentUserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final CurrentUserService currentUserService;

    public UserController(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    @GetMapping("/api/users/me")
    public UserResponse getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        User user = currentUserService.getOrCreateUser(jwt);
        return UserResponse.from(user);
    }
}
