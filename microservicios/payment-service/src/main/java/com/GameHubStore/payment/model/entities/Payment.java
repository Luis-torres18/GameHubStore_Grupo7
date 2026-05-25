package com.GameHubStore.payment.model.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ordenId;

    @Column(nullable = false)
    private Double monto;

    @Column(nullable = false)
    private String metodo;

    @Column(nullable = false)
    private String estado;

    @Column(unique = true, nullable = false)
    private String codigoTransaccion;

    @Column(nullable = false)
    private LocalDateTime fecha;
}