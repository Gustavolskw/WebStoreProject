package com.ms.auth_service.validation;

import com.ms.auth_service.dto.UserRegisterDTO;
import com.ms.auth_service.dto.UserUpdateDTO;
import com.ms.auth_service.exception.ValidationException;
import org.springframework.stereotype.Service;

@Service
public class UserVal<T> implements UserValidation<Object>{


        @Override
        public void hasData(Object userDTO) {
            if (userDTO instanceof UserRegisterDTO) {
                UserRegisterDTO userRegisterDTO = (UserRegisterDTO) userDTO;
                validateData(userRegisterDTO.email(), userRegisterDTO.password(), userRegisterDTO.username());
            } else {
                throw new ValidationException("Invalid DTO type");
            }
        }

        @Override
        public void hasDataForUpdate(Object userDTO) {
            if (userDTO instanceof UserUpdateDTO) {
                UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userDTO;
                validateData(userUpdateDTO.email(), userUpdateDTO.password(), userUpdateDTO.username());
            } else {
                throw new ValidationException("Invalid DTO type");
            }
        }

        private void validateData(String email, String password, String username) {
            if (email == null || email.trim().isEmpty()) {
                throw new ValidationException("Email is empty");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new ValidationException("Password is empty");
            }
            if (username == null || username.trim().isEmpty()) {
                throw new ValidationException("Username is empty");
            }
        }
    }


