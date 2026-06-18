package com.GameHubStore.promotion_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "category-service", url = "http://localhost:8081")
public interface CategoryClient {

    // Verifica que el producto existe antes de crear registro de stock
    @GetMapping("/api/categories/{id}")
    Object getProductById(@PathVariable("id") Long id);
}