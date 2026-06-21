package com.GameHubStore.warranty_service.client;

import com.GameHubStore.warranty_service.client.dto.OrderDto;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "order-service", url ="https://localhost:8085" )
public interface OrderClient {
    @GetMapping("/api/orders/{id}")
    List<OrderDto> getOrderById(@PathVariable ("id") Long id);
}
