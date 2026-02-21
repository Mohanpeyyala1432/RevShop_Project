package com.revshop_backend.services.implementations;

import com.revshop_backend.exception.ResourceNotFoundException;
import com.revshop_backend.model.Notification;
import com.revshop_backend.model.User;
import com.revshop_backend.repository.NotificationRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Mock
    private NotificationRepository notificationRepository;

     @Test
    void testSendNotification_Success() {

        User user = User.builder()
                .id(1L)
                .email("vijay@test.com")
                .name("Vijay")
                .build();

        notificationService.sendNotification(user, "Order placed successfully");

        verify(notificationRepository, times(1))
                .save(any(Notification.class));
    }

     @Test
    void testSendNotification_NullUser() {

        assertThrows(IllegalArgumentException.class,
                () -> notificationService.sendNotification(null, "Message"));
    }

     @Test
    void testGetUserNotifications_Success() {

        User user = User.builder()
                .id(1L)
                .email("vijay@test.com")
                .name("Vijay")
                .build();

        Notification notification = new Notification();
        notification.setMessage("Order confirmed");
        notification.setUser(user);

        when(notificationRepository.findByUserOrderByCreatedAtDesc(user))
                .thenReturn(List.of(notification));

        List<Notification> result =
                notificationService.getUserNotifications(user);

        assertEquals(1, result.size());
        assertEquals("Order confirmed", result.get(0).getMessage());
    }

     @Test
    void testGetUserNotifications_NullUser() {

        assertThrows(IllegalArgumentException.class,
                () -> notificationService.getUserNotifications(null));
    }

     @Test
    void testMarkAsRead_Success() {

        Notification notification = new Notification();
        notification.setId(1L);
        notification.setReadStatus(false);

        when(notificationRepository.findById(1L))
                .thenReturn(Optional.of(notification));

        notificationService.markAsRead(1L);

        assertTrue(notification.isReadStatus());
        verify(notificationRepository, times(1))
                .save(notification);
    }

     @Test
    void testMarkAsRead_NotFound() {

        when(notificationRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> notificationService.markAsRead(1L));
    }
}