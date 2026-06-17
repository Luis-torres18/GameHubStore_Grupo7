package com.GameHubStore.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventory-service", url= "http://localhost:8084")
public interface InventoryClient {
  @PatchMapping("/reserve/{productId}")
    InventoryClientResponse reserveStock(@PathVariable("productId") long productId,
                                         @RequestParam("quantity") Integer quantity);
}
