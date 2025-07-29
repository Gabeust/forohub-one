package com.gabeust.forohub.controller;

import com.gabeust.forohub.entity.Notification;
import com.gabeust.forohub.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Controlador REST para exponer API de notificaciones.
 */
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    /**
     * Obtiene las notificaciones del usuario por parámetro.
     * @param userId ID del usuario
     * @return lista de notificaciones
     */
    @GetMapping
    public ResponseEntity<List<Notification>> getNotificationsForUser(@RequestParam Long userId) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }
    /**
     * Marca la notificación como leída.
     * @param id ID de la notificación
     * @return 204 No Content si éxito
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markNotificationRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }
}
