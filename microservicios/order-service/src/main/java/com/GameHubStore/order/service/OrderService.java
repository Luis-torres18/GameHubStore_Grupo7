package com.GameHubStore.order.service;

import com.GameHubStore.order.client.*;
import com.GameHubStore.order.exception.OrderNotFoundException;
import com.GameHubStore.order.model.dto.OrderRequest;
import com.GameHubStore.order.model.dto.OrderResponse;
import com.GameHubStore.order.model.entities.Order;
import com.GameHubStore.order.repository.OrderRepository;
import feign.FeignException;
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
    private final UserClient userClient;
    private final ProductClient productClient;
    private final InventoryClient inventoryClient;
    private final PromotionClient promotionClient;

    public void createOrder(OrderRequest request) {
        try {
            UserResponse user = userClient.getUserById(request.getUserId());
            if (user == null || Boolean.FALSE.equals(user.getEstado())) {
                throw new IllegalStateException("Usuario inactivo o no encontrado: " + request.getUserId());
            }
            log.info("Usuario validado userId={}", request.getUserId());
        } catch (FeignException.NotFound e) {
            throw new IllegalStateException("No existe usuario con ID: " + request.getUserId());
        } catch (FeignException e) {
            log.error("Error al comunicarse con user-service: {}", e.getMessage());
            throw new IllegalStateException("No se pudo validar el usuario, intenta más tarde.");
        }

        try {
            List<ProductResponse> products = productClient.getProductById(request.getProductId());
            if (products == null || products.isEmpty()) {
                throw new IllegalStateException("Producto no encontrado: " + request.getProductId());
            }
            ProductResponse product = products.get(0);
            if (Boolean.FALSE.equals(product.getEstado())) {
                throw new IllegalStateException("El producto está inactivo: " + request.getProductId());
            }
            log.info("Producto validado productId={}", request.getProductId());
        } catch (FeignException.NotFound e) {
            throw new IllegalStateException("No existe producto con ID: " + request.getProductId());
        } catch (FeignException e) {
            log.error("Error al comunicarse con product-service: {}", e.getMessage());
            throw new IllegalStateException("No se pudo validar el producto, intenta más tarde.");
        }

        try {
            inventoryClient.reserveStock(request.getProductId(), request.getQuantity());
            log.info("Stock reservado productId={} cantidad={}", request.getProductId(), request.getQuantity());
        } catch (FeignException.NotFound e) {
            throw new IllegalStateException("No existe stock para el producto: " + request.getProductId());
        } catch (FeignException e) {
            log.error("Error al comunicarse con inventory-service: {}", e.getMessage());
            throw new IllegalStateException("No se pudo reservar el stock, intenta más tarde.");
        }

        Double total = request.getTotal();
        try {
            List<PromotionResponse> promotions = promotionClient.getActivePromotions();
            if (promotions != null && !promotions.isEmpty()) {
                for (PromotionResponse promo : promotions) {
                    if (Boolean.TRUE.equals(promo.getIsValid())
                            && promo.getMinAmount() != null
                            && total >= promo.getMinAmount()) {
                        total = total - promo.getDiscountAmount();
                        log.info("Promoción aplicada code={} descuento={}", promo.getCode(), promo.getDiscountAmount());
                        break;
                    }
                }
            }
        } catch (FeignException e) {
            log.warn("No se pudo consultar promotion-service, se continúa sin descuento: {}", e.getMessage());
        }
        Order newOrder = Order.builder()
                .userId(request.getUserId())
                .total(total) // <--- CORRECCIÓN: Usamos la variable 'total' que ya tiene el descuento aplicado
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
        orderRepository.save(newOrder);
        log.info("Orden creada para userId={} con total={}", request.getUserId(), total);
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