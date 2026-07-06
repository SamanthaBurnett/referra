package com.sbproject.referra.controller;

import com.sbproject.referra.dto.RoleRequest;
import com.sbproject.referra.dto.UserResponse;
import com.sbproject.referra.service.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final CurrentUserService currentUserService;

    public UserController(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    @GetMapping("/api/users/me")
    public UserResponse getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        return currentUserService.getCurrentUserResponse(jwt);
    }

    @PostMapping("/api/users/me/role")
    public UserResponse onboardRole(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody RoleRequest request
    ) {
        return currentUserService.addRole(jwt, request.role());
    }
}
