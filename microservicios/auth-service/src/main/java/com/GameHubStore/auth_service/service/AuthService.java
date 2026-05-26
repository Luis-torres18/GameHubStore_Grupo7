package com.GameHubStore.auth_service.service;

import com.GameHubStore.auth_service.exception.BusinessException;
import com.GameHubStore.auth_service.exception.ResourceNotFoundException;
import com.GameHubStore.auth_service.model.dto.*;
import com.GameHubStore.auth_service.model.entities.CuentaAcceso;
import com.GameHubStore.auth_service.model.entities.Rol;
import com.GameHubStore.auth_service.repository.CuentaAccesoRepository;
import com.GameHubStore.auth_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final CuentaAccesoRepository cuentaAccesoRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    // ─── Crear cuenta ─────────────────────────────────────────────────────────

    public CuentaResponse register(RegisterRequest request) {
        if (cuentaAccesoRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(
                    "Ya existe una cuenta con el email '" + request.getEmail() + "'");
        }

        CuentaAcceso cuenta = CuentaAcceso.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .rol(request.getRol())
                .estado(true)
                .build();

        cuenta = cuentaAccesoRepository.save(cuenta);
        log.info("Cuenta creada: {} con rol {}", cuenta.getEmail(), cuenta.getRol());
        return mapToResponse(cuenta);
    }

    // ─── Login ────────────────────────────────────────────────────────────────

    public LoginResponse login(LoginRequest request) {
        CuentaAcceso cuenta = cuentaAccesoRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Credenciales inválidas"));

        if (Boolean.FALSE.equals(cuenta.getEstado())) {
            throw new BusinessException("La cuenta está inactiva y no puede autenticarse");
        }

        if (!passwordEncoder.matches(request.getPassword(), cuenta.getPasswordHash())) {
            throw new BusinessException("Credenciales inválidas");
        }

        String token = jwtUtil.generateToken(cuenta.getEmail(), cuenta.getRol().name());
        log.info("Login exitoso: {}", cuenta.getEmail());

        return LoginResponse.builder()
                .token(token)
                .tipo("Bearer")
                .id(cuenta.getId())
                .email(cuenta.getEmail())
                .rol(cuenta.getRol())
                .build();
    }

    // ─── Listar cuentas ───────────────────────────────────────────────────────

    public List<CuentaResponse> getAllCuentas() {
        return cuentaAccesoRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<CuentaResponse> getCuentasByRol(Rol rol) {
        return cuentaAccesoRepository.findByRol(rol)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<CuentaResponse> getCuentasByEstado(Boolean estado) {
        return cuentaAccesoRepository.findByEstado(estado)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<CuentaResponse> getCuentasByRolAndEstado(Rol rol, Boolean estado) {
        return cuentaAccesoRepository.findByRolAndEstado(rol, estado)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ─── Buscar cuenta ────────────────────────────────────────────────────────

    public CuentaResponse getCuentaById(Long id) {
        return mapToResponse(findOrThrow(id));
    }

    public CuentaResponse getCuentaByEmail(String email) {
        return mapToResponse(cuentaAccesoRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cuenta no encontrada con email=" + email)));
    }

    // ─── Actualizar cuenta ────────────────────────────────────────────────────

    public CuentaResponse updateCuenta(Long id, CuentaUpdateRequest request) {
        CuentaAcceso cuenta = findOrThrow(id);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            cuenta.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getRol() != null) {
            cuenta.setRol(request.getRol());
        }
        if (request.getEstado() != null) {
            cuenta.setEstado(request.getEstado());
        }

        cuenta = cuentaAccesoRepository.save(cuenta);
        log.info("Cuenta actualizada: {} (id={})", cuenta.getEmail(), cuenta.getId());
        return mapToResponse(cuenta);
    }

    // ─── Desactivar cuenta ────────────────────────────────────────────────────

    public CuentaResponse deactivateCuenta(Long id) {
        CuentaAcceso cuenta = findOrThrow(id);

        if (Boolean.FALSE.equals(cuenta.getEstado())) {
            throw new BusinessException("La cuenta con id=" + id + " ya se encuentra inactiva");
        }

        cuenta.setEstado(false);
        cuenta = cuentaAccesoRepository.save(cuenta);
        log.warn("Cuenta desactivada: {} (id={})", cuenta.getEmail(), cuenta.getId());
        return mapToResponse(cuenta);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private CuentaAcceso findOrThrow(Long id) {
        return cuentaAccesoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cuenta no encontrada con id=" + id));
    }

    private CuentaResponse mapToResponse(CuentaAcceso cuenta) {
        return CuentaResponse.builder()
                .id(cuenta.getId())
                .email(cuenta.getEmail())
                .rol(cuenta.getRol())
                .estado(cuenta.getEstado())
                .fechaCreacion(cuenta.getFechaCreacion())
                .build();
    }
}