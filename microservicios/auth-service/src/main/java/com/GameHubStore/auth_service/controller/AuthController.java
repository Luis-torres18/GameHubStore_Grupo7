package com.GameHubStore.auth_service.controller;

import com.GameHubStore.auth_service.model.dto.*;
import com.GameHubStore.auth_service.model.entities.Rol;
import com.GameHubStore.auth_service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Metodo POST para registrar cuentas nuevas
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Registrar una nueva cuenta",
            description = "Permite registrar una cuenta nueva en el sistema"
    )
    @ApiResponses(
            value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Cuenta registrada exitosamente"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud invalida"),
    })
    public CuentaResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    // Metodo POST para iniciar sesion
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Iniciar sesion",
            description = "Permite iniciar sesion en el sistema"
    )
    @ApiResponses(
            value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sesion iniciada exitosamente"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud invalida"),
    })
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    // Metodo GET para listar todas las cuentas y poder filtrar por rol y/o estado
    @GetMapping("/cuentas")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Listar cuentas",
            description = "Permite listar todas las cuentas y poder filtrar por rol y/o estado"
    )
    @ApiResponses(
            value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cuentas listadas exitosamente"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud invalida"),
    })
    public List<CuentaResponse> getCuentas(
            @RequestParam(required = false) Rol rol,
            @RequestParam(required = false) Boolean estado) {

        if (rol != null && estado != null) {
            return authService.getCuentasByRolAndEstado(rol, estado);
        }
        if (rol != null) {
            return authService.getCuentasByRol(rol);
        }
        if (estado != null) {
            return authService.getCuentasByEstado(estado);
        }
        return authService.getAllCuentas();
    }

    // Metodo GET para obtener una cuenta por su ID
    @GetMapping("/cuentas/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Obtener cuenta por ID",
            description = "Permite obtener una cuenta por su ID"
    )
    @ApiResponses(
            value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cuenta obtenida exitosamente"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud invalida"),
    })
    public CuentaResponse getCuentaById(@PathVariable Long id) {
        return authService.getCuentaById(id);
    }

    // Metodo GET para obtener una cuenta por su email
    @GetMapping("/cuentas/email/{email}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Obtener cuenta por email",
            description = "Permite obtener una cuenta por su email"
    )
    @ApiResponses(
            value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cuenta obtenida exitosamente"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud invalida"),
    })
    public CuentaResponse getCuentaByEmail(@PathVariable String email) {
        return authService.getCuentaByEmail(email);
    }

    // Metodo PUT para actualizar una cuenta por su ID
    // Puede actualizar la contraseña, rol o estado de la cuenta
    @PutMapping("/cuentas/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Actualizar cuenta por su ID",
            description = "Permite actualizar una cuenta por su ID"
    )
    @ApiResponses(
            value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cuenta actualizada exitosamente"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud invalida"),
    })
    public CuentaResponse updateCuenta(
            @PathVariable Long id,
            @Valid @RequestBody CuentaUpdateRequest request) {
        return authService.updateCuenta(id, request);
    }

    // Metodo PATCH para desactivar una cuenta por su ID
    @PatchMapping("/cuentas/{id}/deactivate")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Desactivar cuenta por su ID",
            description = "Permite desactivar una cuenta por su ID"
    )
    @ApiResponses(
            value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cuenta desactivada exitosamente"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud invalida"),
    })
    public CuentaResponse deactivateCuenta(@PathVariable Long id) {
        return authService.deactivateCuenta(id);
    }

}