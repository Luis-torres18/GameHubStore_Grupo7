package com.GameHubStore.notification_service.repository;

import com.GameHubStore.notification_service.model.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {


    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    




}
