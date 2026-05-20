package com.GameHubStore.product_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {

    private Long id;
    private String nombre;
    private String marca;
    private String modelo;
    private Double precio;
    private String categoriaId;
    private String descripcion;
    private Boolean estado;
}
