package com.sbproject.referra.dto;

import com.sbproject.referra.model.User;
import com.sbproject.referra.model.UserStatus;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        UserStatus status
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getStatus()
        );
    }
}
