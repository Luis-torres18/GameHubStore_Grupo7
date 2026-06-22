package com.GameHubStore.inventory.controller;

import com.GameHubStore.inventory.assemblers.InventoryModelAssemblers;
import com.GameHubStore.inventory.model.dto.InventoryRequest;
import com.GameHubStore.inventory.model.dto.InventoryResponse;
import com.GameHubStore.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Operaciones relacionadas con el inventario de productos")
public class InventoryControllerV2{

    private static final Logger log = LoggerFactory.getLogger(InventoryControllerV2.class);
    private final InventoryService inventoryService;
    private final InventoryModelAssemblers assembler;

    @PostMapping
    @Operation(summary = "Crear registro de stock", description = "Crea un nuevo registro de stock para un producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Stock creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o producto ya tiene stock registrado"),
            @ApiResponse(responseCode = "500", description = "Producto no encontrado en product-service")
    })
    public ResponseEntity<EntityModel<InventoryResponse>> addInventory(@Valid @RequestBody InventoryRequest request) {
        log.info("[INVENTORY-CONTROLLER] POST /api/inventory - productId={}", request.getProductId());
        InventoryResponse response = inventoryService.addInventory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(response));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Buscar stock por ID de Producto", description = "Retorna el registro de stock por el ID del producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Stock no encontrado para este producto")
    })
    public ResponseEntity<CollectionModel<EntityModel<InventoryResponse>>> getInventoryByProductId(
            @Parameter(description = "ID del producto", required = true, example = "1")
            @PathVariable Long productId) {
        log.info("[INVENTORY-CONTROLLER] GET /api/inventory/product/{}", productId);

        InventoryResponse response = inventoryService.getInventoryByProductId(productId);
        EntityModel<InventoryResponse> entityModel = assembler.toModel(response);
        return ResponseEntity.ok(CollectionModel.of(List.of(entityModel),
                linkTo(methodOn(InventoryController.class).getInventoryByProductId(productId)).withSelfRel()));
    }

    @GetMapping("/location/{location}")
    @Operation(summary = "Listar stock por bodega", description = "Retorna todos los registros de stock de una bodega")
    @ApiResponse(responseCode = "200", description = "Listado exitoso")
    public ResponseEntity<CollectionModel<EntityModel<InventoryResponse>>> getInventoryByLocation(
            @Parameter(description = "Nombre de la bodega", required = true, example = "Bodega A")
            @PathVariable String location) {
        log.info("[INVENTORY-CONTROLLER] GET /api/inventory/location/{}", location);

        List<EntityModel<InventoryResponse>> inventories = inventoryService.getInventoryByLocation(location).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(inventories,
                linkTo(methodOn(InventoryController.class).getInventoryByLocation(location)).withSelfRel()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar stock", description = "Actualiza las cantidades disponibles y reservadas de un registro de stock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Stock negativo o reservado mayor al disponible"),
            @ApiResponse(responseCode = "404", description = "Stock no encontrado")
    })
    public ResponseEntity<EntityModel<InventoryResponse>> updateStock(
            @Parameter(description = "ID del registro de stock", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Stock disponible", required = true, example = "50")
            @RequestParam Integer availableStock,
            @Parameter(description = "Stock reservado", required = true, example = "5")
            @RequestParam Integer reservedStock) {
        log.info("[INVENTORY-CONTROLLER] PUT /api/inventory/{} - availableStock={}, reservedStock={}", id, availableStock, reservedStock);
        InventoryResponse response = inventoryService.updateStock(id, availableStock, reservedStock);
        return ResponseEntity.ok(assembler.toModel(response));
    }

    @PatchMapping("/reserve/{productId}")
    @Operation(summary = "Reservar stock", description = "Reserva unidades de stock cuando se crea una orden antes de pagar")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock reservado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Stock insuficiente"),
            @ApiResponse(responseCode = "404", description = "Stock no encontrado para ese producto")
    })
    public ResponseEntity<EntityModel<InventoryResponse>> reserveStock(
            @Parameter(description = "ID del producto", required = true, example = "1")
            @PathVariable Long productId,
            @Parameter(description = "Cantidad a reservar", required = true, example = "3")
            @RequestParam Integer quantity) {
        log.info("[INVENTORY-CONTROLLER] PATCH /api/inventory/reserve/{} - quantity={}", productId, quantity);
        InventoryResponse response = inventoryService.reserveStock(productId, quantity);
        return ResponseEntity.ok(assembler.toModel(response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar registro de stock", description = "Elimina o cierra un registro de stock obsoleto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Stock eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Stock no encontrado")
    })
    public ResponseEntity<Void> deleteInventory(
            @Parameter(description = "ID del registro de stock", required = true, example = "1")
            @PathVariable Long id) {
        log.info("[INVENTORY-CONTROLLER] DELETE /api/inventory/{}", id);
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }
}