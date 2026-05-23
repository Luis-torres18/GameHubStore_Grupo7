package com.GameHubStore.promotion_service.service;

import com.GameHubStore.promotion_service.model.dto.ApplyPromotionRequest;
import com.GameHubStore.promotion_service.model.dto.PromotionRequest;
import com.GameHubStore.promotion_service.model.dto.PromotionResponse;

import java.util.List;

public interface PromotionService {

    PromotionResponse createPromotion(PromotionRequest request);

    List<PromotionResponse> getActivePromotions();

    List<PromotionResponse> getAllPromotions();

    PromotionResponse getPromotionById(Long id);

    PromotionResponse getPromotionByCode(String code);

    PromotionResponse updatePromotion(Long id, PromotionRequest request);

    PromotionResponse desactivatePromotion(Long id);

    PromotionResponse applyPromotion(ApplyPromotionRequest request);
}