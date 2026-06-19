package com.GameHubStore.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "product-service", url = "https://localhost:8082")
public interface ProductClient {
    @GetMapping
    List<ProductResponse> getProductById(@PathVariable("id") Long id);
}
