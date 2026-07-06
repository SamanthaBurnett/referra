package com.sbproject.referra.dto;

import com.sbproject.referra.model.User;
import com.sbproject.referra.model.UserRole;
import com.sbproject.referra.model.UserStatus;

import java.util.Set;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        UserStatus status,
        Set<UserRole> roles
) {
    public static UserResponse from(User user, Set<UserRole> roles) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getStatus(),
                roles
        );
    }
}
