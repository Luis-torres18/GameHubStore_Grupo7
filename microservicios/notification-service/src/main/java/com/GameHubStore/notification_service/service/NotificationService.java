package com.GameHubStore.notification_service.service;

import com.GameHubStore.notification_service.model.dto.NotificationRequest;
import com.GameHubStore.notification_service.model.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {


    NotificationResponse createNotification(NotificationRequest request);
    List<NotificationResponse> findAll();

    List<NotificationResponse> listByUser(Long userId);





    NotificationResponse findById(Long id);


    NotificationResponse markAsRead(Long id);




    void deleteNotification(Long id);



}
