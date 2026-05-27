package com.GameHubStore.notification_service.model.dto;

import lombok.*;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationResponse {

    private Long id;
    private Long userId;
    private String type;
    private String message;
    private Boolean read;
    private String status;
    private LocalDateTime createdAt;
}
