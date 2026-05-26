package com.GameHubStore.inventory.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryResponse {

    private Long id;
    private Long productId;
    private Integer availableStock;
    private Integer reservedStock;
    private Integer minimumStock;
    private String Location;
}