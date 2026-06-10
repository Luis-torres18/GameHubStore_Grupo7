package com.GameHubStore.shipping.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="order-service", url = "http://localhost:8085")
public interface OrderClient {
    @GetMapping("/api/orders/{id}")
    Object getOrderById(@PathVariable("id") Long id);
}
