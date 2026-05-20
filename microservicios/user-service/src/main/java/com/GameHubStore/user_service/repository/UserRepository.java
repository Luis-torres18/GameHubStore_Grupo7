package com.GameHubStore.user_service.repository;

import com.GameHubStore.user_service.model.entities.Rol;
import com.GameHubStore.user_service.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    Optional<User> findByEmail(String email);

    List<User> findByRol(Rol rol);

    List<User> findByEstado(Boolean estado);

    List<User> findByRolAndEstado(Rol rol, Boolean estado);
}
