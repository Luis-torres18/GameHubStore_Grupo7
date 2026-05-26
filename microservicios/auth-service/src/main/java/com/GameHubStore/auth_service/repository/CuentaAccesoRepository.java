package com.GameHubStore.auth_service.repository;

import com.GameHubStore.auth_service.model.entities.CuentaAcceso;
import com.GameHubStore.auth_service.model.entities.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CuentaAccesoRepository extends JpaRepository<CuentaAcceso, Long> {

    boolean existsByEmail(String email);

    Optional<CuentaAcceso> findByEmail(String email);

    List<CuentaAcceso> findByRol(Rol rol);

    List<CuentaAcceso> findByEstado(Boolean estado);

    List<CuentaAcceso> findByRolAndEstado(Rol rol, Boolean estado);
}