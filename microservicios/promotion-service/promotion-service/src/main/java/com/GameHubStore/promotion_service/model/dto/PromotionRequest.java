package com.GameHubStore.promotion_service.model.dto;


import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromotionRequest {

    @NotBlank(message = "El código de la promoción es obligatorio")
    private String code;

    @NotBlank(message = "El tipo de promoción es obligatorio (PORCENTAJE | MONTO_FIJO)")
    private String type;

    @NotNull(message = "El valor del descuento es obligatorio")
    @Positive(message = "El valor del descuento debe ser mayor que cero")
    private Double discountAmount;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate startDate;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate endDate;

    @NotNull(message = "El monto mínimo es obligatorio")
    @PositiveOrZero(message = "El monto mínimo no puede ser negativo")
    private Double minAmount;

    @NotNull(message = "Los usos máximos son obligatorios")
    @Positive(message = "Los usos máximos deben ser mayor que cero")
    private Integer maxUses;


    private Long productId;
    private Long categoryId;

    @Builder.Default
    private Boolean isActive = true;
}