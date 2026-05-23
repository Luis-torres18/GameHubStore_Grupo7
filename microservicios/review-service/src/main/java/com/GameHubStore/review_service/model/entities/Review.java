package com.GameHubStore.review_service.model.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Data // ¡Esta anotación de Lombok es la que soluciona los errores de getProductId(), etc!
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long productId;
    private Long orderId;
    private Integer score;
    private String comment;
    private Boolean status;
    private LocalDateTime date;
}