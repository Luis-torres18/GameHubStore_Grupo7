package com.GameHubStore.shipping.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShippingRequest {

    @NotNull(message = "El ID de la orden es obligatorio")
    private Long orderId;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    @NotBlank(message = "La dirección de envío es obligatoria")
    private String direccion;

    @NotBlank(message = "La empresa transportista es obligatoria")
    private String transportista;
}