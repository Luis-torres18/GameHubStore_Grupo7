package com.GameHubStore.user_service.model.dto;

import com.GameHubStore.user_service.model.entities.Rol;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tine un formato valido")
    private String email;

    private String telefono;

    @NotNull(message = "El rol es obligatorio")
    private Rol rol;

    private Boolean estado = true;

    @NotNull(message = "La direccion es obligatoria")
    @Valid
    private DireccionRequest direccion;
}
