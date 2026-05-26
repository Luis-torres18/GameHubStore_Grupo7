package com.GameHubStore.category_service.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryRequest {

    @NotBlank(message = "El nombre de la categoría no puede estar vacío")
    private String nombre;
    private String descripcion;
    private Boolean estado;
}
