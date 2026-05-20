package com.GameHubStore.product_service.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombre;

    @NotBlank(message = "La marca del producto es obligatoria")
    private String marca;

    @NotBlank(message = "El modelo del producto es obligatorio")
    private String modelo;

    @NotBlank(message = "El precio del producto es obligatorio")
    @Positive(message = "El precio debe ser mayor que cero")
    private Double precio;

    @NotBlank(message = "La categoria del producto es obligatoria")
    private String categoriaId;

    private String descripcion;

    private Boolean estado = true;
}
