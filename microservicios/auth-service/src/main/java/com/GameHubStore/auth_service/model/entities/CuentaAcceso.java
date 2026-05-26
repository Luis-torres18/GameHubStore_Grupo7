package com.GameHubStore.auth_service.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cuentas_acceso")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CuentaAcceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Column(nullable = false)
    private Boolean estado;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "CuentaAcceso {" +
                "id=" + id +
                ", email=" + email +
                ", rol=" + rol +
                ", estado=" + estado +
                ", fechaCreacion=" + fechaCreacion +
                "}";
    }
}