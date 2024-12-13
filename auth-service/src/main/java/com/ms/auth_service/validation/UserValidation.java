package com.ms.auth_service.validation;

import com.ms.auth_service.dto.UserRegisterDTO;
import com.ms.auth_service.dto.UserUpdateDTO;
import org.springframework.context.annotation.Bean;

public interface UserValidation<T> {

    void hasData(T userRegisterDTO);
    void hasDataForUpdate(T userRegisterDTO);
}
