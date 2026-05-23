package com.GameHubStore.warranty_service.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CloseWarrantyRequest {

    @NotBlank(message = "La resolución es obligatoria para cerrar la garantía")
    @Size(max = 500, message = "La resolución no puede superar los 500 caracteres")
    private String resolution;
}