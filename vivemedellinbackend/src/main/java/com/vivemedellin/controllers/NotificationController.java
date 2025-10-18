package com.vivemedellin.controllers;

import com.vivemedellin.payloads.ApiResponse;
import com.vivemedellin.payloads.NotificationDto;
import com.vivemedellin.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getAllNotifications(Principal principal) {
        List<NotificationDto> notifications = notificationService.getNotificationsForUser(principal);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDto>> getUnreadNotifications(Principal principal) {
        List<NotificationDto> notifications = notificationService.getUnreadNotificationsForUser(principal);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadCount(Principal principal) {
        Long count = notificationService.getUnreadNotificationCount(principal);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse> markAsRead(@PathVariable Integer notificationId, Principal principal) {
        notificationService.markAsRead(notificationId, principal);
        return new ResponseEntity<>(new ApiResponse("Notification marked as read", true), HttpStatus.OK);
    }

    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse> markAllAsRead(Principal principal) {
        notificationService.markAllAsRead(principal);
        return new ResponseEntity<>(new ApiResponse("All notifications marked as read", true), HttpStatus.OK);
    }
}
