package com.GameHubStore.user_service.model.dto;

import com.GameHubStore.user_service.model.entities.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private Rol rol;
    private Boolean estado;
    private DireccionResponse direccion;
}
