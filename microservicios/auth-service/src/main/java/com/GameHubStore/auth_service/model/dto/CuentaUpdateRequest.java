package com.GameHubStore.auth_service.model.dto;

import com.GameHubStore.auth_service.model.entities.Rol;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CuentaUpdateRequest {

    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    private Rol rol;

    private Boolean estado;
}