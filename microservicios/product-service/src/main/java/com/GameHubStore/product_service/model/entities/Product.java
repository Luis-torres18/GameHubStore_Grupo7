package com.GameHubStore.product_service.model.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String marca;
    private String modelo;
    private Double precio;
    private String categoriaId;
    private String descripcion;
    private Boolean estado;

    @Override
    public String toString() {
        return "Product {" +
                "id=" + id +
                ", nombre=" + nombre +
                ", marca=" + marca +
                ", modelo=" + modelo +
                ", precio=" + precio +
                ", categoriaId=" + categoriaId +
                ", descripcion=" + descripcion +
                ", estado=" + estado +
                "}";
    }

}