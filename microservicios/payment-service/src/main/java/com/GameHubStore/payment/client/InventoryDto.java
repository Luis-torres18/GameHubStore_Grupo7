package com.GameHubStore.payment.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryDto {
    private Long id;
    private Long productId;
    private Integer availableStock;
    private Integer reservedStock;
    private Integer minimumStock;
    private String location; // Nota: Cambié "Location" a minúscula "location" por buenas prácticas de Java
}
