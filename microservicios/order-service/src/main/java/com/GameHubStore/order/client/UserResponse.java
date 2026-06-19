package com.GameHubStore.order.client;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String rol;
    private Boolean estado;
    private  DireccionResponse direccion;
}
