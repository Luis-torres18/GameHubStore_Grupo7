package com.GameHubStore.order.service;

import com.GameHubStore.order.exception.OrderNotFoundException;
import com.GameHubStore.order.model.dto.OrderRequest;
import com.GameHubStore.order.model.dto.OrderResponse;
import com.GameHubStore.order.model.entities.Order;
import com.GameHubStore.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;

    // Crear una nueva orden
    public void createOrder(OrderRequest request) {
        Order newOrder = Order.builder()
                .userId(request.getUserId())
                .total(request.getTotal())
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
        orderRepository.save(newOrder);
        log.info("Orden creada para userId={} con total={}", request.getUserId(), request.getTotal());
    }

    // Obtener todas las órdenes
    public List<OrderResponse> getAllOrders() {
        log.info("Listando todas las órdenes");
        return orderRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Obtener orden por ID
    public OrderResponse getOrderById(Long id) {
        Order order = findOrderOrThrow(id);
        return mapToResponse(order);
    }

    // Obtener órdenes por userId
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        log.info("Listando órdenes del userId={}", userId);
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Obtener órdenes por estado
    public List<OrderResponse> getOrdersByStatus(String status) {
        log.info("Listando órdenes con status={}", status);
        return orderRepository.findByStatus(status.toUpperCase())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Actualizar estado de una orden
    public OrderResponse updateStatus(Long id, String status) {
        Order order = findOrderOrThrow(id);

        if ("PAID".equalsIgnoreCase(order.getStatus())) {
            throw new IllegalStateException("No se puede modificar una orden ya pagada.");
        }

        order.setStatus(status.toUpperCase());
        log.info("Orden id={} actualizada a status={}", id, status);
        return mapToResponse(orderRepository.save(order));
    }

    // Cancelar una orden
    public OrderResponse cancelOrder(Long id) {
        Order order = findOrderOrThrow(id);

        if ("CANCELLED".equalsIgnoreCase(order.getStatus())) {
            throw new IllegalStateException("La orden ya está cancelada.");
        }
        if ("PAID".equalsIgnoreCase(order.getStatus())) {
            throw new IllegalStateException("No se puede cancelar una orden ya pagada.");
        }

        order.setStatus("CANCELLED");
        log.info("Orden id={} cancelada", id);
        return mapToResponse(orderRepository.save(order));
    }

    // ── helpers ──────────────────────────────────────────────

    private Order findOrderOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Orden con id={} no encontrada", id);
                    return new OrderNotFoundException("Order not found with ID: " + id);
                });
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .total(order.getTotal())
                .createdAt(order.getCreatedAt())
                .build();
    }
}