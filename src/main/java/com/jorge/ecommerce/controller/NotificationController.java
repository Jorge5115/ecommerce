package com.jorge.ecommerce.controller;

import com.jorge.ecommerce.dto.NotificationDTO;
import com.jorge.ecommerce.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Endpoints de notificaciones")
public class NotificationController {

    private final NotificationService notificationService;

    @MessageMapping("/ping")
    @SendTo("/topic/pong")
    public NotificationDTO ping() {
        return new NotificationDTO("PING", "Pong", "WebSocket connection active");
    }

    @Operation(summary = "Enviar notificación a todos los admins")
    @PostMapping("/api/admin/notifications/broadcast")
    @PreAuthorize("hasRole('ADMIN')")
    public void broadcastNotification(@RequestBody NotificationDTO notification) {
        notificationService.sendAdminNotification(notification);
    }
}
