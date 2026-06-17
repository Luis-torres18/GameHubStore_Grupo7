package com.GameHubStore.user_service.controller;

import com.GameHubStore.user_service.model.dto.UserRequest;
import com.GameHubStore.user_service.model.dto.UserResponse;
import com.GameHubStore.user_service.model.dto.UserUpdateRequest;
import com.GameHubStore.user_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "Users v1", description = "Metodos CRUD para gestionar usuarios")
public class UserController {

    private final UserService userService;

    // Crear usuario
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Crear usuario",
            description = "Crea un nuevo usuario")
    @ApiResponses(
            value ={
                    @ApiResponse(
                            responseCode = "201",
                            description = "Usuario creado exitosamente"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Solicitud inválida"),
            }
    )
    public UserResponse addUser(@Valid @RequestBody UserRequest request){
        return userService.addUser(request);
    }

    // Listar todos los usuarios
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Listar todos los usuarios",
            description = "Obtiene una lista de todos los usuarios")
    @ApiResponses(
            value ={
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de usuarios obtenida exitosamente"),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor")
            }
    )
    public Iterable<UserResponse> getAllUsers(){
        return userService.getAllUsers();
    }

    // Listar usuario por id
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Obtener usuario por id",
            description = "Obtiene un usuario por su id")
    @ApiResponses(
            value ={
                    @ApiResponse(
                            responseCode = "200",
                            description = "Usuario obtenido exitosamente"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuario no encontrado")
            }
    )
    public UserResponse getUserById(@PathVariable Long id){
        return userService.getUserById(id);
    }

    // Actualizar usuario
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Actualizar usuario",
            description = "Actualiza un usuario existente")
    @ApiResponses(
            value ={
                    @ApiResponse(
                            responseCode = "200",
                            description = "Usuario actualizado exitosamente"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuario no encontrado")
            }
    )
    public UserResponse updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        return userService.updateUser(id, request);
    }

    // Desactivar usuario
    @PatchMapping("/{id}/desactivar")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Desactivar usuario",
            description = "Desactiva un usuario existente")
    @ApiResponses(
            value ={
                    @ApiResponse(
                            responseCode = "200",
                            description = "Usuario desactivado exitosamente"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuario no encontrado")
            }
    )
    public UserResponse desactivarUser(@PathVariable Long id){
        return userService.desactivarUser(id);
    }
}
