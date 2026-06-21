package com.GameHubStore.notification_service.assemblers;

import com.GameHubStore.notification_service.controller.NotificationControllerV2;
import com.GameHubStore.notification_service.model.dto.NotificationResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class NotificationModelAssembler
        implements RepresentationModelAssembler<NotificationResponse, EntityModel<NotificationResponse>> {

    @Override
    public EntityModel<NotificationResponse>toModel(NotificationResponse notification) {

        EntityModel<NotificationResponse> model = EntityModel.of(notification,
                linkTo(methodOn(NotificationControllerV2.class).findById(notification.getId()))
                        .withSelfRel(),
                linkTo(methodOn(NotificationControllerV2.class).findAll())
                        .withRel("notifications"),
                linkTo(methodOn(NotificationControllerV2.class).listByUser(notification.getUserId()))
                        .withRel("notificationsByUser"));

        // Solo se ofrece la acción de marcar como leída si la notificación aún no fue leída
        if (Boolean.FALSE.equals(notification.getRead())) {
            model.add(linkTo(methodOn(NotificationControllerV2.class).markAsRead(notification.getId()))
                    .withRel("markAsRead"));
        }

        return model;
    }
}