package com.GameHubStore.review_service.model.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private Long id;
    private Long userId;
    private Long productId;
    private Long orderId;
    private Integer score;
    private String comment;
    private Boolean status;
    private LocalDateTime date;
}