package com.ms.auth_service.controller;

import com.ms.auth_service.dto.ApiResponse;
import com.ms.auth_service.dto.JwtResponse;
import com.ms.auth_service.dto.UserLoginDTO;
import com.ms.auth_service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody @Valid UserLoginDTO userLoginDTO) {
        try{
            JwtResponse token = authService.login(userLoginDTO);
            return ResponseEntity.ok().body(new ApiResponse("Login Realizado",token ));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new ApiResponse("Login Error", e.getMessage()));
        }
    }

}
