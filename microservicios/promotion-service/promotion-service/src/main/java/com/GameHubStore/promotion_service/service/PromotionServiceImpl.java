package com.GameHubStore.promotion_service.service;

import com.GameHubStore.promotion_service.client.CategoryClient;
import com.GameHubStore.promotion_service.client.ProductClient;
import com.GameHubStore.promotion_service.exception.InvalidPromotion;
import com.GameHubStore.promotion_service.exception.PromotionNotFoundException;
import com.GameHubStore.promotion_service.client.dto.CategoryResponse;
import com.GameHubStore.promotion_service.client.dto.ProductResponse;
import com.GameHubStore.promotion_service.model.dto.PromotionRequest;
import com.GameHubStore.promotion_service.model.dto.PromotionResponse;
import com.GameHubStore.promotion_service.model.entities.Promotion;
import com.GameHubStore.promotion_service.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final ProductClient productClient;
    private final CategoryClient categoryClient;

    @Override
    public PromotionResponse createPromotion(PromotionRequest request) {

        if (promotionRepository.existsByCode(request.getCode().toUpperCase())) {
            throw new InvalidPromotion("Una promocion ya existe actualmente con el codigo: " + request.getCode());
        }
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new InvalidPromotion("Fecha de inicio no puede ser anterior a fecha de inicio");
        }

        if(request.getProductId()!=null){
            List<ProductResponse> productos = productClient.getProductById(request.getProductId());
            if(productos.isEmpty()) {
                throw new InvalidPromotion("El producto con ID" + request.getProductId() + "no existe");
            }
            if(!Boolean.TRUE.equals(productos.get(0).getEstado())){
                throw new InvalidPromotion(("El producto con ID "+request.getProductId()+"esta inactivo"));
            }

        }
        if(request.getCategoryId()!=null){
            CategoryResponse categoria = categoryClient.getCategoryById(request.getCategoryId());
            if (categoria == null){
                throw new InvalidPromotion("La categoria con id "+ request.getCategoryId() + "no existe");
            }
            if(!Boolean.TRUE.equals(categoria.getEstado())){
                throw new InvalidPromotion(("El producto con ID "+request.getProductId()+"esta inactivo"));
            }
        }
        Promotion promotion = Promotion.builder()
                .code(request.getCode().toUpperCase())
                .type(request.getType().toUpperCase())
                .discountAmount(request.getDiscountAmount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .minAmount(request.getMinAmount())
                .maxUses(request.getMaxUses())
                .currentUses(0)
                .productId(request.getProductId())
                .categoryId(request.getCategoryId())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        Promotion saved = promotionRepository.save(promotion);
        return toResponse(saved);


    }

    @Override
    public List<PromotionResponse> getActivePromotions() {
        LocalDate today = LocalDate.now();
        return promotionRepository
                .findByIsActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(today, today)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PromotionResponse> getAllPromotions() {
        return promotionRepository.findAllByOrderByStartDateDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PromotionResponse getPromotionById(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new PromotionNotFoundException("Promocion no encontrada con ID: " + id));
        return toResponse(promotion);
    }

    @Override
    public PromotionResponse getPromotionByCode(String code) {
        Promotion promotion = promotionRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new PromotionNotFoundException("Promocion no encontrada con el codigo: " + code));
        return toResponse(promotion);
    }

    @Override
    public PromotionResponse updatePromotion(Long id, PromotionRequest request) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new PromotionNotFoundException("Promocion no encontrada con ID: " + id));

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new InvalidPromotion("Fecha de termino no puede ser anterior a fecha de inicio");
        }

        promotion.setType(request.getType().toUpperCase());
        promotion.setDiscountAmount(request.getDiscountAmount());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setMinAmount(request.getMinAmount());
        promotion.setMaxUses(request.getMaxUses());
        promotion.setProductId(request.getProductId());
        promotion.setCategoryId(request.getCategoryId());
        promotion.setIsActive(request.getIsActive());

        Promotion updated = promotionRepository.save(promotion);
        return toResponse(updated);
    }

    @Override
    public PromotionResponse desactivatePromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new PromotionNotFoundException("Promocion no encontrada con el ID: " + id));

        if (!promotion.getIsActive()) {
            throw new InvalidPromotion("Promocion esta actualmente desactivada");
        }
        promotion.setIsActive(false);
        Promotion saved = promotionRepository.save(promotion);
        return toResponse(saved);
    }


    private PromotionResponse toResponse(Promotion p) {
        LocalDate today = LocalDate.now();
        boolean isValid = p.getIsActive()
                && !today.isBefore(p.getStartDate())
                && !today.isAfter(p.getEndDate())
                && p.getCurrentUses() < p.getMaxUses();

        return PromotionResponse.builder()
                .id(p.getId())
                .code(p.getCode())
                .type(p.getType())
                .discountAmount(p.getDiscountAmount())
                .startDate(p.getStartDate())
                .endDate(p.getEndDate())
                .minAmount(p.getMinAmount())
                .maxUses(p.getMaxUses())
                .currentUses(p.getCurrentUses())
                .productId(p.getProductId())
                .categoryId(p.getCategoryId())
                .isActive(p.getIsActive())
                .isValid(isValid)
                .build();
    }
}
