package com.GameHubStore.inventory.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryRequest {
    private Long productId;
    private Integer availableStock;
    private Integer minimumStock;
    private String Location;

}
