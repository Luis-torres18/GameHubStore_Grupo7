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

    // Create promotion
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PromotionResponse createPromotion(@RequestBody @Valid PromotionRequest request) {
        return promotionService.createPromotion(request);
    }

    // Get active promotions
    @GetMapping("/active")
    @ResponseStatus(HttpStatus.OK)
    public List<PromotionResponse> getActivePromotions() {
        return promotionService.getActivePromotions();
    }

    // Get all promotions (historical + active)
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PromotionResponse> getAllPromotions() {
        return promotionService.getAllPromotions();
    }

    // Get promotion by ID
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PromotionResponse getPromotionById(@PathVariable Long id) {
        return promotionService.getPromotionById(id);
    }

    // Get promotion by code
    @GetMapping("/code/{code}")
    @ResponseStatus(HttpStatus.OK)
    public PromotionResponse getPromotionByCode(@PathVariable String code) {
        return promotionService.getPromotionByCode(code);
    }

    // Update conditions and dates
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PromotionResponse updatePromotion(
            @PathVariable Long id,
            @RequestBody @Valid PromotionRequest request) {
        return promotionService.updatePromotion(id, request);
    }

    // Deactivate promotion
    @PatchMapping("/{id}/desactivate")
    @ResponseStatus(HttpStatus.OK)
    public PromotionResponse desactivatePromotion(@PathVariable Long id) {
        return promotionService.desactivatePromotion(id);
    }


}
