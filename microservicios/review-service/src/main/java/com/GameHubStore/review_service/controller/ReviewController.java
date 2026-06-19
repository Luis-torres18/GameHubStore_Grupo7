package com.GameHubStore.review_service.controller;

import com.GameHubStore.review_service.model.dto.ReviewRequest;
import com.GameHubStore.review_service.model.dto.UpdateReviewRequest;
import com.GameHubStore.review_service.model.dto.ReviewResponse;
import com.GameHubStore.review_service.service.ReviewService;
import feign.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Operaciones relacionadas con las reseñas de productos")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "Crear una reseña", description = "Crea una nueva reseña para un producto comprado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reseña creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse createReview(@RequestBody @Valid ReviewRequest request) {
        return reviewService.createReview(request);
    }

    @Operation(summary = "Obtener todas las reseñas", description = "Retorna la lista completa de reseñas registradas")
    @ApiResponse(responseCode = "200", description = "Lista de reseñas obtenida exitosamente")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ReviewResponse> findAllReviews() {
        return reviewService.findAll();
    }

    @Operation(summary = "Obtener reseñas por producto", description = "Retorna todas las reseñas asociadas a un producto específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reseñas obtenidas exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/product/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public List<ReviewResponse> getReviewsByProduct(@PathVariable Long productId) {
        return reviewService.getReviewsByProduct(productId);
    }

    @Operation(summary = "Obtener reseñas por usuario", description = "Retorna todas las reseñas realizadas por un usuario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reseñas obtenidas exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<ReviewResponse> getReviewsByUser(@PathVariable Long userId) {
        return reviewService.getReviewsByUser(userId);
    }

    @Operation(summary = "Obtener reseña por ID", description = "Busca y retorna una reseña según su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reseña encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Reseña no encontrada")
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ReviewResponse getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id);
    }

    @Operation(summary = "Actualizar reseña", description = "Actualiza el comentario o calificación de una reseña existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reseña actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Reseña no encontrada")
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ReviewResponse updateReview(
            @PathVariable Long id,
            @RequestBody @Valid UpdateReviewRequest request) {
        return reviewService.updateReview(id, request);
    }

    @Operation(summary = "Moderar reseña", description = "Cambia el estado de una reseña a MODERADA")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reseña moderada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Reseña no encontrada")
    })
    @PatchMapping("/{id}/moderate")
    @ResponseStatus(HttpStatus.OK)
    public ReviewResponse moderateReview(@PathVariable Long id) {
        return reviewService.moderateReview(id);
    }

    @Operation(summary = "Eliminar reseña", description = "Elimina o desactiva una reseña según su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reseña eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Reseña no encontrada")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> deleteReview(@PathVariable Long id) {
        String message  = reviewService.deleteReview(id);
        return ResponseEntity.ok(message);
    }
}