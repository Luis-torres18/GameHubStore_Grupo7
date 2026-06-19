package com.GameHubStore.order.client;

import lombok.Data;

@Data
public class DireccionResponse {
    private Long id;
    private Long usuarioId;
    private String comuna;
    private String ciudad;
    private String calle;
    private String numero;
}
