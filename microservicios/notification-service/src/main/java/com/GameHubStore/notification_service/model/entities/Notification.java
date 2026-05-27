package com.GameHubStore.notification_service.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "notifications")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "user_id", nullable = false)
    private Long userId;


    @Column(nullable = false, length = 50)
    private String type;


    @Column(nullable = false, length = 500)
    private String message;


    @Column(nullable = false)
    private Boolean read;


    @Column(nullable = false, length = 20)
    private String status;


    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.read == null)   this.read   = false;
        if (this.status == null) this.status = "ACTIVA";
    }
}
