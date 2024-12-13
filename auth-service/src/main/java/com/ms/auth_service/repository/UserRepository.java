package com.ms.auth_service.repository;

import com.ms.auth_service.model.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, String> {

    User findByEmail(String email);

    boolean existsByEmail(@NotBlank String email);

    List<User> findAllByEmail(@NotBlank String email);
}
