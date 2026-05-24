package com.GameHubStore.notification_service.controller;

import com.GameHubStore.notification_service.model.dto.NotificationRequest;
import com.GameHubStore.notification_service.model.dto.NotificationResponse;
import com.GameHubStore.notification_service.service.NotificationService;

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

public class NotificationController {

    private final NotificationService notificationService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationResponse createNotification(@RequestBody @Valid NotificationRequest request) {
        return notificationService.createNotification(request);
    }




    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<NotificationResponse> listByUser(@PathVariable Long userId) {
        return notificationService.listByUser(userId);
    }


    @GetMapping("/user/{userId}/unread")
    @ResponseStatus(HttpStatus.OK)
    public List<NotificationResponse> listUnread(@PathVariable Long userId) {
        return notificationService.listUnreadByUser(userId);
    }


    @GetMapping("/user/{userId}/countUnread")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Long> countUnread(@PathVariable Long userId) {
        return Map.of("noLeidas", notificationService.countUnread(userId));
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
