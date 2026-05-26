package com.GameHubStore.user_service.repository;

import com.GameHubStore.user_service.model.entities.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DireccionRepository extends JpaRepository<Direccion, Long> {

    Optional<Direccion> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioId(Long usuarioId);
}
