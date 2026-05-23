package com.GameHubStore.promotion_service.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplyPromotionRequest {

    @NotBlank(message = "El código del cupón es obligatorio")
    private String code;

    @NotNull(message = "El monto de la orden es obligatorio")
    @Positive(message = "El monto de la orden debe ser mayor que cero")
    private Double orderAmount;
}
