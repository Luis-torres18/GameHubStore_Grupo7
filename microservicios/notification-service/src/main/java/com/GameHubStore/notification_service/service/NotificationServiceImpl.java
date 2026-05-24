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

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;




    @Override
    @Transactional
    public NotificationResponse createNotification(NotificationRequest request) {
        log.info("[notification-service] Creating notification – type='{}', userId={}",
                request.getType(), request.getUserId());


        validateUserExists(request.getUserId());

        Notification notification = Notification.builder()
                .userId(request.getUserId())
                .type(request.getType().toUpperCase())
                .message(request.getMessage())
                .read(false)
                .status("ACTIVA")
                .build();

        Notification saved = notificationRepository.save(notification);
        log.info("[notification-service] Notification created – id={}, userId={}",
                saved.getId(), saved.getUserId());

        return toResponse(saved);
    }



    @Override
    public List<NotificationResponse> listByUser(Long userId) {
        log.info("[notification-service] Listing all notifications for userId={}", userId);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponse> listUnreadByUser(Long userId) {
        log.info("[notification-service] Listing unread notifications for userId={}", userId);
        return notificationRepository.findByUserIdAndRead(userId, false)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationResponse findById(Long id) {
        log.info("[notification-service] Fetching notification id={}", id);
        Notification notification = getOrThrow(id);
        return toResponse(notification);
    }

    @Override
    public long countUnread(Long userId) {
        long total = notificationRepository.countByUserIdAndReadFalse(userId);
        log.info("[notification-service] User {} has {} unread notification(s)", userId, total);
        return total;
    }



    @Override
    @Transactional
    public NotificationResponse markAsRead(Long id) {
        log.info("[notification-service] Marking notification id={} as read", id);
        Notification notification = getOrThrow(id);


        if (Boolean.TRUE.equals(notification.getRead())) {
            log.warn("[notification-service] Notification id={} is already read – ignoring", id);
            throw new NotificationInvalidException(
                    "La notificación ya fue marcada como leída");
        }

        notification.setRead(true);
        Notification saved = notificationRepository.save(notification);
        log.info("[notification-service] Notification id={} marked as read", saved.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public NotificationResponse archiveNotification(Long id) {
        log.info("[notification-service] Archiving notification id={}", id);
        Notification notification = getOrThrow(id);


        if ("ARCHIVADA".equals(notification.getStatus())) {
            log.warn("[notification-service] Notification id={} is already archived – ignoring", id);
            throw new NotificationInvalidException(
                    "La notificación ya está archivada");
        }

        notification.setStatus("ARCHIVADA");
        Notification saved = notificationRepository.save(notification);
        log.info("[notification-service] Notification id={} archived", saved.getId());
        return toResponse(saved);
    }



    @Override
    @Transactional
    public void deleteNotification(Long id) {
        log.info("[notification-service] Deleting notification id={}", id);
        Notification notification = getOrThrow(id);
        notificationRepository.delete(notification);
        log.info("[notification-service] Notification id={} deleted", id);
    }


    private Notification getOrThrow(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[notification-service] Notification not found – id={}", id);
                    return new NotificationNotFoundException(
                            "Notificación no encontrada con ID: " + id);
                });
    }


    private void validateUserExists(Long userId) {
        try {

        } catch (FeignException.NotFound ex) {
            log.error("[notification-service] User not found in user-service – userId={}", userId);
            throw new NotificationInvalidException(
                    "No se puede crear la notificación: el usuario con ID " + userId + " no existe");
        } catch (FeignException ex) {

            log.warn("[notification-service] user-service unavailable (userId={}): {} – proceeding anyway",
                    userId, ex.getMessage());
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
