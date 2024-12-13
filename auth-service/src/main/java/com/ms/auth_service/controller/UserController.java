package com.ms.auth_service.controller;

import com.ms.auth_service.dto.ApiResponse;
import com.ms.auth_service.dto.UserDataDTO;
import com.ms.auth_service.dto.UserRegisterDTO;
import com.ms.auth_service.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/user")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping()
    public ResponseEntity<ApiResponse> createUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        try{
            return ResponseEntity.ok().body(new ApiResponse("Sucesso", userService.registerUser(userRegisterDTO)));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new ApiResponse("Error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllUsers(@PageableDefault(size =20)Pageable pageable) {
        try{
            Page<UserDataDTO> usersList = userService.getAllUsers(pageable);
            Map<String, Object> data = new HashMap<>();
            data.put("pagamentos", usersList.getContent());
            data.put("total", usersList.getTotalElements());
            data.put("current_page", usersList.getNumber() + 1);
            data.put("last_page", usersList.getTotalPages());
            data.put("per_page", usersList.getSize());
            return ResponseEntity.ok().body(new ApiResponse("Sucesso!", data));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new ApiResponse("Error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable("id") String id) {
        try{
            return ResponseEntity.ok().body(new ApiResponse("Sucesso",userService.getUserById(id).isPresent()?userService.getUserById(id):"Sem Usuarios Registrados com esse Id:"+id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse("Error", e.getMessage()));
        }
    }
}
