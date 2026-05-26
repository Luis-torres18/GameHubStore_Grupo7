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
    private Long ordenId;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @NotBlank(message = "El transportista es obligatorio")
    private String transportista;

    // Regla: solo despachar órdenes pagadas
    // El estado de la orden debe ser PAID para poder crear el despacho
    @NotBlank(message = "El estado de la orden es obligatorio")
    private String estadoOrden;
}