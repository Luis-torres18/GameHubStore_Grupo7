package com.GameHubStore.warranty_service.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WarrantyRequest {

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long userId;

    @NotNull(message = "El ID de la orden es obligatorio")
    private Long orderId;

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productId;

    @NotBlank(message = "El motivo de la garantía es obligatorio")
    @Size(max = 500, message = "El motivo no puede superar los 500 caracteres")
    private String reason;
}