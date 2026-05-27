package com.GameHubStore.notification_service.service;


import com.GameHubStore.notification_service.exception.NotificationInvalidException;
import com.GameHubStore.notification_service.exception.NotificationNotFoundException;
import com.GameHubStore.notification_service.model.dto.NotificationRequest;
import com.GameHubStore.notification_service.model.dto.NotificationResponse;
import com.GameHubStore.notification_service.model.entities.Notification;
import com.GameHubStore.notification_service.repository.NotificationRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {


    private final NotificationRepository notificationRepository;




    @Override
    @Transactional
    public NotificationResponse createNotification(NotificationRequest request) {



        validateUserExists(request.getUserId());

        Notification notification = Notification.builder()
                .userId(request.getUserId())
                .type(request.getType().toUpperCase())
                .message(request.getMessage())
                .read(false)
                .status("ACTIVA")
                .build();

        Notification saved = notificationRepository.save(notification);


        return toResponse(saved);
    }
    @Override
    public List<NotificationResponse> findAll() {
        return notificationRepository.findAll()
                .stream()
                .map(this::toResponse) // Asumiendo que tienes este método de mapeo
                .collect(Collectors.toList());
    }


    @Override
    public List<NotificationResponse> listByUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }




    @Override
    public NotificationResponse findById(Long id) {
        Notification notification = getOrThrow(id);
        return toResponse(notification);
    }





    @Override
    @Transactional
    public NotificationResponse markAsRead(Long id) {
        Notification notification = getOrThrow(id);


        if (Boolean.TRUE.equals(notification.getRead())) {
            throw new NotificationInvalidException(
                    "La notificación ya fue marcada como leída");
        }

        notification.setRead(true);
        Notification saved = notificationRepository.save(notification);
        return toResponse(saved);
    }





    @Override
    @Transactional
    public void deleteNotification(Long id) {
        Notification notification = getOrThrow(id);
        notificationRepository.delete(notification);
    }


    private Notification getOrThrow(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> {
                    return new NotificationNotFoundException(
                            "Notificación no encontrada con ID: " + id);
                });
    }


    private void validateUserExists(Long userId) {
        try {

        } catch (FeignException.NotFound ex) {
            throw new NotificationInvalidException(
                    "No se puede crear la notificación: el usuario con ID " + userId + " no existe");
        }
    }


    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .userId(n.getUserId())
                .type(n.getType())
                .message(n.getMessage())
                .read(n.getRead())
                .status(n.getStatus())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
