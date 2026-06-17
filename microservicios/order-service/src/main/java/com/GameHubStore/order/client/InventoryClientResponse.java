package com.GameHubStore.order.client;

import lombok.Data;

@Data
public class InventoryClientResponse {
    private Long id;
    private Long productId;
    private Integer availableStock;
    private Integer reserveStock;
    private String location;
}
