package com.GameHubStore.promotion_service.service;

import com.GameHubStore.promotion_service.model.dto.ApplyPromotionRequest;
import com.GameHubStore.promotion_service.model.dto.PromotionRequest;
import com.GameHubStore.promotion_service.model.dto.PromotionResponse;

import java.util.List;

public interface PromotionService {

    PromotionResponse crearPromocion(PromotionRequest request);

    List<PromotionResponse> listarVigentes();

    List<PromotionResponse> listarTodas();

    PromotionResponse buscarPorId(Long id);

    PromotionResponse buscarPorCodigo(String codigo);

    PromotionResponse actualizarPromocion(Long id, PromotionRequest request);

    PromotionResponse desactivarPromocion(Long id);

    // Endpoint que consume order-service: valida y registra uso del cupón //falta aun apply promotion
    PromotionResponse aplicarCupon(ApplyPromotionRequest request);
}