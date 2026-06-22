package com.GameHubStore.promotion_service.controller;

import com.GameHubStore.promotion_service.assemblers.PromotionModelAssembler;
import com.GameHubStore.promotion_service.model.dto.PromotionRequest;
import com.GameHubStore.promotion_service.model.dto.PromotionResponse;
import com.GameHubStore.promotion_service.service.PromotionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v2/promotion")
@RequiredArgsConstructor
@Tag(name = "Promotions V2 (HATEOAS)", description = "Operaciones relacionadas con las promociones de GameHub Store, con enlaces HATEOAS")
public class PromotionControllerV2 {

    private final PromotionService promotionService;
    private final PromotionModelAssembler assembler;

    // POST /api/v2/promotion — Create promotion
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear una promoción", description = "Crea una nueva promoción en el sistema y retorna sus enlaces HATEOAS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Promoción creada exitosamente",
                    content = @Content(mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = PromotionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de la promoción inválidos",
                    content = @Content)
    })
    public EntityModel<PromotionResponse> createPromotion(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la promoción a crear", required = true)
            @RequestBody @Valid PromotionRequest request) {
        return assembler.toModel(promotionService.createPromotion(request));
    }

    // GET /api/v2/promotion/active — Get active promotions
    @GetMapping(value = "/active", produces = MediaTypes.HAL_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Obtener promociones activas", description = "Retorna una lista de todas las promociones activas con enlaces HATEOAS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de promociones activas obtenida exitosamente",
                    content = @Content(mediaType = MediaTypes.HAL_JSON_VALUE))
    })
    public CollectionModel<EntityModel<PromotionResponse>> getActivePromotions() {
        List<EntityModel<PromotionResponse>> promotions = promotionService.getActivePromotions()
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(promotions,
                linkTo(methodOn(PromotionControllerV2.class).getActivePromotions()).withSelfRel(),
                linkTo(methodOn(PromotionControllerV2.class).getAllPromotions()).withRel("promotions"));
    }

    // GET /api/v2/promotion — Get all promotions
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Obtener todas las promociones", description = "Retorna una lista con todas las promociones registradas, con enlaces HATEOAS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de promociones obtenida exitosamente",
                    content = @Content(mediaType = MediaTypes.HAL_JSON_VALUE))
    })
    public CollectionModel<EntityModel<PromotionResponse>> getAllPromotions() {
        List<EntityModel<PromotionResponse>> promotions = promotionService.getAllPromotions()
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(promotions,
                linkTo(methodOn(PromotionControllerV2.class).getAllPromotions()).withSelfRel(),
                linkTo(methodOn(PromotionControllerV2.class).getActivePromotions()).withRel("activePromotions"));
    }

    // GET /api/v2/promotion/{id} — Get promotion by ID
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Obtener promoción por ID", description = "Retorna una promoción según su ID junto a sus enlaces HATEOAS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promoción encontrada exitosamente",
                    content = @Content(mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = PromotionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Promoción no encontrada",
                    content = @Content)
    })
    public EntityModel<PromotionResponse> getPromotionById(
            @Parameter(description = "ID de la promoción", required = true, example = "1")
            @PathVariable Long id) {
        return assembler.toModel(promotionService.getPromotionById(id));
    }

    // GET /api/v2/promotion/code/{code} — Get promotion by code
    @GetMapping(value = "/code/{code}", produces = MediaTypes.HAL_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Obtener promoción por código", description = "Retorna una promoción según su código único, con enlaces HATEOAS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promoción encontrada exitosamente",
                    content = @Content(mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = PromotionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Promoción no encontrada",
                    content = @Content)
    })
    public EntityModel<PromotionResponse> getPromotionByCode(
            @Parameter(description = "Código único de la promoción", required = true, example = "PROMO2026")
            @PathVariable String code) {
        return assembler.toModel(promotionService.getPromotionByCode(code));
    }

    // PUT /api/v2/promotion/{id} — Update promotion
    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Actualizar una promoción", description = "Actualiza los datos de una promoción existente y retorna sus enlaces HATEOAS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promoción actualizada exitosamente",
                    content = @Content(mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = PromotionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de la promoción inválidos",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Promoción no encontrada",
                    content = @Content)
    })
    public EntityModel<PromotionResponse> updatePromotion(
            @Parameter(description = "ID de la promoción a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevos datos de la promoción", required = true)
            @RequestBody @Valid PromotionRequest request) {
        return assembler.toModel(promotionService.updatePromotion(id, request));
    }

    // PATCH /api/v2/promotion/{id}/desactivate — Deactivate promotion
    @PatchMapping(value = "/{id}/desactivate", produces = MediaTypes.HAL_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Desactivar una promoción", description = "Cambia el estado de una promoción a inactiva y retorna sus enlaces HATEOAS actualizados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promoción desactivada exitosamente",
                    content = @Content(mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = PromotionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Promoción no encontrada",
                    content = @Content)
    })
    public EntityModel<PromotionResponse> desactivatePromotion(
            @Parameter(description = "ID de la promoción a desactivar", required = true, example = "1")
            @PathVariable Long id) {
        return assembler.toModel(promotionService.desactivatePromotion(id));
    }
}