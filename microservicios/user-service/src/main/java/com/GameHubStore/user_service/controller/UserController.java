package com.GameHubStore.user_service.controller;

import com.GameHubStore.user_service.model.dto.UserRequest;
import com.GameHubStore.user_service.model.dto.UserResponse;
import com.GameHubStore.user_service.model.dto.UserUpdateRequest;
import com.GameHubStore.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Crear usuario
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse addUser(@Valid @RequestBody UserRequest request){
        return userService.addUser(request);
    }

    // Listar todos los usuarios
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Iterable<UserResponse> getAllUsers(){
        return userService.getAllUsers();
    }

    // Listar usuario por id
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUserById(@PathVariable Long id){
        return userService.getUserById(id);
    }

    // Actualizar usuario
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        return userService.updateUser(id, request);
    }

    // Desactivar usuario
    @PatchMapping("/{id}/desactivar")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse desactivarUser(@PathVariable Long id){
        return userService.desactivarUser(id);
    }
}
