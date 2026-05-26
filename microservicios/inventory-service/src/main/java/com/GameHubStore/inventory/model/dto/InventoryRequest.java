package com.GameHubStore.inventory.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryRequest {

    @NotNull(message = "productId is required")
    private Long productId;

    @NotNull(message = "availableStock is required")
    @Min(value = 0, message = "availableStock cannot be negative")
    private Integer availableStock;

    @NotNull(message = "minimumStock is required")
    @Min(value = 0, message = "minimumStock cannot be negative")
    private Integer minimumStock;

    private String Location;
}