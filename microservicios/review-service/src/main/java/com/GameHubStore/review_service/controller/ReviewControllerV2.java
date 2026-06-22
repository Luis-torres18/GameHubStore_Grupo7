package com.GameHubStore.review_service.controller;

import com.GameHubStore.review_service.assemblers.ReviewModelAssembler;
import com.GameHubStore.review_service.model.dto.ReviewRequest;
import com.GameHubStore.review_service.model.dto.ReviewResponse;
import com.GameHubStore.review_service.model.dto.UpdateReviewRequest;
import com.GameHubStore.review_service.service.ReviewService;

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
@RequestMapping("/api/v2/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews V2 (HATEOAS)", description = "Operaciones relacionadas con las reseñas de productos, con enlaces HATEOAS")
public class ReviewControllerV2 {

    private final ReviewService reviewService;
    private final ReviewModelAssembler assembler;

    // POST /api/v2/reviews — Create review
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear una reseña", description = "Crea una nueva reseña para un producto comprado y retorna sus enlaces HATEOAS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reseña creada exitosamente",
                    content = @Content(mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = ReviewResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content)
    })
    public EntityModel<ReviewResponse> createReview(
            @RequestBody @Valid ReviewRequest request) {
        return assembler.toModel(reviewService.createReview(request));
    }

    // GET /api/v2/reviews — Get all reviews
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Obtener todas las reseñas", description = "Retorna la lista completa de reseñas registradas, con enlaces HATEOAS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de reseñas obtenida exitosamente",
                    content = @Content(mediaType = MediaTypes.HAL_JSON_VALUE))
    })
    public CollectionModel<EntityModel<ReviewResponse>> findAllReviews() {
        List<EntityModel<ReviewResponse>> reviews = reviewService.findAll()
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(reviews,
                linkTo(methodOn(ReviewControllerV2.class).findAllReviews()).withSelfRel());
    }

    // GET /api/v2/reviews/product/{productId} — Get reviews by product
    @GetMapping(value = "/product/{productId}", produces = MediaTypes.HAL_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Obtener reseñas por producto", description = "Retorna todas las reseñas asociadas a un producto específico, con enlaces HATEOAS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reseñas obtenidas exitosamente",
                    content = @Content(mediaType = MediaTypes.HAL_JSON_VALUE)),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content)
    })
    public CollectionModel<EntityModel<ReviewResponse>> getReviewsByProduct(
            @Parameter(description = "ID del producto", required = true, example = "1")
            @PathVariable Long productId) {
        List<EntityModel<ReviewResponse>> reviews = reviewService.getReviewsByProduct(productId)
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(reviews,
                linkTo(methodOn(ReviewControllerV2.class).getReviewsByProduct(productId)).withSelfRel(),
                linkTo(methodOn(ReviewControllerV2.class).findAllReviews()).withRel("reviews"));
    }

    // GET /api/v2/reviews/user/{userId} — Get reviews by user
    @GetMapping(value = "/user/{userId}", produces = MediaTypes.HAL_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Obtener reseñas por usuario", description = "Retorna todas las reseñas realizadas por un usuario específico, con enlaces HATEOAS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reseñas obtenidas exitosamente",
                    content = @Content(mediaType = MediaTypes.HAL_JSON_VALUE)),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content)
    })
    public CollectionModel<EntityModel<ReviewResponse>> getReviewsByUser(
            @Parameter(description = "ID del usuario", required = true, example = "1")
            @PathVariable Long userId) {
        List<EntityModel<ReviewResponse>> reviews = reviewService.getReviewsByUser(userId)
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(reviews,
                linkTo(methodOn(ReviewControllerV2.class).getReviewsByUser(userId)).withSelfRel(),
                linkTo(methodOn(ReviewControllerV2.class).findAllReviews()).withRel("reviews"));
    }

    // GET /api/v2/reviews/{id} — Get review by ID
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Obtener reseña por ID", description = "Busca y retorna una reseña según su identificador, con enlaces HATEOAS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reseña encontrada exitosamente",
                    content = @Content(mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = ReviewResponse.class))),
            @ApiResponse(responseCode = "404", description = "Reseña no encontrada",
                    content = @Content)
    })
    public EntityModel<ReviewResponse> getReviewById(
            @Parameter(description = "ID de la reseña", required = true, example = "1")
            @PathVariable Long id) {
        return assembler.toModel(reviewService.getReviewById(id));
    }

    // PUT /api/v2/reviews/{id} — Update review
    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Actualizar reseña", description = "Actualiza el comentario o calificación de una reseña existente, con enlaces HATEOAS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reseña actualizada exitosamente",
                    content = @Content(mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = ReviewResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Reseña no encontrada",
                    content = @Content)
    })
    public EntityModel<ReviewResponse> updateReview(
            @Parameter(description = "ID de la reseña a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody @Valid UpdateReviewRequest request) {
        return assembler.toModel(reviewService.updateReview(id, request));
    }

    // PATCH /api/v2/reviews/{id}/moderate — Moderate review
    @PatchMapping(value = "/{id}/moderate", produces = MediaTypes.HAL_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Moderar reseña", description = "Cambia el estado de una reseña a MODERADA y retorna sus enlaces HATEOAS actualizados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reseña moderada exitosamente",
                    content = @Content(mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = ReviewResponse.class))),
            @ApiResponse(responseCode = "404", description = "Reseña no encontrada",
                    content = @Content)
    })
    public EntityModel<ReviewResponse> moderateReview(
            @Parameter(description = "ID de la reseña a moderar", required = true, example = "1")
            @PathVariable Long id) {
        return assembler.toModel(reviewService.moderateReview(id));
    }

    // DELETE /api/v2/reviews/{id} — Delete review
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar reseña", description = "Elimina o desactiva una reseña según su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reseña eliminada exitosamente",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Reseña no encontrada",
                    content = @Content)
    })
    public void deleteReview(
            @Parameter(description = "ID de la reseña a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        reviewService.deleteReview(id);
    }
}