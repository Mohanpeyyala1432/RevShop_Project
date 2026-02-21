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
            log.error("Attempted to send notification to null user");
            throw new IllegalArgumentException("User cannot be null");
        }

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setReadStatus(false);

        notificationRepository.save(notification);

        log.info("Notification successfully sent to user {} with message: {}",
                user.getEmail(), message);
    }

    @Override
    public List<Notification> getUserNotifications(User user) {

        if (user == null) {
            log.error("Attempted to fetch notifications for null user");
            throw new IllegalArgumentException("User cannot be null");
        }

        log.info("Fetching notifications for user {}", user.getEmail());

        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Override
    public void markAsRead(Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> {
                    log.error("Notification not found with ID {}", notificationId);
                    return new ResourceNotFoundException("Notification not found");
                });

        notification.setReadStatus(true);
        notificationRepository.save(notification);

        log.info("Notification with ID {} marked as read", notificationId);
    }
}