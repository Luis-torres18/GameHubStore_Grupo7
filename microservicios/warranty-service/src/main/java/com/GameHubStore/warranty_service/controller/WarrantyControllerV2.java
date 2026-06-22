package com.GameHubStore.warranty_service.controller;

import com.GameHubStore.warranty_service.assemblers.WarrantyModelAssembler;
import com.GameHubStore.warranty_service.model.dto.CloseWarrantyRequest;
import com.GameHubStore.warranty_service.model.dto.UpdateWarrantyRequest;
import com.GameHubStore.warranty_service.model.dto.WarrantyRequest;
import com.GameHubStore.warranty_service.model.dto.WarrantyResponse;
import com.GameHubStore.warranty_service.service.WarrantyService;

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
@RequestMapping("/api/v2/warranty")
@RequiredArgsConstructor
@Tag(name = "Warranties V2 (HATEOAS)", description = "Operaciones relacionadas con las garantías de GameHub Store, con enlaces HATEOAS")
public class WarrantyControllerV2 {

    private final WarrantyService warrantyService;
    private final WarrantyModelAssembler assembler;

    // POST /api/v2/warranty — Create warranty
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear una garantía", description = "Crea una nueva solicitud de garantía y retorna sus enlaces HATEOAS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Garantía creada exitosamente",
                    content = @Content(mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = WarrantyResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de la garantía inválidos",
                    content = @Content)
    })
    public EntityModel<WarrantyResponse> createWarranty(
            @RequestBody @Valid WarrantyRequest request) {
        return assembler.toModel(warrantyService.createWarranty(request));
    }

    // GET /api/v2/warranty — Get all warranties
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Obtener todas las garantías", description = "Retorna la lista completa de garantías registradas, con enlaces HATEOAS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de garantías obtenida exitosamente",
                    content = @Content(mediaType = MediaTypes.HAL_JSON_VALUE))
    })
    public CollectionModel<EntityModel<WarrantyResponse>> findAllWarranties() {
        List<EntityModel<WarrantyResponse>> warranties = warrantyService.findAll()
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(warranties,
                linkTo(methodOn(WarrantyControllerV2.class).findAllWarranties()).withSelfRel());
    }

    // GET /api/v2/warranty/user/{userId} — Get warranties by user
    @GetMapping(value = "/user/{userId}", produces = MediaTypes.HAL_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Obtener garantías por usuario", description = "Retorna todas las garantías asociadas a un usuario específico, con enlaces HATEOAS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Garantías obtenidas exitosamente",
                    content = @Content(mediaType = MediaTypes.HAL_JSON_VALUE)),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content)
    })
    public CollectionModel<EntityModel<WarrantyResponse>> findByUser(
            @Parameter(description = "ID del usuario", required = true, example = "1")
            @PathVariable Long userId) {
        List<EntityModel<WarrantyResponse>> warranties = warrantyService.findByUser(userId)
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(warranties,
                linkTo(methodOn(WarrantyControllerV2.class).findByUser(userId)).withSelfRel(),
                linkTo(methodOn(WarrantyControllerV2.class).findAllWarranties()).withRel("warranties"));
    }

    // GET /api/v2/warranty/product/{productId} — Get warranties by product
    @GetMapping(value = "/product/{productId}", produces = MediaTypes.HAL_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Obtener garantías por producto", description = "Retorna todas las garantías asociadas a un producto específico, con enlaces HATEOAS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Garantías obtenidas exitosamente",
                    content = @Content(mediaType = MediaTypes.HAL_JSON_VALUE)),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content)
    })
    public CollectionModel<EntityModel<WarrantyResponse>> findByProduct(
            @Parameter(description = "ID del producto", required = true, example = "1")
            @PathVariable Long productId) {
        List<EntityModel<WarrantyResponse>> warranties = warrantyService.findByProduct(productId)
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(warranties,
                linkTo(methodOn(WarrantyControllerV2.class).findByProduct(productId)).withSelfRel(),
                linkTo(methodOn(WarrantyControllerV2.class).findAllWarranties()).withRel("warranties"));
    }

    // GET /api/v2/warranty/status/{status} — Get warranties by status
    @GetMapping(value = "/status/{status}", produces = MediaTypes.HAL_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Obtener garantías por estado", description = "Retorna todas las garantías con un estado específico, con enlaces HATEOAS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Garantías obtenidas exitosamente",
                    content = @Content(mediaType = MediaTypes.HAL_JSON_VALUE))
    })
    public CollectionModel<EntityModel<WarrantyResponse>> findByStatus(
            @Parameter(description = "Estado de la garantía (ej: PENDING, CLOSED)", required = true, example = "PENDING")
            @PathVariable String status) {
        List<EntityModel<WarrantyResponse>> warranties = warrantyService.findByStatus(status)
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(warranties,
                linkTo(methodOn(WarrantyControllerV2.class).findByStatus(status)).withSelfRel(),
                linkTo(methodOn(WarrantyControllerV2.class).findAllWarranties()).withRel("warranties"));
    }

    // GET /api/v2/warranty/{id} — Find warranty by ID
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Obtener garantía por ID", description = "Busca y retorna una garantía según su identificador, con enlaces HATEOAS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Garantía encontrada exitosamente",
                    content = @Content(mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = WarrantyResponse.class))),
            @ApiResponse(responseCode = "404", description = "Garantía no encontrada",
                    content = @Content)
    })
    public EntityModel<WarrantyResponse> findById(
            @Parameter(description = "ID de la garantía", required = true, example = "1")
            @PathVariable Long id) {
        return assembler.toModel(warrantyService.findById(id));
    }

    // PUT /api/v2/warranty/{id} — Update warranty
    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Actualizar garantía", description = "Actualiza el estado o diagnóstico de una garantía existente, con enlaces HATEOAS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Garantía actualizada exitosamente",
                    content = @Content(mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = WarrantyResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de la garantía inválidos",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Garantía no encontrada",
                    content = @Content)
    })
    public EntityModel<WarrantyResponse> updateWarranty(
            @Parameter(description = "ID de la garantía a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody @Valid UpdateWarrantyRequest request) {
        return assembler.toModel(warrantyService.updateWarranty(id, request));
    }

    // PATCH /api/v2/warranty/{id}/close — Close warranty
    @PatchMapping(value = "/{id}/close", produces = MediaTypes.HAL_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Cerrar garantía", description = "Cierra una solicitud de garantía con su resolución final y retorna sus enlaces HATEOAS actualizados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Garantía cerrada exitosamente",
                    content = @Content(mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = WarrantyResponse.class))),
            @ApiResponse(responseCode = "400", description = "No se puede cerrar sin resolución",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Garantía no encontrada",
                    content = @Content)
    })
    public EntityModel<WarrantyResponse> closeWarranty(
            @Parameter(description = "ID de la garantía a cerrar", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody @Valid CloseWarrantyRequest request) {
        return assembler.toModel(warrantyService.closeWarranty(id, request));
    }
}