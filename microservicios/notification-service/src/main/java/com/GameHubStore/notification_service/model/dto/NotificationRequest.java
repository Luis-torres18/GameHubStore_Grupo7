package com.GameHubStore.notification_service.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationRequest {

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long userId;


    @NotBlank(message = "El tipo de notificación es obligatorio")
    private String type;

    @NotBlank(message = "El mensaje de la notificación es obligatorio")
    @Size(max = 500, message = "El mensaje no puede superar los 500 caracteres")
    private String message;
}
