package com.GameHubStore.payment.client;

import  com.GameHubStore.order.model.dto.OrderResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "order-service", url = "http://localhost:8085")
public interface OrderClient {
    @GetMapping("/api/orders/{id}")
    List<OrderResponse> getOrders(@PathVariable ("id") Long id);
    List<OrderResponse> getOrderById(@NotNull(message = "El ID de la orden es obligatorio") Long ordenId);
}
