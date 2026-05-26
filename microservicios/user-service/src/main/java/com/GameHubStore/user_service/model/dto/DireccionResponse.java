package com.GameHubStore.user_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DireccionResponse {

    private Long id;
    private Long usuarioId;
    private String comuna;
    private String ciudad;
    private String calle;
    private String numero;
}
