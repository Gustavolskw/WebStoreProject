package com.ms.auth_service.dto;

import com.ms.auth_service.model.Role;
import jakarta.validation.constraints.NotBlank;

public record UserRegisterDTO(
        @NotBlank String email,
        @NotBlank String username,
        @NotBlank String password,
        Long role
) {
}
