package com.GameHubStore.shipping.controller;

import com.GameHubStore.shipping.model.dto.ShippingRequest;
import com.GameHubStore.shipping.model.dto.ShippingResponse;
import com.GameHubStore.shipping.service.ShippingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name="Shippings", description = "Operaciones relacionadas con el despacho de ordenes")
public class ShippingController {

    private static final Logger log = LoggerFactory.getLogger(ShippingController.class);
    private final ShippingService shippingService;

    // POST /api/shippings → Crear despacho
    @PostMapping
    @Operation(summary = "Crear despacho", description = "Crea un nuevo despacho para una orden pagada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Despacho de orden creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Orden no pagada o direccion invalida")
    })
    public ResponseEntity<ShippingResponse> createShipping(@Valid @RequestBody ShippingRequest request) {
        log.info("[SHIPPING-CONTROLLER] POST /api/shippings - ordenId={}", request.getOrdenId());
        return new ResponseEntity<>(shippingService.createShipping(request), HttpStatus.CREATED);
    }

    // GET /api/shippings → Listar por orden o estado
    @GetMapping
    @Operation(summary = "Listar despacho", description = "Retorna despachos filtrados por ordenId o estado")
    @ApiResponse(responseCode = "200", description = "Listado exitoso")
    public ResponseEntity<List<ShippingResponse>> getShippings(
            @Parameter(description = "ID del despacho", example = "1")
            @RequestParam(required = false) Long ordenId,
            @Parameter(description = "Estado del despacho", example = "Pendiente")
            @RequestParam(required = false) String estado) {
        log.info("[SHIPPING-CONTROLLER] GET /api/shippings - ordenId={}, estado={}", ordenId, estado);
        return ResponseEntity.ok(shippingService.getShippings(ordenId, estado));
    }

    // GET /api/shippings/{id} → Buscar por ID
    @GetMapping("/{id}")
    @Operation(summary = "Buscar despacho por ID", description = "Retorna un despacho especifico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Despacho encontrado"),
            @ApiResponse(responseCode = "404", description = "Despacho no encontrado")
    })
    public ResponseEntity<ShippingResponse> getShippingById(
            @Parameter(description = "Id del despacho",required = true,  example ="1")
            @PathVariable Long id) {
        log.info("[SHIPPING-CONTROLLER] GET /api/shippings/{}", id);
        return ResponseEntity.ok(shippingService.getShippingById(id));
    }

    // PUT /api/shippings/{id}/status → Actualizar estado y tracking
    @PutMapping("/{id}/status")
    @Operation(summary = "Crear despacho", description = "Crea un nuevo despacho para una orden pagada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Despacho actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "No se puede cambiar a Entregado sin fecha de envio o tracking duplicado"),
            @ApiResponse(responseCode = "404", description = "Despacho no encontrado ")
    })
    public ResponseEntity<ShippingResponse> updateShippingStatus(
            @Parameter(description = "ID del despacho", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nuevo estado", required = true, example = "Enviado")
            @RequestParam String estado,
            @Parameter(description = "Número de tracking", required = true, example = "TKR-12345")
            @RequestParam(required = false) String tracking) {
        log.info("[SHIPPING-CONTROLLER] PUT /api/shippings/{}/status - estado={}", id, estado);
        return ResponseEntity.ok(shippingService.updateShippingStatus(id, estado, tracking));
    }

    // DELETE /api/shippings/{id} → Cancelar despacho
    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar despacho", description = "Cancela un despacho si la orden fue anulada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Despacho de orden cancelado exitosamente"),
            @ApiResponse(responseCode = "400", description = "No se puede cancelar un despacho entregado"),
            @ApiResponse(responseCode = "404", description = "Despacho no encontrado")
    })
    public ResponseEntity<Void> cancelShipping(
            @Parameter(description = "ID del despacho", required = true, example = "1")
            @PathVariable Long id) {
        log.info("[SHIPPING-CONTROLLER] DELETE /api/shippings/{}", id);
        shippingService.cancelShipping(id);
        return ResponseEntity.noContent().build();
    }
}