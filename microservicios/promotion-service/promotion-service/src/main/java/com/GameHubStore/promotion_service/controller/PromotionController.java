package com.GameHubStore.promotion_service.controller;


import com.GameHubStore.promotion_service.model.dto.PromotionRequest;
import com.GameHubStore.promotion_service.model.dto.PromotionResponse;
import com.GameHubStore.promotion_service.service.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotion")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    // crear promocion
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PromotionResponse createPromotion(@RequestBody @Valid PromotionRequest request) {
        return promotionService.createPromotion(request);
    }

    // obenter promociones activas
    @GetMapping("/active")
    @ResponseStatus(HttpStatus.OK)
    public List<PromotionResponse> getActivePromotions() {
        return promotionService.getActivePromotions();
    }

    // tener todas las promociones
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PromotionResponse> getAllPromotions() {
        return promotionService.getAllPromotions();
    }

    // obtener promociones por id
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PromotionResponse getPromotionById(@PathVariable Long id) {
        return promotionService.getPromotionById(id);
    }

    // obtener promociones por codigo
    @GetMapping("/code/{code}")
    @ResponseStatus(HttpStatus.OK)
    public PromotionResponse getPromotionByCode(@PathVariable String code) {
        return promotionService.getPromotionByCode(code);
    }

    // actualizar promocion
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PromotionResponse updatePromotion(
            @PathVariable Long id,
            @RequestBody @Valid PromotionRequest request) {
        return promotionService.updatePromotion(id, request);
    }

    // desactivar promocion
    @PatchMapping("/{id}/desactivate")
    @ResponseStatus(HttpStatus.OK)
    public PromotionResponse desactivatePromotion(@PathVariable Long id) {
        return promotionService.desactivatePromotion(id);
    }


}
