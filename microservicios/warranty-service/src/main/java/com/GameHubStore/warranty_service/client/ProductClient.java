package com.GameHubStore.warranty_service.client;

import com.GameHubStore.warranty_service.client.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "product-service", url = "http://localhost:8082")
public interface ProductClient {

    // Verifica que el producto existe antes de crear registro de stock
    @GetMapping("/api/product/{id}")
    List<ProductDto> getProductById(@PathVariable("id") Long id);
}