package com.GameHubStore.auth_service.service;

import com.GameHubStore.auth_service.exception.BusinessException;
import com.GameHubStore.auth_service.exception.ResourceNotFoundException;
import com.GameHubStore.auth_service.model.dto.*;
import com.GameHubStore.auth_service.model.entities.CuentaAcceso;
import com.GameHubStore.auth_service.model.entities.Rol;
import com.GameHubStore.auth_service.repository.CuentaAccesoRepository;
import com.GameHubStore.auth_service.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private CuentaAccesoRepository cuentaAccesoRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private AuthService authService;

    private CuentaAcceso cuentaActiva;

    @BeforeEach
    void setUp() {
        authService = new AuthService(cuentaAccesoRepository, jwtUtil, passwordEncoder);

        cuentaActiva = CuentaAcceso.builder()
                .id(1L)
                .email("ana@mail.com")
                .passwordHash("$2a$10$hashSimulado")
                .rol(Rol.CLIENTE)
                .estado(true)
                .fechaCreacion(LocalDateTime.now())
                .build();
    }

    // ---------- register ----------

    @Test
    @DisplayName("register debe crear la cuenta cuando el email no existe")
    void register_deberiaCrearCuenta() {
        RegisterRequest request = RegisterRequest.builder()
                .email("ana@mail.com")
                .password("password123")
                .rol(Rol.CLIENTE)
                .build();

        when(cuentaAccesoRepository.existsByEmail("ana@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashSimulado");
        when(cuentaAccesoRepository.save(any(CuentaAcceso.class))).thenReturn(cuentaActiva);

        CuentaResponse resultado = authService.register(request);

        assertThat(resultado.getEmail()).isEqualTo("ana@mail.com");
        assertThat(resultado.getRol()).isEqualTo(Rol.CLIENTE);

        ArgumentCaptor<CuentaAcceso> captor = ArgumentCaptor.forClass(CuentaAcceso.class);
        verify(cuentaAccesoRepository).save(captor.capture());
        assertThat(captor.getValue().getPasswordHash()).isEqualTo("$2a$10$hashSimulado");
        assertThat(captor.getValue().getEstado()).isTrue();
    }

    @Test
    @DisplayName("register debe lanzar BusinessException si el email ya existe")
    void register_deberiaLanzarExcepcionSiEmailYaExiste() {
        RegisterRequest request = RegisterRequest.builder()
                .email("ana@mail.com")
                .password("password123")
                .rol(Rol.CLIENTE)
                .build();

        when(cuentaAccesoRepository.existsByEmail("ana@mail.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ana@mail.com");

        verify(cuentaAccesoRepository, never()).save(any());
    }

    // ---------- login ----------

    @Test
    @DisplayName("login debe retornar token cuando las credenciales son validas")
    void login_deberiaRetornarTokenConCredencialesValidas() {
        LoginRequest request = LoginRequest.builder()
                .email("ana@mail.com")
                .password("password123")
                .build();

        when(cuentaAccesoRepository.findByEmail("ana@mail.com")).thenReturn(Optional.of(cuentaActiva));
        when(passwordEncoder.matches("password123", cuentaActiva.getPasswordHash())).thenReturn(true);
        when(jwtUtil.generateToken("ana@mail.com", "CLIENTE")).thenReturn("token-jwt-simulado");

        LoginResponse resultado = authService.login(request);

        assertThat(resultado.getToken()).isEqualTo("token-jwt-simulado");
        assertThat(resultado.getTipo()).isEqualTo("Bearer");
        assertThat(resultado.getEmail()).isEqualTo("ana@mail.com");
        assertThat(resultado.getRol()).isEqualTo(Rol.CLIENTE);
    }

    @Test
    @DisplayName("login debe lanzar BusinessException si el email no existe")
    void login_deberiaLanzarExcepcionSiEmailNoExiste() {
        LoginRequest request = LoginRequest.builder()
                .email("noexiste@mail.com")
                .password("password123")
                .build();

        when(cuentaAccesoRepository.findByEmail("noexiste@mail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Credenciales inválidas");

        verifyNoInteractions(jwtUtil);
    }

    @Test
    @DisplayName("login debe lanzar BusinessException si la cuenta esta inactiva")
    void login_deberiaLanzarExcepcionSiCuentaInactiva() {
        cuentaActiva.setEstado(false);
        LoginRequest request = LoginRequest.builder()
                .email("ana@mail.com")
                .password("password123")
                .build();

        when(cuentaAccesoRepository.findByEmail("ana@mail.com")).thenReturn(Optional.of(cuentaActiva));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("inactiva");

        verify(passwordEncoder, never()).matches(any(), any());
        verifyNoInteractions(jwtUtil);
    }

    @Test
    @DisplayName("login debe lanzar BusinessException si la contraseña no coincide")
    void login_deberiaLanzarExcepcionSiPasswordNoCoincide() {
        LoginRequest request = LoginRequest.builder()
                .email("ana@mail.com")
                .password("passwordIncorrecta")
                .build();

        when(cuentaAccesoRepository.findByEmail("ana@mail.com")).thenReturn(Optional.of(cuentaActiva));
        when(passwordEncoder.matches("passwordIncorrecta", cuentaActiva.getPasswordHash())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Credenciales inválidas");

        verifyNoInteractions(jwtUtil);
    }

    // ---------- listados ----------

    @Test
    @DisplayName("getAllCuentas debe retornar todas las cuentas mapeadas")
    void getAllCuentas_deberiaRetornarTodasLasCuentas() {
        when(cuentaAccesoRepository.findAll()).thenReturn(List.of(cuentaActiva));

        List<CuentaResponse> resultado = authService.getAllCuentas();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEmail()).isEqualTo("ana@mail.com");
    }

    @Test
    @DisplayName("getCuentasByRol debe filtrar por rol")
    void getCuentasByRol_deberiaFiltrarPorRol() {
        when(cuentaAccesoRepository.findByRol(Rol.CLIENTE)).thenReturn(List.of(cuentaActiva));

        List<CuentaResponse> resultado = authService.getCuentasByRol(Rol.CLIENTE);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getRol()).isEqualTo(Rol.CLIENTE);
    }

    @Test
    @DisplayName("getCuentasByEstado debe filtrar por estado")
    void getCuentasByEstado_deberiaFiltrarPorEstado() {
        when(cuentaAccesoRepository.findByEstado(true)).thenReturn(List.of(cuentaActiva));

        List<CuentaResponse> resultado = authService.getCuentasByEstado(true);

        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("getCuentasByRolAndEstado debe filtrar por ambos criterios")
    void getCuentasByRolAndEstado_deberiaFiltrarPorAmbos() {
        when(cuentaAccesoRepository.findByRolAndEstado(Rol.CLIENTE, true)).thenReturn(List.of(cuentaActiva));

        List<CuentaResponse> resultado = authService.getCuentasByRolAndEstado(Rol.CLIENTE, true);

        assertThat(resultado).hasSize(1);
    }

    // ---------- getCuentaById / getCuentaByEmail ----------

    @Test
    @DisplayName("getCuentaById debe retornar la cuenta cuando existe")
    void getCuentaById_deberiaRetornarCuenta() {
        when(cuentaAccesoRepository.findById(1L)).thenReturn(Optional.of(cuentaActiva));

        CuentaResponse resultado = authService.getCuentaById(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("getCuentaById debe lanzar ResourceNotFoundException si no existe")
    void getCuentaById_deberiaLanzarExcepcionSiNoExiste() {
        when(cuentaAccesoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.getCuentaById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("getCuentaByEmail debe retornar la cuenta cuando existe")
    void getCuentaByEmail_deberiaRetornarCuenta() {
        when(cuentaAccesoRepository.findByEmail("ana@mail.com")).thenReturn(Optional.of(cuentaActiva));

        CuentaResponse resultado = authService.getCuentaByEmail("ana@mail.com");

        assertThat(resultado.getEmail()).isEqualTo("ana@mail.com");
    }

    @Test
    @DisplayName("getCuentaByEmail debe lanzar ResourceNotFoundException si no existe")
    void getCuentaByEmail_deberiaLanzarExcepcionSiNoExiste() {
        when(cuentaAccesoRepository.findByEmail("noexiste@mail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.getCuentaByEmail("noexiste@mail.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ---------- updateCuenta ----------

    @Test
    @DisplayName("updateCuenta debe actualizar solo el rol si es lo unico enviado")
    void updateCuenta_deberiaActualizarSoloRol() {
        when(cuentaAccesoRepository.findById(1L)).thenReturn(Optional.of(cuentaActiva));
        when(cuentaAccesoRepository.save(any(CuentaAcceso.class))).thenAnswer(inv -> inv.getArgument(0));

        CuentaUpdateRequest request = CuentaUpdateRequest.builder()
                .rol(Rol.ADMINISTRADOR)
                .build();

        CuentaResponse resultado = authService.updateCuenta(1L, request);

        assertThat(resultado.getRol()).isEqualTo(Rol.ADMINISTRADOR);
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    @DisplayName("updateCuenta debe re-encriptar la contraseña si se envia una nueva")
    void updateCuenta_deberiaActualizarPassword() {
        when(cuentaAccesoRepository.findById(1L)).thenReturn(Optional.of(cuentaActiva));
        when(passwordEncoder.encode("nuevaPassword123")).thenReturn("$2a$10$nuevoHash");
        when(cuentaAccesoRepository.save(any(CuentaAcceso.class))).thenAnswer(inv -> inv.getArgument(0));

        CuentaUpdateRequest request = CuentaUpdateRequest.builder()
                .password("nuevaPassword123")
                .build();

        authService.updateCuenta(1L, request);

        ArgumentCaptor<CuentaAcceso> captor = ArgumentCaptor.forClass(CuentaAcceso.class);
        verify(cuentaAccesoRepository).save(captor.capture());
        assertThat(captor.getValue().getPasswordHash()).isEqualTo("$2a$10$nuevoHash");
    }

    @Test
    @DisplayName("updateCuenta debe lanzar ResourceNotFoundException si no existe")
    void updateCuenta_deberiaLanzarExcepcionSiNoExiste() {
        when(cuentaAccesoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.updateCuenta(99L, new CuentaUpdateRequest()))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(cuentaAccesoRepository, never()).save(any());
    }

    // ---------- deactivateCuenta ----------

    @Test
    @DisplayName("deactivateCuenta debe cambiar el estado a false")
    void deactivateCuenta_deberiaDesactivarCuenta() {
        when(cuentaAccesoRepository.findById(1L)).thenReturn(Optional.of(cuentaActiva));
        when(cuentaAccesoRepository.save(any(CuentaAcceso.class))).thenAnswer(inv -> inv.getArgument(0));

        CuentaResponse resultado = authService.deactivateCuenta(1L);

        assertThat(resultado.getEstado()).isFalse();
    }

    @Test
    @DisplayName("deactivateCuenta debe lanzar BusinessException si ya esta inactiva")
    void deactivateCuenta_deberiaLanzarExcepcionSiYaEstaInactiva() {
        cuentaActiva.setEstado(false);
        when(cuentaAccesoRepository.findById(1L)).thenReturn(Optional.of(cuentaActiva));

        assertThatThrownBy(() -> authService.deactivateCuenta(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ya se encuentra inactiva");

        verify(cuentaAccesoRepository, never()).save(any());
    }

    @Test
    @DisplayName("deactivateCuenta debe lanzar ResourceNotFoundException si no existe")
    void deactivateCuenta_deberiaLanzarExcepcionSiNoExiste() {
        when(cuentaAccesoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.deactivateCuenta(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
