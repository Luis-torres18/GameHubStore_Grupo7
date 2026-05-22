package com.GameHubStore.inventory.service;

import com.GameHubStore.inventory.model.dto.InventoryRequest;
import com.GameHubStore.inventory.model.entities.Inventory;
import com.GameHubStore.inventory.repository.InventoryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    // ESTE NOMBRE DEBE SER EXACTAMENTE IGUAL AL QUE LLAMAS EN EL CONTROLLER
    public List<Inventory> getAllInventories() {
        log.info("Consultando todos los inventarios");
        return inventoryRepository.findAll();
    }

    // ESTE NOMBRE DEBE SER EXACTAMENTE IGUAL AL QUE LLAMAS EN EL CONTROLLER
    public Inventory addInventory(InventoryRequest request) {
        log.info("Agregando nuevo inventario para producto: {}", request.getProductId());
        Inventory inventory = new Inventory();
        inventory.setProductId(request.getProductId());
        inventory.setAvailableStock(request.getAvailableStock());
        inventory.setMinimumStock(request.getMinimumStock());
        inventory.setLocation(request.getLocation());

        return inventoryRepository.save(inventory);
    }
}