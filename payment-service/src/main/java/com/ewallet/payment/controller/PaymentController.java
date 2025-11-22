package com.ewallet.payment.controller;

import com.ewallet.payment.dto.*;
import com.ewallet.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@Tag(name = "Payment API", description = "APIs for payment processing")
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/initiate")
    @Operation(summary = "Initiate and validate a new payment")
    public ResponseEntity<PaymentInitiateResponse> initiatePayment(
            @Valid @RequestBody PaymentInitiateRequest request) {
        logger.info("POST /payments/initiate - customerId: {}, merchantId: {}, productId: {}, quantity: {}", 
            request.getCustomerId(), request.getMerchantId(), request.getProductId(), request.getQuantity());
        try {
            PaymentInitiateResponse response = paymentService.initiatePayment(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            logger.error("Error initiating payment", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{paymentId}/process")
    @Operation(summary = "Process a validated payment")
    public ResponseEntity<ProcessPaymentResponse> processPayment(
            @PathVariable("paymentId") Long paymentId,
            @Valid @RequestBody ProcessPaymentRequest request) {
        logger.info("POST /payments/{}/process", paymentId);
        try {
            ProcessPaymentResponse response = paymentService.processPayment(paymentId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("Error processing payment: {}", paymentId, e);
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{paymentId}/status")
    @Operation(summary = "Get payment status")
    public ResponseEntity<PaymentStatusResponse> getPaymentStatus(@PathVariable Long paymentId) {
        logger.info("GET /payments/{}/status", paymentId);
        try {
            PaymentStatusResponse response = paymentService.getPaymentStatus(paymentId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("Error getting payment status: {}", paymentId, e);
            return ResponseEntity.notFound().build();
        }
    }
}

