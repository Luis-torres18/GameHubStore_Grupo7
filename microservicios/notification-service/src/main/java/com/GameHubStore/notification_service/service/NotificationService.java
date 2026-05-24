package com.GameHubStore.notification_service.service;

import com.GameHubStore.notification_service.model.dto.NotificationRequest;
import com.GameHubStore.notification_service.model.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {


    NotificationResponse createNotification(NotificationRequest request);


    List<NotificationResponse> listByUser(Long userId);


    List<NotificationResponse> listUnreadByUser(Long userId);


    NotificationResponse findById(Long id);


    NotificationResponse markAsRead(Long id);


    NotificationResponse archiveNotification(Long id);


    void deleteNotification(Long id);


    long countUnread(Long userId);
}
