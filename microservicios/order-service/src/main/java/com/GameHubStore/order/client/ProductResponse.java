package com.GameHubStore.order.client;

import lombok.Data;

@Data
public class ProductResponse {
    private Long id;
    private String nombre;
    private String marca;
    private String modelo;
    private Double precio;
    private String categoryId;
    private String description;
    private Boolean estado;
}
