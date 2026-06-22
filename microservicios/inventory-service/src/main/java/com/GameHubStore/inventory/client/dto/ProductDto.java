package com.GameHubStore.inventory.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {
    private Long id;
    private String nombre;
    private String marca;
    private String modelo;
    private Double precio;
    private String categoriaId;
    private String descripcion;
    private Boolean estado;
}
