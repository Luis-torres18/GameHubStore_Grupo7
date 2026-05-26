package com.GameHubStore.user_service.service;

import com.GameHubStore.user_service.model.entities.Rol;
import com.GameHubStore.user_service.model.dto.DireccionRequest;
import com.GameHubStore.user_service.exception.BusinessException;
import com.GameHubStore.user_service.exception.ResourceNotFoundException;
import com.GameHubStore.user_service.model.dto.DireccionResponse;
import com.GameHubStore.user_service.model.dto.UserRequest;
import com.GameHubStore.user_service.model.dto.UserResponse;
import com.GameHubStore.user_service.model.dto.UserUpdateRequest;
import com.GameHubStore.user_service.model.entities.Direccion;
import com.GameHubStore.user_service.model.entities.User;
import com.GameHubStore.user_service.repository.DireccionRepository;
import com.GameHubStore.user_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final DireccionRepository direccionRepository;

    @Transactional
    public UserResponse addUser(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(
                    "Ya existe un usuario con el correo: " + request.getEmail());
        }

        User user = User.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .rol(request.getRol())
                .estado(request.getEstado() != null ? request.getEstado() : true)
                .build();

        user = userRepository.save(user);

        Direccion direccion = Direccion.builder()
                .usuarioId(user.getId())
                .comuna(request.getDireccion().getComuna())
                .ciudad(request.getDireccion().getCiudad())
                .calle(request.getDireccion().getCalle())
                .numero(request.getDireccion().getNumero())
                .build();

        direccion = direccionRepository.save(direccion);
        log.info("Usuario creado: {} (id={})", user.getNombre(), user.getId());
        return mapToResponse(user, direccion);
    }

    // Listar todos los usuarios
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> mapToResponse(user, getDireccion(user.getId())))
                .toList();
    }

    // Listar usuarios por rol
    public List<UserResponse> getUsersByRol(Rol rol) {
        return userRepository.findByRol(rol)
                .stream()
                .map(user -> mapToResponse(user, getDireccion(user.getId())))
                .toList();
    }

    // Listar usuario por ID
    public UserResponse getUserById(Long id) {
        User user = findUserOrThrow(id);
        return mapToResponse(user, getDireccion(id));
    }

    // Actualizar usuario
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = findUserOrThrow(id);

        if (request.getNombre() != null && !request.getNombre().isBlank()) {
            user.setNombre(request.getNombre());
        }
        if (request.getTelefono() != null) {
            user.setTelefono(request.getTelefono());
        }

        user = userRepository.save(user);

        // Actualizar dirección si viene en el request
        Direccion direccion = getDireccion(id);
        if (request.getDireccion() != null) {
            DireccionRequest dir = request.getDireccion();
            if (direccion != null) {
                direccion.setComuna(dir.getComuna());
                direccion.setCiudad(dir.getCiudad());
                direccion.setCalle(dir.getCalle());
                direccion.setNumero(dir.getNumero());
            } else {
                direccion = Direccion.builder()
                        .usuarioId(id)
                        .comuna(dir.getComuna())
                        .ciudad(dir.getCiudad())
                        .calle(dir.getCalle())
                        .numero(dir.getNumero())
                        .build();
            }
            direccion = direccionRepository.save(direccion);
        }

        log.info("Usuario actualizado: {} (id={})", user.getNombre(), user.getId());
        return mapToResponse(user, direccion);
    }

    // Desactivar usuario
    public UserResponse desactivarUser(Long id) {
        User user = findUserOrThrow(id);

        if (Boolean.FALSE.equals(user.getEstado())) {
            throw new BusinessException("El usuario con id=" + id + " ya se encuentra inactivo");
        }

        user.setEstado(false);
        user = userRepository.save(user);
        log.warn("Usuario desactivado: {} (id={})", user.getNombre(), user.getId());
        return mapToResponse(user, getDireccion(id));
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado con id=" + id));
    }

    private Direccion getDireccion(Long usuarioId) {
        return direccionRepository.findByUsuarioId(usuarioId).orElse(null);
    }

    private UserResponse mapToResponse(User user, Direccion direccion) {
        DireccionResponse direccionResponse = null;
        if (direccion != null) {
            direccionResponse = DireccionResponse.builder()
                    .id(direccion.getId())
                    .usuarioId(direccion.getUsuarioId())
                    .comuna(direccion.getComuna())
                    .ciudad(direccion.getCiudad())
                    .calle(direccion.getCalle())
                    .numero(direccion.getNumero())
                    .build();
        }

        return UserResponse.builder()
                .id(user.getId())
                .nombre(user.getNombre())
                .email(user.getEmail())
                .telefono(user.getTelefono())
                .rol(user.getRol())
                .estado(user.getEstado())
                .direccion(direccionResponse)
                .build();
    }
}
