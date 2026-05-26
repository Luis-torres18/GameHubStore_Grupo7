package com.GameHubStore.auth_service.model.dto;

import com.GameHubStore.auth_service.model.entities.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CuentaResponse {

    private Long id;
    private String email;
    private Rol rol;
    private Boolean estado;
    private LocalDateTime fechaCreacion;
}