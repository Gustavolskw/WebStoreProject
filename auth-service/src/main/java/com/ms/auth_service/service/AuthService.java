package com.ms.auth_service.service;

import com.ms.auth_service.config.AuthUserDetails;
import com.ms.auth_service.dto.JwtResponse;
import com.ms.auth_service.dto.UserLoginDTO;
import com.ms.auth_service.jwt.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthService(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }


    public JwtResponse login(UserLoginDTO userLoginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginDTO.email(), userLoginDTO.password())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jtwToken = jwtUtils.generateTokenForUser(authentication);

        AuthUserDetails userDetails = (AuthUserDetails) authentication.getPrincipal();

        return new JwtResponse(userDetails.getId(), jtwToken);
    }




}
