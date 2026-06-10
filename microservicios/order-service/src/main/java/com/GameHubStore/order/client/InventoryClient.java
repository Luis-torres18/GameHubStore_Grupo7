package com.GameHubStore.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventory-service", url= "http://localhost:8084")
public interface InventoryClient {
    @GetMapping("/api/inventory/product/{productId}")
    Object getInventoryByProductId(@PathVariable("productId") Long productId);

    @PatchMapping("/api/inventory/reserve/{productId}")
    Object reserveStock(@PathVariable("productId") Long productId);

    @PatchMapping("/api/inventory/release/{productId}")
    Object releaseStock(@PathVariable("productId") Long productId,
                        @RequestParam("quantity")Integer quantity);

}
