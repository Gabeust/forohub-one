package com.gabeust.forohub.kafka;

import com.gabeust.forohub.dto.NotificationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Servicio para enviar eventos de notificación a Kafka.
 */
@Service
public class NotificationProducer {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;
    // Nombre del topic Kafka donde se publican los eventos
    private static final String TOPIC = "forum-notifications";

    public NotificationProducer(KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    /**
     * Envía el evento de notificación al topic Kafka.
     * @param event Evento de notificación a enviar.
     */
    public void sendNotification(NotificationEvent event) {
        kafkaTemplate.send(TOPIC, event);
    }
}

