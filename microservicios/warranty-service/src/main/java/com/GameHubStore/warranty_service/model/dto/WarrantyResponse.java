package com.GameHubStore.warranty_service.model.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WarrantyResponse {

    private Long id;
    private Long userId;
    private Long orderId;
    private Long productId;
    private String reason;
    private String status;
    private LocalDate requestDate;
    private String diagnosis;
    private String resolution;
}