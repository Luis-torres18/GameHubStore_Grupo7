package com.GameHubStore.user_service.model.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "direcciones")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Direccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private String comuna;

    @Column(nullable = false)
    private String ciudad;

    @Column(nullable = false)
    private String calle;

    @Column(nullable = false)
    private String numero;

    @Override
    public String toString() {
        return "Direccion{" +
                "id=" + id +
                ", usuarioId=" + usuarioId +
                ", comuna='" + comuna + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", calle='" + calle + '\'' +
                ", numero='" + numero + '\'' +
                '}';
    }
}
