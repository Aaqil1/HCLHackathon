package com.ewallet.notification.kafka;

import com.ewallet.notification.dto.PaymentNotificationEvent;
import com.ewallet.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentNotificationConsumer {
    private static final Logger logger = LoggerFactory.getLogger(PaymentNotificationConsumer.class);

    private final NotificationService notificationService;

    public PaymentNotificationConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "payment-notifications", groupId = "notification-service-group")
    public void consumePaymentNotification(PaymentNotificationEvent event) {
        logger.info("Received payment notification event from Kafka for paymentId: {}", 
            event.getPaymentId());
        try {
            notificationService.processPaymentNotification(event);
        } catch (Exception e) {
            logger.error("Error processing payment notification from Kafka for paymentId: {}", 
                event.getPaymentId(), e);
        }
    }
}

