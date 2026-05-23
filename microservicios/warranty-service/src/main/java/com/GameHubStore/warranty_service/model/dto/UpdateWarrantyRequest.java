package com.GameHubStore.warranty_service.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateWarrantyRequest {

    // PENDING | IN_REVIEW | APPROVED | REJECTED
    @NotBlank(message = "El estado es obligatorio")
    private String status;

    @Size(max = 500, message = "El diagnóstico no puede superar los 500 caracteres")
    private String diagnosis;
}