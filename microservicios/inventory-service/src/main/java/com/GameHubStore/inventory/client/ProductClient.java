package com.GameHubStore.inventory.client;

import com.GameHubStore.inventory.client.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

// Consume product-service en puerto 8082
@FeignClient(name = "product-service", url = "http://localhost:8082/api/product/")
public interface ProductClient {

    @GetMapping("/{id}")
    List<ProductDto> getProductById(@PathVariable ("id") Long id);
}