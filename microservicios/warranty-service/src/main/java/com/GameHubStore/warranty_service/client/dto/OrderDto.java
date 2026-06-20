package com.GameHubStore.warranty_service.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
