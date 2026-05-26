package com.GameHubStore.user_service.model.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateRequest {

    private String nombre;
    private String telefono;

    @Valid
    private DireccionRequest direccion;
}
