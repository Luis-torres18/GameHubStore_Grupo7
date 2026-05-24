package com.GameHubStore.inventory.service;

import com.GameHubStore.inventory.exception.BusinessException;
import com.GameHubStore.inventory.exception.InventoryNotFoundException;
import com.GameHubStore.inventory.model.dto.InventoryRequest;
import com.GameHubStore.inventory.model.dto.InventoryResponse;
import com.GameHubStore.inventory.model.entities.Inventory;
import com.GameHubStore.inventory.model.entities.InventoryMovement;
import com.GameHubStore.inventory.repository.InventoryMovementRepository;
import com.GameHubStore.inventory.repository.InventoryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository inventoryMovementRepository;

    // Crea registro de stock para cada producto
    @Transactional
    public InventoryResponse addInventory(InventoryRequest request) {
        log.info("[INVENTORY-SERVICE] Creating stock record for productId={}", request.getProductId());

        // validacion no duplicar productos
        if (inventoryRepository.existsByProductId(request.getProductId())) {
            log.warn("[INVENTORY-SERVICE] Stock record already exists for productId={}", request.getProductId());
            throw new BusinessException("Stock record already exists for productId=" + request.getProductId());
        }

        Inventory inventory = Inventory.builder()
                .productId(request.getProductId())
                .availableStock(request.getAvailableStock())
                .reservedStock(0)
                .minimumStock(request.getMinimumStock())
                .Location(request.getLocation())
                .build();

        Inventory saved = inventoryRepository.save(inventory);

        // Registra movimiento por cada ajuste
        registerMovement(saved.getProductId(), "ENTRADA", saved.getAvailableStock());

        log.info("[INVENTORY-SERVICE] Stock record created id={}", saved.getId());
        return mapToResponse(saved);
    }

    // Listar stock por producto
    public InventoryResponse getInventoryByProductId(Long productId) {
        log.info("[INVENTORY-SERVICE] Fetching stock for productId={}", productId);
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> {
                    log.error("[INVENTORY-SERVICE] Stock not found for productId={}", productId);
                    return new InventoryNotFoundException("Stock not found for productId=" + productId);
                });
        return mapToResponse(inventory);
    }
    //  Listar stock por bodega (ubicacion) ──────────────────────────────────────
    public List<InventoryResponse> getInventoryByLocation(String location) {
        log.info("[INVENTORY-SERVICE] Fetching stock for location={}", location);
        return inventoryRepository.findByLocation(location)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ─── Buscar stock por ID ──────────────────────────────────────────────────────
    public InventoryResponse getInventoryById(Long id) {
        log.info("[INVENTORY-SERVICE] Fetching stock by id={}", id);
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[INVENTORY-SERVICE] Stock not found with id={}", id);
                    return new InventoryNotFoundException("Stock not found with id=" + id);
                });
        return mapToResponse(inventory);
    }

    // ─── Actualizar cantidades disponibles o reservadas ───────────────────────────
    @Transactional
    public InventoryResponse updateStock(Long id, Integer availableStock, Integer reservedStock) {
        log.info("[INVENTORY-SERVICE] Updating stock for id={}", id);

        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new InventoryNotFoundException("Stock not found with id=" + id));

        // Regla: stock no puede quedar negativo
        if (availableStock < 0 || reservedStock < 0) {
            log.warn("[INVENTORY-SERVICE] Stock cannot be negative for id={}", id);
            throw new BusinessException("Stock values cannot be negative");
        }

        // Regla: no reservar más unidades que el stock disponible
        if (reservedStock > availableStock) {
            log.warn("[INVENTORY-SERVICE] Reserved stock cannot exceed available stock");
            throw new BusinessException("Reserved stock cannot exceed available stock");
        }

        int difference = availableStock - inventory.getAvailableStock();
        inventory.setAvailableStock(availableStock);
        inventory.setReservedStock(reservedStock);
        inventoryRepository.save(inventory);

        // Regla: registrar movimiento por cada ajuste
        registerMovement(inventory.getProductId(),
                difference >= 0 ? "ENTRADA" : "SALIDA",
                Math.abs(difference));

        log.info("[INVENTORY-SERVICE] Stock updated for productId={}", inventory.getProductId());
        return mapToResponse(inventory);
    }

    // Reserva Stock
    @Transactional
    public InventoryResponse reserveStock(Long productId, Integer quantity) {
        log.info("[INVENTORY-SERVICE] Reserving {} units for productId={}", quantity, productId);

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException("Stock not found for productId=" + productId));

        int freeStock = inventory.getAvailableStock() - inventory.getReservedStock();

        // Regla: no reservar más unidades que el stock disponible
        if (quantity > freeStock) {
            log.warn("[INVENTORY-SERVICE] Insufficient stock. Free={}, Requested={}", freeStock, quantity);
            throw new BusinessException("Insufficient stock. Available: " + freeStock + ", Requested: " + quantity);
        }

        inventory.setReservedStock(inventory.getReservedStock() + quantity);
        inventoryRepository.save(inventory);

        // Regla: registrar movimiento por cada ajuste
        registerMovement(productId, "RESERVA", quantity);

        log.info("[INVENTORY-SERVICE] Stock reserved for productId={}", productId);
        return mapToResponse(inventory);
    }

    // ─── Eliminar o cerrar registro de stock obsoleto ───
    @Transactional
    public void deleteInventory(Long id) {
        log.info("[INVENTORY-SERVICE] Deleting stock record id={}", id);
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new InventoryNotFoundException("Stock not found with id=" + id));
        inventoryRepository.delete(inventory);
        log.info("[INVENTORY-SERVICE] Stock record deleted id={}", id);
    }

    // ─── Helper: registrar movimiento por cada ajuste ────
    private void registerMovement(Long productId, String type, Integer quantity) {
        InventoryMovement movement = InventoryMovement.builder()
                .productId(productId)
                .type(type)
                .quantity(quantity)
                .build();
        inventoryMovementRepository.save(movement);
        log.info("[INVENTORY-SERVICE] Movement registered: type={}, productId={}, quantity={}", type, productId, quantity);
    }

    // mapear entidad a response DTO ───
    private InventoryResponse mapToResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .productId(inventory.getProductId())
                .availableStock(inventory.getAvailableStock())
                .reservedStock(inventory.getReservedStock())
                .minimumStock(inventory.getMinimumStock())
                .Location(inventory.getLocation())
                .build();
    }
}