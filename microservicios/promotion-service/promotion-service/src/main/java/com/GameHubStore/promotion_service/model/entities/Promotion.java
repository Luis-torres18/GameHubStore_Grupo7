package com.GameHubStore.promotion_service.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "promociones")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    // PORCENTAJE | MONTO_FIJO
    @Column(nullable = false, length = 20)
    private String tipo;

    @Column(nullable = false)
    private Double valor;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false)
    private Double montoMinimo;

    @Column(nullable = false)
    private Integer usosMaximos;

    @Column(nullable = false)
    private Integer usosActuales;

    // Referencia opcional a producto o categoría específica (null = aplica a todo)
    private Long productoId;
    private Long categoriaId;

    @Column(nullable = false)
    private Boolean estado;
}