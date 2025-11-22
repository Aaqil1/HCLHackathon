package com.ewallet.payment.kafka;

import com.ewallet.payment.dto.PaymentNotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentNotificationProducer {
    private static final Logger logger = LoggerFactory.getLogger(PaymentNotificationProducer.class);
    private static final String TOPIC = "payment-notifications";

    private final KafkaTemplate<String, PaymentNotificationEvent> kafkaTemplate;

    public PaymentNotificationProducer(KafkaTemplate<String, PaymentNotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPaymentNotification(PaymentNotificationEvent event) {
        try {
            logger.info("Publishing payment notification event to Kafka topic: {} for paymentId: {}", 
                TOPIC, event.getPaymentId());
            kafkaTemplate.send(TOPIC, event);
            logger.info("Successfully published payment notification event for paymentId: {}", 
                event.getPaymentId());
        } catch (Exception e) {
            logger.error("Error publishing payment notification event to Kafka for paymentId: {}", 
                event.getPaymentId(), e);
            throw new RuntimeException("Failed to publish notification event", e);
        }
    }
}

