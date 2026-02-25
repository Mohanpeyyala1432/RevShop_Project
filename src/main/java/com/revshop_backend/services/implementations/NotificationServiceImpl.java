package com.revshop_backend.services.implementations;

import com.revshop_backend.exception.ResourceNotFoundException;
import com.revshop_backend.model.Notification;
import com.revshop_backend.model.User;
import com.revshop_backend.repository.NotificationRepository;
import com.revshop_backend.services.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public void sendNotification(User user, String message) {

        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setReadStatus(false);

        notificationRepository.save(notification);

        log.info("Notification sent to user {}: {}", user.getEmail(), message);
    }

    @Override
    public List<Notification> getUserNotifications(User user) {

        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Override
    public void markAsRead(Long notificationId) {

        if (notificationId == null) {
            throw new IllegalArgumentException("Notification ID cannot be null");
        }

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Notification not found"));

        notification.setReadStatus(true);
        notificationRepository.save(notification);

        log.info("Notification {} marked as read", notificationId);
    }
}