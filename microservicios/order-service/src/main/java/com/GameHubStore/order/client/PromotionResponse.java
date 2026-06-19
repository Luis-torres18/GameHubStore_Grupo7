package com.GameHubStore.order.client;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PromotionResponse {
private Long id;
private String code;
private String type;
private Double discountAmount;
private LocalDate startDate;
private LocalDate endDate;
private Double minAmount;
private Integer maxUses;
private Integer currentUses;
private long productId;
private Long categoryId;
private Boolean isActive;
private Boolean isValid;}
