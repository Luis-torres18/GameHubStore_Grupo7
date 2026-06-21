package com.GameHubStore.notification_service.controller;

import com.GameHubStore.notification_service.assemblers.NotificationModelAssembler;
import com.GameHubStore.notification_service.model.dto.NotificationRequest;
import com.GameHubStore.notification_service.model.dto.NotificationResponse;
import com.GameHubStore.notification_service.service.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v2/notification")
@RequiredArgsConstructor
@Tag(name = "Notifications V2 (HATEOAS)", description = "Operaciones relacionadas con las notificaciones de usuarios en GameHub Store, con enlaces HATEOAS")
public class NotificationControllerV2 {

    private final NotificationService notificationService;
    private final NotificationModelAssembler assembler;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear una notificación", description = "Registra una nueva notificación en el sistema y retorna sus enlaces HATEOAS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Notificación creada exitosamente",
                    content = @Content(mediaType = "application/hal+json",
                            schema = @Schema(implementation = NotificationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de la notificación inválidos",
                    content = @Content)
    })
    public EntityModel<NotificationResponse> createNotification(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la notificación a crear", required = true)
            @RequestBody @Valid NotificationRequest request) {
        NotificationResponse created = notificationService.createNotification(request);
        return assembler.toModel(created);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Obtener todas las notificaciones", description = "Retorna todas las notificaciones registradas, incluyendo enlaces HATEOAS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de notificaciones obtenida exitosamente",
                    content = @Content(mediaType = "application/hal+json"))
    })
    public CollectionModel<EntityModel<NotificationResponse>> findAll() {
        List<EntityModel<NotificationResponse>> notifications = notificationService.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(notifications,
                linkTo(methodOn(NotificationControllerV2.class).findAll()).withSelfRel());
    }

    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Obtener notificaciones por usuario", description = "Retorna todas las notificaciones asociadas a un usuario específico, incluyendo enlaces HATEOAS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de notificaciones del usuario obtenida exitosamente",
                    content = @Content(mediaType = "application/hal+json"))
    })
    public CollectionModel<EntityModel<NotificationResponse>> listByUser(
            @Parameter(description = "ID del usuario", required = true, example = "1")
            @PathVariable Long userId) {
        List<EntityModel<NotificationResponse>> notifications = notificationService.listByUser(userId).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(notifications,
                linkTo(methodOn(NotificationControllerV2.class).listByUser(userId)).withSelfRel(),
                linkTo(methodOn(NotificationControllerV2.class).findAll()).withRel("notifications"));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Obtener notificación por ID", description = "Retorna la notificación correspondiente al ID indicado junto con sus enlaces HATEOAS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificación encontrada exitosamente",
                    content = @Content(mediaType = "application/hal+json",
                            schema = @Schema(implementation = NotificationResponse.class))),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada",
                    content = @Content)
    })
    public EntityModel<NotificationResponse> findById(
            @Parameter(description = "ID de la notificación", required = true, example = "1")
            @PathVariable Long id) {
        NotificationResponse notification = notificationService.findById(id);
        return assembler.toModel(notification);
    }

    @PatchMapping("/{id}/read")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Marcar notificación como leída", description = "Actualiza el estado de una notificación a leída y retorna sus enlaces HATEOAS actualizados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificación marcada como leída exitosamente",
                    content = @Content(mediaType = "application/hal+json",
                            schema = @Schema(implementation = NotificationResponse.class))),
            @ApiResponse(responseCode = "400", description = "La notificación ya estaba marcada como leída",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada",
                    content = @Content)
    })
    public EntityModel<NotificationResponse> markAsRead(
            @Parameter(description = "ID de la notificación a marcar como leída", required = true, example = "1")
            @PathVariable Long id) {
        NotificationResponse updated = notificationService.markAsRead(id);
        return assembler.toModel(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar una notificación", description = "Elimina una notificación del sistema según su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Notificación eliminada exitosamente",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada",
                    content = @Content)
    })
    public void deleteNotification(
            @Parameter(description = "ID de la notificación a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        notificationService.deleteNotification(id);
    }
}