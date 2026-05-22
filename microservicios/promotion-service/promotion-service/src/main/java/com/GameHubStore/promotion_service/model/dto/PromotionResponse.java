package com.GameHubStore.promotion_service.model.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromotionResponse {

    private Long id;
    private String codigo;
    private String tipo;
    private Double valor;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Double montoMinimo;
    private Integer usosMaximos;
    private Integer usosActuales;
    private Long productoId;
    private Long categoriaId;
    private Boolean estado;
    private Boolean vigente;
}