package com.GameHubStore.order.controller;

import com.GameHubStore.order.model.dto.OrderRequest;
import com.GameHubStore.order.model.dto.OrderResponse;
import com.GameHubStore.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

// Controlador REST que expone los endpoints de órdenes
// Ruta base: /api/orders
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    // Servicio inyectado automáticamente por @RequiredArgsConstructor
    private final OrderService orderService;

    // GET /api/orders
    // Retorna la lista completa de órdenes
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Listar todas las ordenes", description = "Retorna la lista completa de ordenes")
    @ApiResponse(responseCode = "200", description = "Listado exitoso")
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }

    // GET /api/orders/{id}
    // Retorna una sola orden buscada por ID, envuelta en lista (mismo estilo que compañero)
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Buscar orden por ID", description = "Rertorna una orden especifica por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode ="200", description = "Orden encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    public List<OrderResponse> getOrderById(
            @Parameter(description = "ID de la orden", required = true, example = "1")
            @PathVariable Long id) {
        return Collections.singletonList(orderService.getOrderById(id));
    }

    // GET /api/orders/usuario/{userId}
    // Retorna todas las órdenes de un usuario específico
    @GetMapping("/usuario/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Listar ordenes por usuario", description = "Retorna todas las ordenes de un  usuario especifico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado exitoso"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public List<OrderResponse> getOrdersByUserId(
            @Parameter(description = "ID del usuario", required = true, example = "1")
            @PathVariable Long userId) {
        return orderService.getOrdersByUserId(userId);
    }

    // GET /api/orders/estado/{status}
    // Retorna órdenes filtradas por estado (ej: PENDING, PAID, CANCELLED)
    @GetMapping("/estado/{status}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Listar ordenes por estado", description = "Retorna ordenes filtradas por estado : PENDING, PAID, CANCELLED")
    @ApiResponse(responseCode ="200" , description ="Listado exitoso" )
    public List<OrderResponse> getOrdersByStatus(
            @Parameter(description = "Estado de la orden", required = true, example = "PENDING")
            @PathVariable String status) {
        return orderService.getOrdersByStatus(status);
    }

    // POST /api/orders
    // Crea una nueva orden con los datos del body
    // @Valid activa las validaciones del OrderRequest
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear orden", description = "Crear una nueva orden de compra")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orden creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos")
    })
    public void createOrder(@Valid @RequestBody OrderRequest request) {
        orderService.createOrder(request);
    }

    // PUT /api/orders/{id}/estado?status=PAID
    // Actualiza el estado de una orden específica
    @PutMapping("/{id}/estado")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Crear orden", description = "Crear una nueva orden de compra")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "No se puede modificar una orden pagada"),
            @ApiResponse(responseCode = "404", description ="Orden no encontrada")
    })
    public OrderResponse updateStatus(
            @Parameter(description ="ID de la orden", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nuevo estado",required = true, example = "PAID")
            @RequestParam String status) {
        return orderService.updateStatus(id, status);
    }

    // PATCH /api/orders/{id}/cancelar
    // Cancela una orden cambiando su estado a CANCELLED
    @PatchMapping("/{id}/cancelar")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Cancelar Orden", description = "Cancela una orden cambiando su estado a CANCELLED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden cancelada exitosamente"),
            @ApiResponse(responseCode = "400", description = "No se puede cancelar una orden pagada"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    public OrderResponse cancelOrder(
            @Parameter(description = "ID de la orden", required = true, example = "1")
            @PathVariable Long id) {
        return orderService.cancelOrder(id);
    }
}