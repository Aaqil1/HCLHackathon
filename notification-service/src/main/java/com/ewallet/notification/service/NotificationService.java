package com.ewallet.notification.service;

import com.ewallet.notification.dto.PaymentNotificationEvent;
import com.ewallet.notification.entity.Merchant;
import com.ewallet.notification.entity.NotificationEvent;
import com.ewallet.notification.repository.MerchantRepository;
import com.ewallet.notification.repository.NotificationEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationEventRepository notificationEventRepository;
    private final MerchantRepository merchantRepository;
    private final ObjectMapper objectMapper;

    public NotificationService(NotificationEventRepository notificationEventRepository,
                              MerchantRepository merchantRepository,
                              ObjectMapper objectMapper) {
        this.notificationEventRepository = notificationEventRepository;
        this.merchantRepository = merchantRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void processPaymentNotification(PaymentNotificationEvent event) {
        logger.info("Processing payment notification event for paymentId: {}", event.getPaymentId());

        try {
            Optional<Merchant> merchantOpt = merchantRepository.findById(event.getMerchantId());
            if (merchantOpt.isEmpty()) {
                logger.error("Merchant not found: {}", event.getMerchantId());
                return;
            }

            Merchant merchant = merchantOpt.get();

            // Send email notification only
            if (merchant.getNotificationEmail() != null && !merchant.getNotificationEmail().isEmpty()) {
                NotificationEvent emailNotification = createNotificationEvent(
                    event, merchant, "EMAIL", merchant.getNotificationEmail());
                sendNotification(emailNotification);
                logger.info("Payment notification processed successfully for paymentId: {}", event.getPaymentId());
            } else {
                logger.warn("Merchant {} has no notification email configured", event.getMerchantId());
            }

        } catch (Exception e) {
            logger.error("Error processing payment notification for paymentId: {}", 
                event.getPaymentId(), e);
        }
    }

    private NotificationEvent createNotificationEvent(PaymentNotificationEvent event, 
                                                      Merchant merchant, 
                                                      String channel, 
                                                      String destination) {
        NotificationEvent notification = new NotificationEvent();
        notification.setPaymentId(event.getPaymentId());
        notification.setMerchantId(event.getMerchantId());
        notification.setWalletAccountId(event.getWalletAccountId());
        notification.setChannel(channel);
        notification.setDestination(destination);
        notification.setStatus("PENDING");

        try {
            String payload = objectMapper.writeValueAsString(event);
            notification.setPayload(payload);
        } catch (Exception e) {
            logger.warn("Error serializing notification payload", e);
        }

        return notificationEventRepository.save(notification);
    }

    private void sendNotification(NotificationEvent notification) {
        try {
            logger.info("Sending {} notification to {} for paymentId: {}", 
                notification.getChannel(), notification.getDestination(), notification.getPaymentId());

            // Simulate sending notification
            // In production, this would call actual email service, webhook, SMS gateway, etc.
            Thread.sleep(100); // Simulate network delay

            notification.setStatus("SENT");
            notification.setSentAt(LocalDateTime.now());
            notificationEventRepository.save(notification);

            logger.info("Notification sent successfully. NotificationId: {}", notification.getId());

        } catch (Exception e) {
            logger.error("Error sending notification. NotificationId: {}", notification.getId(), e);
            notification.setStatus("FAILED");
            notification.setFailureReason(e.getMessage());
            notificationEventRepository.save(notification);
        }
    }

    public NotificationResponse triggerPaymentSuccessNotification(Long paymentId, Long merchantId, 
                                                                  java.math.BigDecimal amount) {
        logger.info("Triggering payment success notification for paymentId: {}", paymentId);

        PaymentNotificationEvent event = new PaymentNotificationEvent();
        event.setPaymentId(paymentId);
        event.setMerchantId(merchantId);
        event.setAmount(amount);
        event.setStatus("COMPLETED");

        processPaymentNotification(event);

        String batchId = "notif-" + UUID.randomUUID().toString().substring(0, 8);
        return new NotificationResponse(batchId, "ACCEPTED");
    }
}

