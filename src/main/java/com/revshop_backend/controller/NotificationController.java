package com.revshop_backend.controller;

import com.revshop_backend.model.Notification;
import com.revshop_backend.model.User;
import com.revshop_backend.services.interfaces.NotificationService;
import com.revshop_backend.services.implementations.CartServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buyer/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final CartServiceImpl cartService;

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications() {
        User user = cartService.getLoggedInUser();
        List<Notification> notifications = notificationService.getUserNotifications(user);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<String> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok("Notification marked as read");
    }
}
