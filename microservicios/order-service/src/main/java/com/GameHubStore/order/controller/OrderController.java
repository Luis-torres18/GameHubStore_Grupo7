package com.GameHubStore.order.controller;

import com.GameHubStore.order.model.dto.OrderRequest;
import com.GameHubStore.order.model.dto.OrderResponse;
import com.GameHubStore.order.service.OrderService;
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
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }

    // GET /api/orders/{id}
    // Retorna una sola orden buscada por ID, envuelta en lista (mismo estilo que compañero)
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getOrderById(@PathVariable Long id) {
        return Collections.singletonList(orderService.getOrderById(id));
    }

    // GET /api/orders/usuario/{userId}
    // Retorna todas las órdenes de un usuario específico
    @GetMapping("/usuario/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getOrdersByUserId(@PathVariable Long userId) {
        return orderService.getOrdersByUserId(userId);
    }

    // GET /api/orders/estado/{status}
    // Retorna órdenes filtradas por estado (ej: PENDING, PAID, CANCELLED)
    @GetMapping("/estado/{status}")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getOrdersByStatus(@PathVariable String status) {
        return orderService.getOrdersByStatus(status);
    }

    // POST /api/orders
    // Crea una nueva orden con los datos del body
    // @Valid activa las validaciones del OrderRequest
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createOrder(@Valid @RequestBody OrderRequest request) {
        orderService.createOrder(request);
    }

    // PUT /api/orders/{id}/estado?status=PAID
    // Actualiza el estado de una orden específica
    @PutMapping("/{id}/estado")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return orderService.updateStatus(id, status);
    }

    // PATCH /api/orders/{id}/cancelar
    // Cancela una orden cambiando su estado a CANCELLED
    @PatchMapping("/{id}/cancelar")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse cancelOrder(@PathVariable Long id) {
        return orderService.cancelOrder(id);
    }
}