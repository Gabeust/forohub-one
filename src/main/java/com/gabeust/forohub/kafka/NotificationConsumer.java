package com.gabeust.forohub.kafka;

import com.gabeust.forohub.dto.NotificationEvent;
import com.gabeust.forohub.entity.Notification;
import com.gabeust.forohub.repository.INotificationRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
/**
 * Servicio consumidor que escucha eventos Kafka y guarda las notificaciones en la base de datos.
 */
@Service
public class NotificationConsumer {

    private final INotificationRepository notificationRepository;

    public NotificationConsumer(INotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
    /**
     * Método que escucha mensajes en el topic "forum-notifications"
     * @param event Evento recibido desde Kafka
     */
    @KafkaListener(topics = "forum-notifications", groupId = "forum-notifications-group")
    public void consume(NotificationEvent event) {
        Notification notification = new Notification();
        notification.setRecipientUserId(event.recipientUserId());
        notification.setType(event.type());
        notification.setMessage(event.message());
        notification.setPostId(event.postId());
        notification.setCommentId(event.commentId());
        notification.setReactionId(event.reactionId());

        notificationRepository.save(notification);

    }
}
