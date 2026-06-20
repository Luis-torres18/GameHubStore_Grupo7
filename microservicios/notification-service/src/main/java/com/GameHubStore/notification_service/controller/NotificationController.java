package com.GameHubStore.notification_service.controller;

import com.GameHubStore.notification_service.model.dto.NotificationRequest;
import com.GameHubStore.notification_service.model.dto.NotificationResponse;
import com.GameHubStore.notification_service.service.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.TableGenerator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Operaciones relacionadas con las notificaciones de usuarios en GameHub Store")
public class NotificationController {

    private final NotificationService notificationService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear una notificacion", description = "Registra una nueva noptificacion  en el sistema ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Notificacion creada con exito"),
            @ApiResponse
    }


    )
    public NotificationResponse createNotification(@RequestBody @Valid NotificationRequest request) {
        return notificationService.createNotification(request);
    }

    // List all warranties
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<NotificationResponse> findAllWarranties() {
        return notificationService.findAll();
    }


    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<NotificationResponse> listByUser(@PathVariable Long userId) {
        return notificationService.listByUser(userId);
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<NotificationResponse> findById(@PathVariable Long id) {
        return Collections.singletonList(notificationService.findById(id));
    }


    @PatchMapping("/{id}/read")
    @ResponseStatus(HttpStatus.OK)
    public NotificationResponse markAsRead(@PathVariable Long id) {
        return notificationService.markAsRead(id);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
    }
}
