package com.sbproject.referra.dto;

import com.sbproject.referra.model.UserRole;
import jakarta.validation.constraints.NotNull;

public record RoleRequest(
        @NotNull
        UserRole role
) {
}
