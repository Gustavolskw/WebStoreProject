package com.ms.auth_service.service;

import com.ms.auth_service.dto.UserDataDTO;
import com.ms.auth_service.dto.UserRegisterDTO;
import com.ms.auth_service.dto.UserUpdateDTO;
import com.ms.auth_service.exception.AlreadyExistsException;
import com.ms.auth_service.exception.ValidationException;
import com.ms.auth_service.model.Role;
import com.ms.auth_service.model.User;
import com.ms.auth_service.repository.RoleRepository;
import com.ms.auth_service.repository.UserRepository;
import com.ms.auth_service.validation.UserValidation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserValidation<Object> userValidation;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, UserValidation<Object> userValidation) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userValidation = userValidation;
    }

    public Page<UserDataDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserDataDTO::new);
    }

    public Optional<UserDataDTO> getUserById(String id) {
        return userRepository.findById(id)
                .map(UserDataDTO::new);  // Converte User para DTO

    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public UserDataDTO updateUser(String id, UserUpdateDTO userDataDTO) {
        // Fetch user by ID
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Usuario de ID:" + id + ", não Existe"));

        // Validate the provided data (email, username, etc.)
        try {
            userValidation.hasDataForUpdate(userDataDTO);

            // Check if the email already exists
            List<User> usersByEmail = userRepository.findAllByEmail(userDataDTO.email());
            if (usersByEmail.size() > 1) {
                throw new AlreadyExistsException("Email ja Existe");
            }
            else if (usersByEmail.size() == 1 && !usersByEmail.get(0).getId().equals(id)) {
                // Prevent update if email already exists for another user
                throw new AlreadyExistsException("Email já existe para outro usuário");
            }

            // Update user information
            user.setEmail(userDataDTO.email());
            user.setUsername(userDataDTO.username());
            user.setPassword(passwordEncoder.encode(userDataDTO.password()));
            user.setRole(roleRepository.findById(userDataDTO.role())
                    .orElseThrow(() -> new ValidationException("Role Inexistente")));
            userRepository.save(user);

            return new UserDataDTO(user);

        } catch (ValidationException e) {
            throw new RuntimeException("Missing Arguments for User Creation: " + e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void inacivateUser(String userId){
        User user = userRepository.findById(userId).orElseThrow(()->new ValidationException("Usuario Inexistente"));
        user.setStatus(false);
        userRepository.save(user);
    }


    public UserDataDTO registerUser(UserRegisterDTO dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new AlreadyExistsException("User already exists");
        }

        try {
            // Validate user data
            userValidation.hasData(dto);

            // Build the user
            User newUser = buildUser(dto);

            // Save the user
            userRepository.save(newUser);

            // Return the created user data
            return new UserDataDTO(newUser);
        } catch (ValidationException e) {
            throw new RuntimeException("Missing Arguments for User Creation: " + e.getMessage());
        }
    }
    private User buildUser(UserRegisterDTO dto ){
        Role role = roleRepository.findById(dto.role()).orElseThrow(()->new RuntimeException("Role not found"));
        User user = new User();
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setUsername(dto.username());
        user.setRole(role);
        user.setStatus(true);
        return user;
    }


}
