package com.GameHubStore.payment.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotNull(message = "El ID de la orden es obligatorio")
    private Long ordenId;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    private Double monto;

    @NotBlank(message = "El método de pago es obligatorio")
    private String metodo;

    @NotBlank(message = "El código de transacción es obligatorio")
    private String codigoTransaccion;
}