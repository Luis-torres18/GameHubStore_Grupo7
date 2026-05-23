package com.GameHubStore.promotion_service.service;

import com.GameHubStore.promotion_service.exception.InvalidPromotion;
import com.GameHubStore.promotion_service.exception.PromotionNotFoundException;
import com.GameHubStore.promotion_service.model.dto.ApplyPromotionRequest;
import com.GameHubStore.promotion_service.model.dto.PromotionRequest;
import com.GameHubStore.promotion_service.model.dto.PromotionResponse;
import com.GameHubStore.promotion_service.model.entities.Promotion;
import com.GameHubStore.promotion_service.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private static final Logger log = LoggerFactory.getLogger(PromotionServiceImpl.class);
    private final PromotionRepository promotionRepository;

    @Override
    public PromotionResponse createPromotion(PromotionRequest request) {
        log.info("[promotion-service] Creating promotion with code: {}", request.getCode());

        if (promotionRepository.existsByCode(request.getCode().toUpperCase())) {
            throw new InvalidPromotion("A promotion already exists with code: " + request.getCode());
        }
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new InvalidPromotion("End date cannot be before start date");
        }

        Promotion promotion = Promotion.builder()
                .code(request.getCode().toUpperCase())
                .type(request.getType().toUpperCase())
                .value(request.getValue())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .minAmount(request.getMinAmount())
                .maxUses(request.getMaxUses())
                .currentUses(0)
                .productId(request.getProductId())
                .categoryId(request.getCategoryId())
                .status(request.getStatus() != null ? request.getStatus() : true)
                .build();

        Promotion saved = promotionRepository.save(promotion);
        log.info("[promotion-service] Promotion created with ID: {}", saved.getId());
        return toResponse(saved);
    }

    @Override
    public List<PromotionResponse> getActivePromotions() {
        log.info("[promotion-service] Listing active promotions");
        LocalDate today = LocalDate.now();
        return promotionRepository
                .findByStatusTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(today, today)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PromotionResponse> getAllPromotions() {
        log.info("[promotion-service] Listing all promotions (historical)");
        return promotionRepository.findAllByOrderByStartDateDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PromotionResponse getPromotionById(Long id) {
        log.info("[promotion-service] Searching promotion by ID: {}", id);
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new PromotionNotFoundException("Promotion not found with ID: " + id));
        return toResponse(promotion);
    }

    @Override
    public PromotionResponse getPromotionByCode(String code) {
        log.info("[promotion-service] Searching promotion by code: {}", code);
        Promotion promotion = promotionRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new PromotionNotFoundException("Promotion not found with code: " + code));
        return toResponse(promotion);
    }

    @Override
    public PromotionResponse updatePromotion(Long id, PromotionRequest request) {
        log.info("[promotion-service] Updating promotion ID: {}", id);
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new PromotionNotFoundException("Promotion not found with ID: " + id));

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new InvalidPromotion("End date cannot be before start date");
        }

        promotion.setType(request.getType().toUpperCase());
        promotion.setValue(request.getValue());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setMinAmount(request.getMinAmount());
        promotion.setMaxUses(request.getMaxUses());
        promotion.setProductId(request.getProductId());
        promotion.setCategoryId(request.getCategoryId());

        Promotion updated = promotionRepository.save(promotion);
        log.info("[promotion-service] Promotion updated ID: {}", updated.getId());
        return toResponse(updated);
    }

    @Override
    public PromotionResponse desactivatePromotion(Long id) {
        log.info("[promotion-service] Deactivating promotion ID: {}", id);
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new PromotionNotFoundException("Promotion not found with ID: " + id));

        if (!promotion.getStatus()) {
            throw new InvalidPromotion("Promotion is already inactive");
        }
        promotion.setStatus(false);
        Promotion saved = promotionRepository.save(promotion);
        log.info("[promotion-service] Promotion deactivated ID: {}", saved.getId());
        return toResponse(saved);
    }

    @Override
    public PromotionResponse applyPromotion(ApplyPromotionRequest request) {
        log.info("[promotion-service] Applying coupon: {} for amount: {}", request.getCode(), request.getOrderAmount());

        Promotion promotion = promotionRepository.findByCode(request.getCode().toUpperCase())
                .orElseThrow(() -> new PromotionNotFoundException("Coupon not found: " + request.getCode()));

        LocalDate today = LocalDate.now();

        // Business Logic Validations
        if (!promotion.getStatus()) {
            throw new InvalidPromotion("Coupon is inactive");
        }
        if (today.isBefore(promotion.getStartDate()) || today.isAfter(promotion.getEndDate())) {
            throw new InvalidPromotion("Coupon is expired or not yet valid");
        }
        // Validate Max Uses
        if (promotion.getCurrentUses() >= promotion.getMaxUses()) {
            throw new InvalidPromotion("Coupon has reached its maximum usage limit");
        }
        // Validate Min Amount
        if (request.getOrderAmount() < promotion.getMinAmount()) {
            throw new InvalidPromotion(
                    "Order amount (" + request.getOrderAmount() + ") does not meet the minimum required (" + promotion.getMinAmount() + ")");
        }

        double discount = "PORCENTAJE".equalsIgnoreCase(promotion.getType())
                ? request.getOrderAmount() * (promotion.getValue() / 100)
                : promotion.getValue();

        if (discount >= request.getOrderAmount()) {
            throw new InvalidPromotion("Discount cannot be equal to or greater than the order total");
        }

        promotion.setCurrentUses(promotion.getCurrentUses() + 1);
        Promotion saved = promotionRepository.save(promotion);
        log.info("[promotion-service] Coupon {} applied. Current uses: {}", saved.getCode(), saved.getCurrentUses());
        return toResponse(saved);
    }

    private PromotionResponse toResponse(Promotion p) {
        LocalDate today = LocalDate.now();
        boolean isValid = p.getStatus()
                && !today.isBefore(p.getStartDate())
                && !today.isAfter(p.getEndDate())
                && p.getCurrentUses() < p.getMaxUses();

        return PromotionResponse.builder()
                .id(p.getId())
                .code(p.getCode())
                .type(p.getType())
                .value(p.getValue())
                .startDate(p.getStartDate())
                .endDate(p.getEndDate())
                .minAmount(p.getMinAmount())
                .maxUses(p.getMaxUses())
                .currentUses(p.getCurrentUses())
                .productId(p.getProductId())
                .categoryId(p.getCategoryId())
                .status(p.getStatus())
                .isValid(isValid)
                .build();
    }
}
