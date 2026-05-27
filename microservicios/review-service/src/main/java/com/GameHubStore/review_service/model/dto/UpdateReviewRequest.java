package com.GameHubStore.review_service.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateReviewRequest {

    @NotNull(message = "La puntuación es obligatoria")
    @Min(value = 1, message = "La puntuación mínima es 1")
    @Max(value = 5, message = "La puntuación máxima es 5")
    private Integer score;

    @NotBlank(message = "El comentario no puede estar vacío")
    private String comment;
}