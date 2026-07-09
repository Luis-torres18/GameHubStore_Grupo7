package com.GameHubStore.auth_service.controller;

import com.GameHubStore.auth_service.config.SecurityConfig;
import com.GameHubStore.auth_service.exception.BusinessException;
import com.GameHubStore.auth_service.exception.ResourceNotFoundException;
import com.GameHubStore.auth_service.model.dto.*;
import com.GameHubStore.auth_service.model.entities.Rol;
import com.GameHubStore.auth_service.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class) // usa la config de seguridad real (permitAll) en vez de la de default de @WebMvcTest
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private CuentaResponse cuentaResponse;

    @BeforeEach
    void setUp() {
        cuentaResponse = CuentaResponse.builder()
                .id(1L)
                .email("ana@mail.com")
                .rol(Rol.CLIENTE)
                .estado(true)
                .fechaCreacion(LocalDateTime.now())
                .build();
    }

    // ---------- register ----------

    @Test
    @DisplayName("POST /api/auth/register debe retornar 201 con un request valido")
    void register_deberiaRetornar201() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .email("ana@mail.com")
                .password("password123")
                .rol(Rol.CLIENTE)
                .build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(cuentaResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("ana@mail.com"));
    }

    @Test
    @DisplayName("POST /api/auth/register debe retornar 400 si el email es invalido")
    void register_deberiaRetornar400SiEmailInvalido() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .email("no-es-un-email")
                .password("password123")
                .rol(Rol.CLIENTE)
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    @DisplayName("POST /api/auth/register debe retornar 400 si la password es muy corta")
    void register_deberiaRetornar400SiPasswordCorta() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .email("ana@mail.com")
                .password("123")
                .rol(Rol.CLIENTE)
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/register debe retornar 422 si el email ya existe")
    void register_deberiaRetornar422SiEmailYaExiste() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .email("ana@mail.com")
                .password("password123")
                .rol(Rol.CLIENTE)
                .build();

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new BusinessException("Ya existe una cuenta con el email 'ana@mail.com'"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    // ---------- login ----------

    @Test
    @DisplayName("POST /api/auth/login debe retornar 200 con token cuando las credenciales son validas")
    void login_deberiaRetornar200() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("ana@mail.com")
                .password("password123")
                .build();

        LoginResponse loginResponse = LoginResponse.builder()
                .token("token-jwt-simulado")
                .tipo("Bearer")
                .id(1L)
                .email("ana@mail.com")
                .rol(Rol.CLIENTE)
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-jwt-simulado"))
                .andExpect(jsonPath("$.tipo").value("Bearer"));
    }

    @Test
    @DisplayName("POST /api/auth/login debe retornar 422 si las credenciales son invalidas")
    void login_deberiaRetornar422SiCredencialesInvalidas() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("ana@mail.com")
                .password("incorrecta")
                .build();

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BusinessException("Credenciales inválidas"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("POST /api/auth/login debe retornar 400 si el body es invalido")
    void login_deberiaRetornar400SiBodyInvalido() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("")
                .password("")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    // ---------- listar cuentas ----------

    @Test
    @DisplayName("GET /api/auth/cuentas sin filtros debe retornar todas las cuentas")
    void getCuentas_sinFiltros_deberiaRetornarTodas() throws Exception {
        when(authService.getAllCuentas()).thenReturn(List.of(cuentaResponse));

        mockMvc.perform(get("/api/auth/cuentas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("ana@mail.com"));

        verify(authService).getAllCuentas();
    }

    @Test
    @DisplayName("GET /api/auth/cuentas?rol=X debe filtrar por rol")
    void getCuentas_conRol_deberiaFiltrarPorRol() throws Exception {
        when(authService.getCuentasByRol(Rol.CLIENTE)).thenReturn(List.of(cuentaResponse));

        mockMvc.perform(get("/api/auth/cuentas").param("rol", "CLIENTE"))
                .andExpect(status().isOk());

        verify(authService).getCuentasByRol(Rol.CLIENTE);
        verify(authService, never()).getAllCuentas();
    }

    @Test
    @DisplayName("GET /api/auth/cuentas?estado=true debe filtrar por estado")
    void getCuentas_conEstado_deberiaFiltrarPorEstado() throws Exception {
        when(authService.getCuentasByEstado(true)).thenReturn(List.of(cuentaResponse));

        mockMvc.perform(get("/api/auth/cuentas").param("estado", "true"))
                .andExpect(status().isOk());

        verify(authService).getCuentasByEstado(true);
    }

    @Test
    @DisplayName("GET /api/auth/cuentas?rol=X&estado=true debe filtrar por ambos")
    void getCuentas_conRolYEstado_deberiaFiltrarPorAmbos() throws Exception {
        when(authService.getCuentasByRolAndEstado(Rol.CLIENTE, true)).thenReturn(List.of(cuentaResponse));

        mockMvc.perform(get("/api/auth/cuentas")
                        .param("rol", "CLIENTE")
                        .param("estado", "true"))
                .andExpect(status().isOk());

        verify(authService).getCuentasByRolAndEstado(Rol.CLIENTE, true);
    }

    // ---------- obtener por id / email ----------

    @Test
    @DisplayName("GET /api/auth/cuentas/{id} debe retornar 200 cuando existe")
    void getCuentaById_deberiaRetornar200() throws Exception {
        when(authService.getCuentaById(1L)).thenReturn(cuentaResponse);

        mockMvc.perform(get("/api/auth/cuentas/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("GET /api/auth/cuentas/{id} debe retornar 404 cuando no existe")
    void getCuentaById_deberiaRetornar404SiNoExiste() throws Exception {
        when(authService.getCuentaById(99L))
                .thenThrow(new ResourceNotFoundException("Cuenta no encontrada con id=99"));

        mockMvc.perform(get("/api/auth/cuentas/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/auth/cuentas/email/{email} debe retornar 200 cuando existe")
    void getCuentaByEmail_deberiaRetornar200() throws Exception {
        when(authService.getCuentaByEmail("ana@mail.com")).thenReturn(cuentaResponse);

        mockMvc.perform(get("/api/auth/cuentas/email/{email}", "ana@mail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ana@mail.com"));
    }

    @Test
    @DisplayName("GET /api/auth/cuentas/email/{email} debe retornar 404 cuando no existe")
    void getCuentaByEmail_deberiaRetornar404SiNoExiste() throws Exception {
        when(authService.getCuentaByEmail("noexiste@mail.com"))
                .thenThrow(new ResourceNotFoundException("Cuenta no encontrada con email=noexiste@mail.com"));

        mockMvc.perform(get("/api/auth/cuentas/email/{email}", "noexiste@mail.com"))
                .andExpect(status().isNotFound());
    }

    // ---------- actualizar ----------

    @Test
    @DisplayName("PUT /api/auth/cuentas/{id} debe retornar 200 con datos validos")
    void updateCuenta_deberiaRetornar200() throws Exception {
        CuentaUpdateRequest request = CuentaUpdateRequest.builder()
                .rol(Rol.OPERADOR)
                .build();

        CuentaResponse actualizado = CuentaResponse.builder()
                .id(1L).email("ana@mail.com").rol(Rol.OPERADOR).estado(true).build();

        when(authService.updateCuenta(eq(1L), any(CuentaUpdateRequest.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/auth/cuentas/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("OPERADOR"));
    }

    @Test
    @DisplayName("PUT /api/auth/cuentas/{id} debe retornar 400 si la password enviada es muy corta")
    void updateCuenta_deberiaRetornar400SiPasswordCorta() throws Exception {
        CuentaUpdateRequest request = CuentaUpdateRequest.builder()
                .password("123")
                .build();

        mockMvc.perform(put("/api/auth/cuentas/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    @DisplayName("PUT /api/auth/cuentas/{id} debe retornar 404 si la cuenta no existe")
    void updateCuenta_deberiaRetornar404SiNoExiste() throws Exception {
        when(authService.updateCuenta(eq(99L), any(CuentaUpdateRequest.class)))
                .thenThrow(new ResourceNotFoundException("Cuenta no encontrada con id=99"));

        mockMvc.perform(put("/api/auth/cuentas/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CuentaUpdateRequest())))
                .andExpect(status().isNotFound());
    }

    // ---------- desactivar ----------

    @Test
    @DisplayName("PATCH /api/auth/cuentas/{id}/deactivate debe retornar 200")
    void deactivateCuenta_deberiaRetornar200() throws Exception {
        CuentaResponse desactivada = CuentaResponse.builder()
                .id(1L).email("ana@mail.com").estado(false).build();

        when(authService.deactivateCuenta(1L)).thenReturn(desactivada);

        mockMvc.perform(patch("/api/auth/cuentas/{id}/deactivate", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value(false));
    }

    @Test
    @DisplayName("PATCH /api/auth/cuentas/{id}/deactivate debe retornar 404 si no existe")
    void deactivateCuenta_deberiaRetornar404SiNoExiste() throws Exception {
        when(authService.deactivateCuenta(99L))
                .thenThrow(new ResourceNotFoundException("Cuenta no encontrada con id=99"));

        mockMvc.perform(patch("/api/auth/cuentas/{id}/deactivate", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /api/auth/cuentas/{id}/deactivate debe retornar 422 si ya esta inactiva")
    void deactivateCuenta_deberiaRetornar422SiYaEstaInactiva() throws Exception {
        when(authService.deactivateCuenta(1L))
                .thenThrow(new BusinessException("La cuenta con id=1 ya se encuentra inactiva"));

        mockMvc.perform(patch("/api/auth/cuentas/{id}/deactivate", 1L))
                .andExpect(status().isUnprocessableEntity());
    }
}
