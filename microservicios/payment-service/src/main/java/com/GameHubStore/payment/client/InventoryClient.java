package com.GameHubStore.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient
public interface InventoryClient {

    @PatchMapping("/confirmar/{ordenId}")
    void confirmarPorOrdenId(@PathVariable("ordenId")Long ordenId);

    @PatchMapping("/liberar/{ordenId}")
    void liberarPorOrdenId(@PathVariable("ordenId")Long ordenId);

    @GetMapping("/producto/{productId}")
    InventoryDto getInventoryByProductId(@PathVariable("productId") Long productId);
}
