package com.GameHubStore.auth_service.model.dto;

import com.GameHubStore.auth_service.model.entities.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {

    private String token;
    private String tipo = "Bearer";
    private Long id;
    private String email;
    private Rol rol;
}