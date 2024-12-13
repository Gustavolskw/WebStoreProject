package com.ms.auth_service.dto;

import com.ms.auth_service.model.User;

import java.util.UUID;

public record UserDataDTO(String id, String username, String email, Boolean status, String role, Integer roleLevel) {
    public UserDataDTO(User user) {
        this(user.getId(), user.getUsername(), user.getEmail(), user.getStatus(), user.getRole().getName(), user.getRole().getLevel());
    }
}
