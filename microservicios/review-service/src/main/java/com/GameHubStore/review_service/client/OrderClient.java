package com.GameHubStore.review_service.client;

import com.GameHubStore.review_service.client.dto.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "order-service", url = "http://localhost:8085")
public interface OrderClient {

    @GetMapping("/api/orders/{id}")
    List<OrderResponse> getOrderById(@PathVariable Long id);
}