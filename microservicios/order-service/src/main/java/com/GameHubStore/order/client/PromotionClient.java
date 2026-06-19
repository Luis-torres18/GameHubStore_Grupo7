package com.GameHubStore.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name= "promotion-service", url = "http://localhost:8088/api/promotion")
public interface PromotionClient {
    @GetMapping("/active")
    List<PromotionResponse> getActivePromotions();
    @GetMapping("/{id}")
    PromotionResponse getPromotionById(@PathVariable ("id") Long id);
}
