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
        log.info("[promotion-service] Creating promotion with code: {}", request.getCodigo());

        if (promotionRepository.existsByCodigo(request.getCodigo().toUpperCase())) {
            throw new InvalidPromotion("A promotion already exists with code: " + request.getCodigo());
        }
        if (request.getFechaFin().isBefore(request.getFechaInicio())) {
            throw new InvalidPromotion("End date cannot be before start date");
        }

        Promotion promotion = Promotion.builder()
                .codigo(request.getCodigo().toUpperCase())
                .tipo(request.getTipo().toUpperCase())
                .valor(request.getValor())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .montoMinimo(request.getMontoMinimo())
                .usosMaximos(request.getUsosMaximos())
                .usosActuales(0)
                .productoId(request.getProductoId())
                .categoriaId(request.getCategoriaId())
                .estado(request.getEstado() != null ? request.getEstado() : true)
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
                .findByEstadoTrueAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(today, today)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PromotionResponse> getAllPromotions() {
        log.info("[promotion-service] Listing all promotions (historical)");
        return promotionRepository.findAllByOrderByFechaInicioDesc()
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
    public PromotionResponse getPromotionByCode(String codigo) {
        log.info("[promotion-service] Searching promotion by code: {}", codigo);
        Promotion promotion = promotionRepository.findByCodigo(codigo.toUpperCase())
                .orElseThrow(() -> new PromotionNotFoundException("Promotion not found with code: " + codigo));
        return toResponse(promotion);
    }

    @Override
    public PromotionResponse updatePromotion(Long id, PromotionRequest request) {
        log.info("[promotion-service] Updating promotion ID: {}", id);
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new PromotionNotFoundException("Promotion not found with ID: " + id));

        if (request.getFechaFin().isBefore(request.getFechaInicio())) {
            throw new InvalidPromotion("End date cannot be before start date");
        }

        promotion.setTipo(request.getTipo().toUpperCase());
        promotion.setValor(request.getValor());
        promotion.setFechaInicio(request.getFechaInicio());
        promotion.setFechaFin(request.getFechaFin());
        promotion.setMontoMinimo(request.getMontoMinimo());
        promotion.setUsosMaximos(request.getUsosMaximos());
        promotion.setProductoId(request.getProductoId());
        promotion.setCategoriaId(request.getCategoriaId());

        Promotion updated = promotionRepository.save(promotion);
        log.info("[promotion-service] Promotion updated ID: {}", updated.getId());
        return toResponse(updated);
    }

    @Override
    public PromotionResponse desactivatePromotion(Long id) {
        log.info("[promotion-service] Deactivating promotion ID: {}", id);
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new PromotionNotFoundException("Promotion not found with ID: " + id));

        if (!promotion.getEstado()) {
            throw new InvalidPromotion("Promotion is already inactive");
        }
        promotion.setEstado(false);
        Promotion saved = promotionRepository.save(promotion);
        log.info("[promotion-service] Promotion deactivated ID: {}", saved.getId());
        return toResponse(saved);
    }

    @Override
    public PromotionResponse applyPromotion(ApplyPromotionRequest request) {
        log.info("[promotion-service] Applying coupon: {} for amount: {}", request.getCodigo(), request.getMontoOrden());

        Promotion promotion = promotionRepository.findByCodigo(request.getCodigo().toUpperCase())
                .orElseThrow(() -> new PromotionNotFoundException("Coupon not found: " + request.getCodigo()));

        LocalDate today = LocalDate.now();

        if (!promotion.getEstado()) {
            throw new InvalidPromotion("Coupon is inactive");
        }
        if (today.isBefore(promotion.getFechaInicio()) || today.isAfter(promotion.getFechaFin())) {
            throw new InvalidPromotion("Coupon is expired or not yet valid");
        }
        if (promotion.getUsosActuales() >= promotion.getUsosMaximos()) {
            throw new InvalidPromotion("Coupon has reached its maximum usage limit");
        }
        if (request.getMontoOrden() < promotion.getMontoMinimo()) {
            throw new InvalidPromotion(
                    "Order amount (" + request.getMontoOrden() + ") does not meet the minimum required (" + promotion.getMontoMinimo() + ")");
        }

        double discount = "PORCENTAJE".equals(promotion.getTipo())
                ? request.getMontoOrden() * (promotion.getValor() / 100)
                : promotion.getValor();

        if (discount >= request.getMontoOrden()) {
            throw new InvalidPromotion("Discount cannot be equal to or greater than the order total");
        }

        promotion.setUsosActuales(promotion.getUsosActuales() + 1);
        Promotion saved = promotionRepository.save(promotion);
        log.info("[promotion-service] Coupon {} applied. Current uses: {}", saved.getCodigo(), saved.getUsosActuales());
        return toResponse(saved);
    }

    // ─── Mapper ────────────────────────────────────────────────────────────────
    private PromotionResponse toResponse(Promotion p) {
        LocalDate today = LocalDate.now();
        boolean vigente = p.getEstado()
                && !today.isBefore(p.getFechaInicio())
                && !today.isAfter(p.getFechaFin())
                && p.getUsosActuales() < p.getUsosMaximos();

        return PromotionResponse.builder()
                .id(p.getId())
                .codigo(p.getCodigo())
                .tipo(p.getTipo())
                .valor(p.getValor())
                .fechaInicio(p.getFechaInicio())
                .fechaFin(p.getFechaFin())
                .montoMinimo(p.getMontoMinimo())
                .usosMaximos(p.getUsosMaximos())
                .usosActuales(p.getUsosActuales())
                .productoId(p.getProductoId())
                .categoriaId(p.getCategoriaId())
                .estado(p.getEstado())
                .vigente(vigente)
                .build();
    }
}
