package com.GameHubStore.inventory.repository;

import com.GameHubStore.inventory.model.entities.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {

}

