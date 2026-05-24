package com.GameHubStore.inventory.controller;

import com.GameHubStore.inventory.model.dto.InventoryRequest;
import com.GameHubStore.inventory.model.dto.InventoryResponse;
import com.GameHubStore.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController{

    private static final Logger log = LoggerFactory.getLogger(InventoryController.class);

    private final InventoryService inventoryService;

    // Crear registro de stock para producto
    @PostMapping
    public ResponseEntity<InventoryResponse> addInventory(@Valid @RequestBody InventoryRequest request) {
        log.info("[INVENTORY-CONTROLLER] POST /api/inventory - productId={}", request.getProductId());
        InventoryResponse response = inventoryService.addInventory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Buscar stock por ID
    @GetMapping("/{id}")
    public ResponseEntity<List<InventoryResponse>> getInventoryById(@PathVariable Long id) {
        log.info("[INVENTORY-CONTROLLER] GET /api/inventory/{}", id);
        return ResponseEntity.ok(Collections.singletonList(inventoryService.getInventoryById(id)));
    }

    // Listar stock por producto
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<InventoryResponse>> getInventoryByProductId(@PathVariable Long productId) {
        log.info("[INVENTORY-CONTROLLER] GET /api/inventory/product/{}", productId);
        return ResponseEntity.ok(Collections.singletonList(inventoryService.getInventoryByProductId(productId)));
    }

    // Listar stock por bodega
    @GetMapping("/location/{location}")
    public ResponseEntity<List<InventoryResponse>> getInventoryByLocation(@PathVariable String location) {
        log.info("[INVENTORY-CONTROLLER] GET /api/inventory/location/{}", location);
        return ResponseEntity.ok(inventoryService.getInventoryByLocation(location));
    }

    // Actualizar cantidades disponibles o reservadas
    @PutMapping("/{id}")
    public ResponseEntity<InventoryResponse> updateStock(
            @PathVariable Long id,
            @RequestParam Integer availableStock,
            @RequestParam Integer reservedStock) {
        log.info("[INVENTORY-CONTROLLER] PUT /api/inventory/{} - availableStock={}, reservedStock={}", id, availableStock, reservedStock);
        return ResponseEntity.ok(inventoryService.updateStock(id, availableStock, reservedStock));
    }


    // Reservar stock cuando se crea una orden (antes de pagar)
    @PatchMapping("/reserve/{productId}")
    public ResponseEntity<InventoryResponse> reserveStock(
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        log.info("[INVENTORY-CONTROLLER] PATCH /api/inventory/reserve/{} - quantity={}", productId, quantity);
        return ResponseEntity.ok(inventoryService.reserveStock(productId, quantity));
    }

    // Eliminar o cerrar registro de stock
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        log.info("[INVENTORY-CONTROLLER] DELETE /api/inventory/{}", id);
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }
}