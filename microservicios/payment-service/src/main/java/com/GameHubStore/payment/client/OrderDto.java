package com.GameHubStore.payment.client;

import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {

    private Long id;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private String status;
    private Double total;
    private LocalDateTime createdAt;
}