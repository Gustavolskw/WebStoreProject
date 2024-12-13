package com.ms.auth_service.dto;

import jakarta.validation.constraints.NotBlank;

public record UserUpdateDTO(
        @NotBlank String email,
        @NotBlank String username,
        @NotBlank String password,
        Long role
) {
}
