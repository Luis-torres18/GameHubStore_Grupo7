package com.GameHubStore.shipping.controller;

import com.GameHubStore.shipping.model.dto.ShippingRequest;
import com.GameHubStore.shipping.model.dto.ShippingResponse;
import com.GameHubStore.shipping.service.ShippingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shippings")
@RequiredArgsConstructor
public class ShippingController {

    private static final Logger log = LoggerFactory.getLogger(ShippingController.class);

    private final ShippingService shippingService;

    // POST /api/shippings → Crear despacho
    @PostMapping
    public ResponseEntity<ShippingResponse> createShipping(@Valid @RequestBody ShippingRequest request) {
        log.info("[SHIPPING-CONTROLLER] POST /api/shippings - ordenId={}", request.getOrdenId());
        return new ResponseEntity<>(shippingService.createShipping(request), HttpStatus.CREATED);
    }

    // GET /api/shippings → Listar por orden o estado
    @GetMapping
    public ResponseEntity<List<ShippingResponse>> getShippings(
            @RequestParam(required = false) Long ordenId,
            @RequestParam(required = false) String estado) {
        log.info("[SHIPPING-CONTROLLER] GET /api/shippings - ordenId={}, estado={}", ordenId, estado);
        return ResponseEntity.ok(shippingService.getShippings(ordenId, estado));
    }

    // GET /api/shippings/{id} → Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<ShippingResponse> getShippingById(@PathVariable Long id) {
        log.info("[SHIPPING-CONTROLLER] GET /api/shippings/{}", id);
        return ResponseEntity.ok(shippingService.getShippingById(id));
    }

    // PUT /api/shippings/{id}/status → Actualizar estado y tracking
    @PutMapping("/{id}/status")
    public ResponseEntity<ShippingResponse> updateShippingStatus(
            @PathVariable Long id,
            @RequestParam String estado,
            @RequestParam(required = false) String tracking) {
        log.info("[SHIPPING-CONTROLLER] PUT /api/shippings/{}/status - estado={}", id, estado);
        return ResponseEntity.ok(shippingService.updateShippingStatus(id, estado, tracking));
    }

    // DELETE /api/shippings/{id} → Cancelar despacho
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelShipping(@PathVariable Long id) {
        log.info("[SHIPPING-CONTROLLER] DELETE /api/shippings/{}", id);
        shippingService.cancelShipping(id);
        return ResponseEntity.noContent().build();
    }
}