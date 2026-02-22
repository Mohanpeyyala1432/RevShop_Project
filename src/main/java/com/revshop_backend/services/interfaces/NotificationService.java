package com.revshop_backend.services.interfaces;

import com.revshop_backend.model.Notification;
import com.revshop_backend.model.User;

import java.util.List;

public interface NotificationService {

    void sendNotification(User user, String message);

    List<Notification> getUserNotifications(User user);

    void markAsRead(Long notificationId);
}
