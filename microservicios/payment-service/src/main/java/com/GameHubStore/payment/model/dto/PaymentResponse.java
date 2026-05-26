package com.GameHubStore.payment.model.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse {

    private Long id;
    private Long ordenId;
    private Double monto;
    private String metodo;
    private String estado;
    private String codigoTransaccion;
    private LocalDateTime fecha;
}