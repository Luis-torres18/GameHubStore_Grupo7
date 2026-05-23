package com.GameHubStore.warranty_service.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "warranties")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Warranty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false, length = 500)
    private String reason;

    // PENDING | IN_REVIEW | APPROVED | REJECTED | CLOSED
    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false)
    private LocalDate requestDate;

    @Column(length = 500)
    private String diagnosis;

    @Column(length = 500)
    private String resolution;
}