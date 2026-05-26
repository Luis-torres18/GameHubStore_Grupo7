package com.GameHubStore.shipping.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShippingResponse {

    private Long id;
    private Long ordenId;
    private Long usuarioId;
    private String direccion;
    private String transportista;
    private String tracking;
    private String estado;
    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaEntrega;
}