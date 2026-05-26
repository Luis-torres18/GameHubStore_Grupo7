package com.GameHubStore.shipping.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "shippings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ordenId;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private String transportista;

    // Regla: tracking único cuando exista
    @Column(unique = true)
    private String tracking;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private LocalDateTime fechaEnvio;

    // Necesario para regla: no cambiar a entregado sin fecha de entrega
    private LocalDateTime fechaEntrega;
}