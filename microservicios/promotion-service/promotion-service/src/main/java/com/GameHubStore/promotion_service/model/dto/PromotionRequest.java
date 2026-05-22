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
    private String codigo;

    @NotBlank(message = "El tipo de promoción es obligatorio (PORCENTAJE | MONTO_FIJO)")
    private String tipo;

    @NotNull(message = "El valor del descuento es obligatorio")
    @Positive(message = "El valor del descuento debe ser mayor que cero")
    private Double valor;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    @NotNull(message = "El monto mínimo es obligatorio")
    @PositiveOrZero(message = "El monto mínimo no puede ser negativo")
    private Double montoMinimo;

    @NotNull(message = "Los usos máximos son obligatorios")
    @Positive(message = "Los usos máximos deben ser mayor que cero")
    private Integer usosMaximos;

    // Opcionales: aplica a un producto o categoría específica
    private Long productoId;
    private Long categoriaId;

    @Builder.Default
    private Boolean estado = true;
}