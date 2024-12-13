package com.ms.auth_service.service;

import com.ms.auth_service.dto.UserRegisterDTO;
import com.ms.auth_service.dto.UserUpdateDTO;
import com.ms.auth_service.exception.AlreadyExistsException;
import com.ms.auth_service.exception.ValidationException;
import com.ms.auth_service.model.Role;
import com.ms.auth_service.model.User;
import com.ms.auth_service.repository.RoleRepository;
import com.ms.auth_service.repository.UserRepository;
import com.ms.auth_service.validation.UserValidation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> userIdArgumentCaptor;

    @Mock
    private UserValidation userValidation;  // Mock da validação de usuário

    @Mock
    private PasswordEncoder passwordEncoder;  // Mock do PasswordEncoder
    @Nested
    class CreateUser {

        @Test
        @DisplayName("ShouldThrowErrorWhenRoleDoesntExist")
        void shouldThrowErrorWhenRoleDoesntExist() {

            lenient().doThrow(new RuntimeException()).when(userRepository).save(any(User.class));

            var input = new UserRegisterDTO("Gustavo@email.com", "Gustavo Luis", "1234567", 1L);
            // Act: Chama o método para registrar o usuário
            assertThrows(RuntimeException.class, ()->userService.registerUser(input));
        }

        @Test
        @DisplayName("ShouldNotCreateUserWithMissingData")
        void shouldNotCreateUserWithMissingData() {
            // Criação da role mockada com ID 1
            Role role = new Role(1L, "ADMIN", 100, true);
            when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

            lenient().doThrow(new RuntimeException()).when(userRepository).save(any(User.class));

            var input = new UserRegisterDTO("" ,"Gustavo Luis", "1234567", 1L);

            // Assert: Verifica se o usuário foi criado com sucesso
            assertThrows(RuntimeException.class, ()->userService.registerUser(input));

        }

        @Test
        @DisplayName("ShouldCreateUserWithSuccess")
        void shouldCreateUser() {
            // Criação da role mockada com ID 1
            Role role = new Role(1L, "ADMIN", 100, true);
            when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

            // Simulação do comportamento do PasswordEncoder
            when(passwordEncoder.encode("1234567")).thenReturn("encoded_password");

            // Criação do usuário com a role associada
            var user = new User(null, "Gustavo Luis", "encoded_password", "Gustavo@email.com", role, true);

            // Simula o comportamento de salvar o usuário no repositório
            doReturn(user).when(userRepository).save(userArgumentCaptor.capture());

            var input = new UserRegisterDTO("Gustavo@email.com", "Gustavo Luis", "1234567", 1L);

            // Act: Chama o método para registrar o usuário
            var output = userService.registerUser(input);

            // Assert: Verifica se o usuário foi criado com sucesso
            assertNotNull(output);
            assertEquals(input.username(), userArgumentCaptor.getValue().getUsername());

        }



    }

    @Nested
    class getUser{
        @Test
        @DisplayName("ShouldNotReturnUser")
        void shouldNotReturnUser() {

            var userId = "jivnfjinfi";
            doReturn(Optional.empty()).when(userRepository).findById(userIdArgumentCaptor.capture());

            // Chama o método para obter o usuário
            var output = userService.getUserById(userId);

            // Assert: Verifica se o usuário foi retornado corretamente
            assertTrue(output.isEmpty());  // Verifica se o resultado não é vazio
            assertEquals(userId, userIdArgumentCaptor.getValue());  // Verifica se o ID do usuário é o mesmo
        }


        @Test
        @DisplayName("ShouldReturnUser")
        void shouldReturnUser() {

            // Criação da role mockada com ID 1
            Role role = new Role(1L, "ADMIN", 100, true);

            // Criação do usuário com a role associada
            var user = new User("1", "Gustavo Luis", "encoded_password", "Gustavo@email.com", role, true);

            doReturn(Optional.of(user)).when(userRepository).findById(userIdArgumentCaptor.capture());

            // Chama o método para obter o usuário
            var output = userService.getUserById(user.getId());

            // Assert: Verifica se o usuário foi retornado corretamente
            assertTrue(output.isPresent());  // Verifica se o resultado não é vazio
            assertEquals(user.getId(), output.get().id());  // Verifica se o ID do usuário é o mesmo
        }
    }

    @Nested
    class listUsers {


        @Test
        @DisplayName("Should list Users With Success")
        void shouldListUsers() {
            // arrange
            Role role = new Role(1L, "ADMIN", 100, true);
            Pageable pageable = PageRequest.of(0, 10);
            var user = new User("1", "Gustavo Luis", "encoded_password", "Gustavo@email.com", role, true);

            // Simulando um Page com um único usuário
            Page<User> page = new PageImpl<>(List.of(user), pageable, 1);

            doReturn(page).when(userRepository).findAll(pageable);

            // act
            var output = userService.getAllUsers(pageable);

            // assert
            assertNotNull(output);
            assertEquals(1, output.getTotalElements());
        }

        @Test
        @DisplayName("Should list Users With Error")
        void shouldNotListUsers() {

            Pageable pageable = PageRequest.of(0, 10);

            // Simulando um Page com um único usuário
            Page<User> page = new PageImpl<>(Collections.emptyList(), pageable, 0);

            doReturn(page).when(userRepository).findAll(pageable);

            // act
            var output = userService.getAllUsers(pageable);

            // assert
            assertNotNull(output);
            assertEquals(0, output.getTotalElements());
        }

    }


    @Nested
    class updateUser {


        @Test
        @DisplayName("Should Update User With Success")
        void shouldUpdateUser() {
            // Arrange: Mock Role and User
            Role role = new Role(1L, "ADMIN", 100, true);
            doReturn(Optional.of(role)).when(roleRepository).findById(1L);

            // Initial user to update
            var user = new User("1", "Gustavo", "encoded_password", "Gustavo@email.com", role, true);
            doReturn(Optional.of(user)).when(userRepository).findById(user.getId());



            // Update DTO
            var updateUser = new UserUpdateDTO("Update@email.com", "Jigaboo", "123456", role.getId());
            
            // Mocking user repository for email check: we expect an email "Update@email.com"
            // to be already in the repository
            doReturn(List.of(user)).when(userRepository).findAllByEmail(updateUser.email());
            // Act: Call the update method
            var output = userService.updateUser(user.getId(), updateUser);

            // Assert: Verifying that the user was updated correctly
            assertNotNull(output);
            assertEquals(updateUser.email(), output.email());
            assertEquals("Jigaboo", output.username());  // Check that the username was updated
            assertEquals(true, user.getStatus());  // Ensure that the user status remains the same
            verify(userRepository).save(user);  // Ensure that save method was called
        }

        @Test
        @DisplayName("Should Not Update User When Email Already Exists")
        void shouldNotUpdateUser() {
            // Arrange: Mock Role and User
            Role role = new Role(1L, "ADMIN", 100, true);
            lenient().doReturn(Optional.of(role)).when(roleRepository).findById(1L);

            // Initial user to update
            var user = new User("1", "Gustavo", "encoded_password", "Gustavo@email.com", role, true);
            var user2 = new User("2", "Gustavo", "encoded_password", "Gustavo2@email.com", role, true);

            // Mock the user repository to return the user
            lenient().doReturn(Optional.of(user)).when(userRepository).findById(user.getId());

            // Update DTO: Trying to update to an existing email
            var updateUser = new UserUpdateDTO("Gustavo2@email.com", "Jigaboo", "123456", role.getId());

            // Mocking user repository to return two users with the same email
            ArrayList<User> listadeuser = new ArrayList<>();
            listadeuser.add(user);  // existing user
            listadeuser.add(user2);  // another user with the same email as the one being updated
            lenient().doReturn(listadeuser).when(userRepository).findAllByEmail(updateUser.email());

            // Act & Assert: Ensure that the exception is thrown when attempting to update
            assertThrows(AlreadyExistsException.class, () -> userService.updateUser(user.getId(), updateUser));

            // Ensure that the save method was NOT called because the email already exists
            verify(userRepository, times(0)).save(user);
        }

    }


    @Nested
    class deleteUser {


        @Test
        @DisplayName("Should deactivate the user with success")
        void shouldDeactivateUser() {
            // Criação da role mockada com ID 1
            Role role = new Role(1L, "ADMIN", 100, true);

            // Criação do usuário com a role associada
            var user = new User("1", "Gustavo Luis", "encoded_password", "Gustavo@email.com", role, true);

            doReturn(Optional.of(user)).when(userRepository).findById(userIdArgumentCaptor.capture());

            userService.inacivateUser(user.getId());


            var output = userService.getUserById(user.getId());

            // Assert: Verifica se o usuário foi retornado corretamente
            assertTrue(output.isPresent());  // Verifica se o resultado não é vazio
            assertEquals(user.getId(), output.get().id());  // Verifica se o ID do usuário é o mesmo
            assertEquals(false, user.getStatus());
            verify(userRepository, times(1)).save(user);
        }


        @Test
        @DisplayName("Should Not deactivate the user with success")
        void shouldNotDeactivateUser() {

          assertThrows(RuntimeException.class, ()-> userService.inacivateUser(null));
          verify(userRepository, never()).save(any(User.class));
        }
    }


}