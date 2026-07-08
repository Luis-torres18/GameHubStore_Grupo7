package com.GameHubStore.notification_service.Service;


import com.GameHubStore.notification_service.model.dto.NotificationRequest;
import com.GameHubStore.notification_service.model.dto.NotificationResponse;
import com.GameHubStore.notification_service.model.entities.Notification;
import com.GameHubStore.notification_service.repository.NotificationRepository;
import com.GameHubStore.notification_service.service.NotificationServiceImpl;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    // ───────────────────────────────────────────
    // Mocks
    // ───────────────────────────────────────────

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private final Faker faker = new Faker();

    private Notification notification;
    private NotificationRequest request;
    private Long fakeUserId;
    private String fakeMessage;
    private String fakeType;



    @BeforeEach
    void setUp() {
        fakeUserId = faker.number().numberBetween(1L, 100L);
        fakeMessage = faker.lorem().sentence();
        fakeType = "ALERTA";

        request = NotificationRequest.builder()
                .userId(fakeUserId)
                .type(fakeType.toLowerCase())
                .message(fakeMessage)
                .build();

        notification = Notification.builder()
                .id(faker.number().numberBetween(1L, 100L))
                .userId(fakeUserId)
                .type(fakeType.toUpperCase())
                .message(fakeMessage)
                .read(false)
                .status("ACTIVA")
                .createdAt(LocalDateTime.now())
                .build();
    }

    // ───────────────────────────────────────────
    // createNotification
    // ───────────────────────────────────────────

    @Test
    @DisplayName("createNotification - Crea notificación exitosamente")
    void createNotification_Success() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        NotificationResponse response = notificationService.createNotification(request);

        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(fakeUserId);
        assertThat(response.getType()).isEqualTo("ALERTA");
        assertThat(response.getMessage()).isEqualTo(fakeMessage);
        assertThat(response.getRead()).isFalse();
        assertThat(response.getStatus()).isEqualTo("ACTIVA");
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("createNotification - El tipo se convierte a mayúsculas al guardar")
    void createNotification_TypeIsUppercased_BeforeSaving() {
        request.setType("alerta");
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

    }
}
