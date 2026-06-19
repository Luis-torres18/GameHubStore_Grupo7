package com.GameHubStore.promotion_service.controller;

import com.GameHubStore.promotion_service.model.dto.PromotionRequest;
import com.GameHubStore.promotion_service.model.dto.PromotionResponse;
import com.GameHubStore.promotion_service.service.PromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotion")
@RequiredArgsConstructor
@Tag(name = "Promotions", description = "Operaciones relacionadas con las promociones de GameHub Store")
public class PromotionController {

    private final PromotionService promotionService;

    // crear promocion
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear una promoción", description = "Crea una nueva promoción en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Promoción creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PromotionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de la promoción inválidos",
                    content = @Content)
    })
    public PromotionResponse createPromotion(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la promoción a crear", required = true)
            @RequestBody @Valid PromotionRequest request) {
        return promotionService.createPromotion(request);
    }

    // obtener promociones activas
    @GetMapping("/active")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Obtener promociones activas", description = "Retorna una lista de todas las promociones activas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de promociones activas obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PromotionResponse.class))))
    })
    public List<PromotionResponse> getActivePromotions() {
        return promotionService.getActivePromotions();
    }

    // obtener todas las promociones
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Obtener todas las promociones", description = "Retorna una lista con todas las promociones registradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de promociones obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PromotionResponse.class))))
    })
    public List<PromotionResponse> getAllPromotions() {
        return promotionService.getAllPromotions();
    }

    // obtener promocion por id
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Obtener promoción por ID", description = "Retorna una promoción según su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promoción encontrada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PromotionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Promoción no encontrada",
                    content = @Content)
    })
    public PromotionResponse getPromotionById(
            @Parameter(description = "ID de la promoción", required = true, example = "1")
            @PathVariable Long id) {
        return promotionService.getPromotionById(id);
    }

    // obtener promocion por codigo
    @GetMapping("/code/{code}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Obtener promoción por código", description = "Retorna una promoción según su código único")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promoción encontrada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PromotionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Promoción no encontrada",
                    content = @Content)
    })
    public PromotionResponse getPromotionByCode(
            @Parameter(description = "Código único de la promoción", required = true, example = "PROMO2026")
            @PathVariable String code) {
        return promotionService.getPromotionByCode(code);
    }

    // actualizar promocion
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Actualizar una promoción", description = "Actualiza los datos de una promoción existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promoción actualizada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PromotionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de la promoción inválidos",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Promoción no encontrada",
                    content = @Content)
    })
    public PromotionResponse updatePromotion(
            @Parameter(description = "ID de la promoción a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevos datos de la promoción", required = true)
            @RequestBody @Valid PromotionRequest request) {
        return promotionService.updatePromotion(id, request);
    }

    // desactivar promocion
    @PatchMapping("/{id}/desactivate")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Desactivar una promoción", description = "Cambia el estado de una promoción a inactiva")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promoción desactivada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PromotionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Promoción no encontrada",
                    content = @Content)
    })
    public PromotionResponse desactivatePromotion(
            @Parameter(description = "ID de la promoción a desactivar", required = true, example = "1")
            @PathVariable Long id) {
        return promotionService.desactivatePromotion(id);
    }
}