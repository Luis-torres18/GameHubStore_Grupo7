package com.GameHubStore.inventory.repository;

import com.GameHubStore.inventory.model.entities.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // Ya lo tenías - buscar por productId
    Optional<Inventory> findByProductId(long productId);

    // Nuevo - buscar por bodega/ubicacion
    List<Inventory> findByLocation(String location);

    // Nuevo - verificar si ya existe un registro para ese producto
    boolean existsByProductId(long productId);
}