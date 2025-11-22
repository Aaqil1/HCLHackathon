package com.ewallet.wallet.service;

import com.ewallet.wallet.dto.*;
import com.ewallet.wallet.entity.*;
import com.ewallet.wallet.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class WalletService {
    private static final Logger logger = LoggerFactory.getLogger(WalletService.class);

    private final WalletAccountRepository walletAccountRepository;
    private final CustomerRepository customerRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final SupportedCurrencyRepository supportedCurrencyRepository;

    public WalletService(WalletAccountRepository walletAccountRepository,
                        CustomerRepository customerRepository,
                        WalletTransactionRepository walletTransactionRepository,
                        SupportedCurrencyRepository supportedCurrencyRepository) {
        this.walletAccountRepository = walletAccountRepository;
        this.customerRepository = customerRepository;
        this.walletTransactionRepository = walletTransactionRepository;
        this.supportedCurrencyRepository = supportedCurrencyRepository;
    }

    public WalletResponse getWalletByCustomerId(Long customerId) {
        logger.info("Fetching wallet for customerId: {}", customerId);
        
        Optional<Customer> customerOpt = customerRepository.findByIdAndStatus(customerId, "ACTIVE");
        if (customerOpt.isEmpty()) {
            throw new RuntimeException("Customer not found or inactive: " + customerId);
        }

        Optional<WalletAccount> walletOpt = walletAccountRepository.findByCustomerId(customerId);
        if (walletOpt.isEmpty()) {
            throw new RuntimeException("Wallet not found for customer: " + customerId);
        }

        WalletAccount wallet = walletOpt.get();
        return new WalletResponse(
            customerId,
            wallet.getId(),
            wallet.getCurrency(),
            wallet.getBalanceAmount(),
            wallet.getStatus()
        );
    }

    public BalanceValidationResponse validateBalance(BalanceValidationRequest request) {
        logger.info("Validating balance for walletId: {}, amount: {}, currency: {}", 
            request.getWalletId(), request.getAmount(), request.getCurrency());

        Optional<WalletAccount> walletOpt = walletAccountRepository.findById(request.getWalletId());
        if (walletOpt.isEmpty()) {
            return new BalanceValidationResponse("INVALID", false, false);
        }

        WalletAccount wallet = walletOpt.get();
        
        // Check currency support
        Optional<SupportedCurrency> currencyOpt = supportedCurrencyRepository.findByCodeAndIsActiveTrue(request.getCurrency());
        boolean isCurrencySupported = currencyOpt.isPresent() && 
            wallet.getCurrency().equals(request.getCurrency());

        // Check balance
        boolean hasSufficientBalance = wallet.getBalanceAmount().compareTo(request.getAmount()) >= 0;

        String status = (hasSufficientBalance && isCurrencySupported) ? "VALID" : "INVALID";

        return new BalanceValidationResponse(status, hasSufficientBalance, isCurrencySupported);
    }

    @Transactional
    public DebitResponse debitWallet(Long walletId, DebitRequest request) {
        logger.info("Debiting walletId: {} for paymentId: {}, amount: {}", 
            walletId, request.getPaymentId(), request.getAmount());

        Optional<WalletAccount> walletOpt = walletAccountRepository.findByIdWithLock(walletId);
        if (walletOpt.isEmpty()) {
            throw new RuntimeException("Wallet not found: " + walletId);
        }

        WalletAccount wallet = walletOpt.get();
        
        if (!wallet.getStatus().equals("ACTIVE")) {
            throw new RuntimeException("Wallet is not active: " + walletId);
        }

        if (!wallet.getCurrency().equals(request.getCurrency())) {
            throw new RuntimeException("Currency mismatch. Wallet currency: " + wallet.getCurrency());
        }

        BigDecimal balanceBefore = wallet.getBalanceAmount();
        if (balanceBefore.compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance. Available: " + balanceBefore + ", Required: " + request.getAmount());
        }

        BigDecimal balanceAfter = balanceBefore.subtract(request.getAmount());
        wallet.setBalanceAmount(balanceAfter);
        walletAccountRepository.save(wallet);

        // Create ledger entry
        WalletTransaction transaction = new WalletTransaction();
        transaction.setWalletAccountId(walletId);
        transaction.setPaymentId(request.getPaymentId());
        transaction.setEntryType("DEBIT");
        transaction.setPurpose("PAYMENT");
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(balanceAfter);
        walletTransactionRepository.save(transaction);

        logger.info("Wallet debited successfully. Balance before: {}, after: {}", balanceBefore, balanceAfter);

        return new DebitResponse("DEBIT_SUCCESS", balanceAfter);
    }
}

