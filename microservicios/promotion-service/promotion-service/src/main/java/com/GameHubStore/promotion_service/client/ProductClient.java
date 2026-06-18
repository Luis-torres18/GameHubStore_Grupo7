package com.GameHubStore.promotion_service.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "http://localhost:8082")
public interface ProductClient {

    // Verifica que el producto existe antes de crear registro de stock
    @GetMapping("/api/product/{id}")
    Object getProductById(@PathVariable("id") Long id);
}