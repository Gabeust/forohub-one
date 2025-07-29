package com.gabeust.forohub.service;

import com.gabeust.forohub.entity.Notification;
import com.gabeust.forohub.repository.INotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * Servicio para gestionar notificaciones: consultar y marcar como leídas.
 */
@Service
public class NotificationService {

    private final INotificationRepository notificationRepository;

    public NotificationService(INotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
    /**
     * Obtiene las notificaciones de un usuario.
     * @param userId ID del usuario
     * @return Lista de notificaciones ordenadas por fecha
     */
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(userId);
    }
    /**
     * Marca una notificación como leída.
     * @param notificationId ID de la notificación
     */
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }
}

