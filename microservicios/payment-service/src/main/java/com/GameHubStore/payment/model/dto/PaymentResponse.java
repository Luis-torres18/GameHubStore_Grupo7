package com.GameHubStore.payment.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {

    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private String method;
    private String status;
    private String transactionCode;
    private LocalDateTime date;
}