package com.GameHubStore.promotion_service.controller;


import com.GameHubStore.promotion_service.model.dto.ApplyPromotionRequest;
import com.GameHubStore.promotion_service.model.dto.PromotionRequest;
import com.GameHubStore.promotion_service.model.dto.PromotionResponse;
import com.GameHubStore.promotion_service.service.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/promotion")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    // Crear promoción
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PromotionResponse crearPromocion(@RequestBody @Valid PromotionRequest request) {
        return promotionService.crearPromocion(request);
    }

    // Listar promociones vigentes
    @GetMapping("/vigentes")
    @ResponseStatus(HttpStatus.OK)
    public List<PromotionResponse> listarVigentes() {
        return promotionService.listarVigentes();
    }

    // Listar todas (históricas + activas)
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PromotionResponse> listarTodas() {
        return promotionService.listarTodas();
    }

    // Buscar por ID
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<PromotionResponse> buscarPorId(@PathVariable Long id) {
        return Collections.singletonList(promotionService.buscarPorId(id));
    }

    // Buscar por código
    @GetMapping("/codigo/{codigo}")
    @ResponseStatus(HttpStatus.OK)
    public PromotionResponse buscarPorCodigo(@PathVariable String codigo) {
        return promotionService.buscarPorCodigo(codigo);
    }

    // Actualizar condiciones y fechas
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PromotionResponse actualizarPromocion(
            @PathVariable Long id,
            @RequestBody @Valid PromotionRequest request) {
        return promotionService.actualizarPromocion(id, request);
    }

    // Desactivar promoción
    @PatchMapping("/{id}/desactivar")
    @ResponseStatus(HttpStatus.OK)
    public PromotionResponse desactivarPromocion(@PathVariable Long id) {
        return promotionService.desactivarPromocion(id);
    }

    // Aplicar cupón (consumido por order-service)
    @PostMapping("/aplicar")
    @ResponseStatus(HttpStatus.OK)
    public PromotionResponse aplicarCupon(@RequestBody @Valid ApplyPromotionRequest request) {
        return promotionService.aplicarCupon(request);
    }
}
