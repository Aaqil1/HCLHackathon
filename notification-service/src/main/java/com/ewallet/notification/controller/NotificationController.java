package com.ewallet.notification.controller;

import com.ewallet.notification.dto.NotificationResponse;
import com.ewallet.notification.dto.PaymentSuccessNotificationRequest;
import com.ewallet.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@Tag(name = "Notification API", description = "APIs for payment notifications")
public class NotificationController {
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/payment-success")
    @Operation(summary = "Trigger payment success notification")
    public ResponseEntity<NotificationResponse> triggerPaymentSuccessNotification(
            @Valid @RequestBody PaymentSuccessNotificationRequest request) {
        logger.info("POST /notifications/payment-success - paymentId: {}, merchantId: {}", 
            request.getPaymentId(), request.getMerchantId());
        try {
            NotificationResponse response = notificationService.triggerPaymentSuccessNotification(
                request.getPaymentId(), request.getMerchantId(), request.getAmount());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        } catch (Exception e) {
            logger.error("Error triggering payment success notification", e);
            return ResponseEntity.badRequest().build();
        }
    }
}

