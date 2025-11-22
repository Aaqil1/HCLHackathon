package com.ewallet.wallet.controller;

import com.ewallet.wallet.dto.*;
import com.ewallet.wallet.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallets")
@Tag(name = "Wallet API", description = "APIs for wallet management")
public class WalletController {
    private static final Logger logger = LoggerFactory.getLogger(WalletController.class);

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/{customerId}")
    @Operation(summary = "Get wallet details by customer ID")
    public ResponseEntity<WalletResponse> getWalletByCustomerId(@PathVariable Long customerId) {
        logger.info("GET /wallets/{}", customerId);
        try {
            WalletResponse response = walletService.getWalletByCustomerId(customerId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("Error fetching wallet for customerId: {}", customerId, e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/validate-balance")
    @Operation(summary = "Validate wallet balance and currency")
    public ResponseEntity<BalanceValidationResponse> validateBalance(
            @Valid @RequestBody BalanceValidationRequest request) {
        logger.info("POST /wallets/validate-balance - walletId: {}, amount: {}", 
            request.getWalletId(), request.getAmount());
        try {
            BalanceValidationResponse response = walletService.validateBalance(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error validating balance", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{walletId}/debit")
    @Operation(summary = "Debit wallet for a payment")
    public ResponseEntity<DebitResponse> debitWallet(
            @PathVariable Long walletId,
            @Valid @RequestBody DebitRequest request) {
        logger.info("POST /wallets/{}/debit - paymentId: {}, amount: {}", 
            walletId, request.getPaymentId(), request.getAmount());
        try {
            DebitResponse response = walletService.debitWallet(walletId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("Error debiting wallet: {}", walletId, e);
            if (e.getMessage().contains("not found") || e.getMessage().contains("not active")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            if (e.getMessage().contains("Insufficient") || e.getMessage().contains("Currency mismatch")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.badRequest().build();
        }
    }
}

