package com.ewallet.payment.service;

import com.ewallet.payment.dto.*;
import com.ewallet.payment.entity.*;
import com.ewallet.payment.feign.WalletServiceClient;
import com.ewallet.payment.kafka.PaymentNotificationProducer;
import com.ewallet.payment.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    private static final BigDecimal WALLET_FEE_PERCENTAGE = new BigDecimal("0.02"); // 2%

    private final PaymentRepository paymentRepository;
    private final MerchantRepository merchantRepository;
    private final ProductRepository productRepository;
    private final SettlementRepository settlementRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final SupportedCurrencyRepository supportedCurrencyRepository;
    private final WalletServiceClient walletServiceClient;
    private final PaymentNotificationProducer notificationProducer;
    private final AuditService auditService;

    public PaymentService(PaymentRepository paymentRepository,
                         MerchantRepository merchantRepository,
                         ProductRepository productRepository,
                         SettlementRepository settlementRepository,
                         WalletTransactionRepository walletTransactionRepository,
                         SupportedCurrencyRepository supportedCurrencyRepository,
                         WalletServiceClient walletServiceClient,
                         PaymentNotificationProducer notificationProducer,
                         AuditService auditService) {
        this.paymentRepository = paymentRepository;
        this.merchantRepository = merchantRepository;
        this.productRepository = productRepository;
        this.settlementRepository = settlementRepository;
        this.walletTransactionRepository = walletTransactionRepository;
        this.supportedCurrencyRepository = supportedCurrencyRepository;
        this.walletServiceClient = walletServiceClient;
        this.notificationProducer = notificationProducer;
        this.auditService = auditService;
    }

    @Transactional
    public PaymentInitiateResponse initiatePayment(PaymentInitiateRequest request) {
        logger.info("Initiating and validating payment for customerId: {}, merchantId: {}, productId: {}, quantity: {}", 
            request.getCustomerId(), request.getMerchantId(), request.getProductId(), request.getQuantity());

        // Validate merchant
        Optional<Merchant> merchantOpt = merchantRepository.findByIdAndStatus(
            request.getMerchantId(), "ACTIVE");
        if (merchantOpt.isEmpty()) {
            throw new RuntimeException("Merchant not found or inactive: " + request.getMerchantId());
        }

        // Get and validate product
        Optional<Product> productOpt = productRepository.findByIdAndMerchantIdAndStatus(
            request.getProductId(), request.getMerchantId(), "ACTIVE");
        if (productOpt.isEmpty()) {
            throw new RuntimeException("Product not found or inactive: " + request.getProductId() + 
                " for merchant: " + request.getMerchantId());
        }

        Product product = productOpt.get();
        
        // Calculate amount from product cost * quantity
        BigDecimal amount = product.getCost().multiply(new BigDecimal(request.getQuantity()));
        String currency = product.getCurrency();

        // Validate balance and currency via Wallet Service
        BalanceValidationRequest validationRequest = new BalanceValidationRequest(
            request.getWalletId(),
            amount,
            currency,
            null // paymentId not yet created
        );

        BalanceValidationResponse validationResponse = walletServiceClient.validateBalance(validationRequest);

        if (!validationResponse.getStatus().equals("VALID")) {
            String failureReason = "Validation failed: " + 
                (!validationResponse.getHasSufficientBalance() ? "Insufficient balance" : "Currency not supported");
            
            auditService.logAudit("PAYMENT", 0L, "PAYMENT_VALIDATION", "FAILURE",
                failureReason, null, "FAILED", "SYSTEM");

            throw new RuntimeException(failureReason);
        }

        // Create payment with VALIDATED status (merged initiate + validate)
        Payment payment = new Payment();
        payment.setWalletAccountId(request.getWalletId());
        payment.setMerchantId(request.getMerchantId());
        payment.setProductId(request.getProductId());
        payment.setQuantity(request.getQuantity());
        payment.setAmount(amount);
        payment.setCurrency(currency);
        payment.setStatus("VALIDATED");
        payment.setCorrelationId(UUID.randomUUID().toString());

        payment = paymentRepository.save(payment);

        auditService.logAudit("PAYMENT", payment.getId(), "PAYMENT_INITIATED_AND_VALIDATED", "SUCCESS",
            "Payment initiated and validated for merchant: " + request.getMerchantId() + 
            ", product: " + product.getName() + ", quantity: " + request.getQuantity() + 
            ", amount: " + amount, null, "Status: VALIDATED", "SYSTEM");

        logger.info("Payment initiated and validated with paymentId: {}", payment.getId());
        return new PaymentInitiateResponse(payment.getId(), payment.getStatus());
    }

    @Transactional
    public ProcessPaymentResponse processPayment(Long paymentId, ProcessPaymentRequest request) {
        logger.info("Processing payment: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));

        if (!payment.getStatus().equals("VALIDATED")) {
            throw new RuntimeException("Payment must be in VALIDATED status. Current: " + payment.getStatus());
        }

        // Verify walletId matches
        if (!payment.getWalletAccountId().equals(request.getWalletId())) {
            throw new RuntimeException("Wallet ID mismatch. Payment wallet: " + 
                payment.getWalletAccountId() + ", Request wallet: " + request.getWalletId());
        }

        try {
            // Calculate wallet fee (2% of amount)
            BigDecimal walletFee = payment.getAmount().multiply(WALLET_FEE_PERCENTAGE);
            payment.setWalletFeeAmount(walletFee);

            // Debit wallet via Wallet Service
            DebitRequest debitRequest = new DebitRequest(
                paymentId,
                payment.getAmount(),
                payment.getCurrency()
            );

            DebitResponse debitResponse = walletServiceClient.debitWallet(
                request.getWalletId(), debitRequest);

            if (!debitResponse.getStatus().equals("DEBIT_SUCCESS")) {
                throw new RuntimeException("Wallet debit failed");
            }

            // Create wallet transaction ledger entry (local copy)
            WalletTransaction transaction = new WalletTransaction();
            transaction.setWalletAccountId(request.getWalletId());
            transaction.setPaymentId(paymentId);
            transaction.setEntryType("DEBIT");
            transaction.setPurpose("PAYMENT");
            transaction.setAmount(payment.getAmount());
            transaction.setCurrency(payment.getCurrency());
            // Note: balance_before and balance_after would ideally come from wallet service
            transaction.setBalanceBefore(debitResponse.getBalanceAfter().add(payment.getAmount()));
            transaction.setBalanceAfter(debitResponse.getBalanceAfter());
            walletTransactionRepository.save(transaction);

            auditService.logAudit("WALLET_ACCOUNT", request.getWalletId(), "WALLET_DEBITED", "SUCCESS",
                "Wallet debited for payment: " + paymentId, null, "Balance: " + debitResponse.getBalanceAfter(), "SYSTEM");

            // Create settlement
            Settlement settlement = new Settlement();
            settlement.setPaymentId(paymentId);
            settlement.setMerchantId(payment.getMerchantId());
            settlement.setGrossAmount(payment.getAmount());
            settlement.setWalletFeeAmount(walletFee);
            settlement.setNetAmount(payment.getAmount().subtract(walletFee));
            settlement.setCurrency(payment.getCurrency());
            settlement.setStatus("PENDING");
            settlement = settlementRepository.save(settlement);

            auditService.logAudit("SETTLEMENT", settlement.getId(), "SETTLEMENT_CREATED", "SUCCESS",
                "Settlement created for payment: " + paymentId, null, "Status: PENDING", "SYSTEM");

            // Update payment status
            payment.setStatus("COMPLETED");
            payment.setCompletedAt(LocalDateTime.now());
            payment = paymentRepository.save(payment);

            auditService.logAudit("PAYMENT", paymentId, "PAYMENT_COMPLETED", "SUCCESS",
                "Payment processed successfully", "VALIDATED", "COMPLETED", "SYSTEM");

            // Publish notification event to Kafka
            PaymentNotificationEvent event = new PaymentNotificationEvent(
                paymentId,
                payment.getMerchantId(),
                payment.getWalletAccountId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus()
            );

            notificationProducer.sendPaymentNotification(event);

            auditService.logAudit("NOTIFICATION", paymentId, "NOTIFICATION_PUBLISHED", "SUCCESS",
                "Payment notification event published to Kafka", null, "Topic: payment-notifications", "SYSTEM");

            logger.info("Payment processed successfully: {}", paymentId);
            return new ProcessPaymentResponse(paymentId, payment.getStatus(), walletFee);

        } catch (Exception e) {
            logger.error("Error processing payment: {}", paymentId, e);
            
            payment.setStatus("FAILED");
            payment.setFailureReason(e.getMessage());
            paymentRepository.save(payment);

            auditService.logAudit("PAYMENT", paymentId, "PAYMENT_PROCESSING", "FAILURE",
                "Payment processing failed: " + e.getMessage(), "VALIDATED", "FAILED", "SYSTEM");

            throw new RuntimeException("Payment processing failed: " + e.getMessage(), e);
        }
    }

    public PaymentStatusResponse getPaymentStatus(Long paymentId) {
        logger.info("Getting payment status for paymentId: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));

        Optional<Settlement> settlementOpt = settlementRepository.findByPaymentId(paymentId);
        BigDecimal netAmount = settlementOpt.map(Settlement::getNetAmount).orElse(BigDecimal.ZERO);

        PaymentStatusResponse response = new PaymentStatusResponse();
        response.setPaymentId(payment.getId());
        response.setStatus(payment.getStatus());
        response.setAmount(payment.getAmount());
        response.setCurrency(payment.getCurrency());
        response.setMerchantId(payment.getMerchantId());
        response.setWalletFeeAmount(payment.getWalletFeeAmount());
        response.setNetAmountToMerchant(netAmount);
        response.setFailureReason(payment.getFailureReason());
        response.setInitiatedAt(payment.getInitiatedAt());
        response.setCompletedAt(payment.getCompletedAt());

        return response;
    }
}

