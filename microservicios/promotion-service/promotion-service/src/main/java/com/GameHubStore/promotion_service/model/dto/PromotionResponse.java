package com.GameHubStore.promotion_service.model.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromotionResponse {

    private Long id;
    private String code;
    private String type;
    private Double value;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double minAmount;
    private Integer maxUses;
    private Integer currentUses;
    private Long productId;
    private Long categoryId;
    private Boolean status;
    private Boolean isValid;
}