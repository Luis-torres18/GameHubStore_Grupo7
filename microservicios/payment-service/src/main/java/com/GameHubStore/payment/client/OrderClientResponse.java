package com.GameHubStore.payment.client;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderClientResponse {
    private Long id;
    private Long userId;
    private String status;
    private Double total;
    private LocalDateTime createdAt;
}
