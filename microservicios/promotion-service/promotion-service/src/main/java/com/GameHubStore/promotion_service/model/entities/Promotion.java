package com.GameHubStore.promotion_service.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "promotion")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;


    @Column(nullable = false, length = 20)
    private String type;

    @Column(nullable = false)
    private Double discountAmount;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Double minAmount;

    @Column(nullable = false)
    private Integer maxUses;

    @Column(nullable = false)
    private Integer currentUses;


    private Long productId;
    private Long categoryId;

    @Column(nullable = false)
    private Boolean isActive;
}