package com.GameHubStore.category_service.repository;

import com.GameHubStore.category_service.model.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
    Optional<Category> findByNombreIgnoreCase(String nombre);
    List<Category> findAllByEstado(Boolean estado);
}
