package com.ewallet.payment.feign;

import com.ewallet.payment.dto.BalanceValidationRequest;
import com.ewallet.payment.dto.BalanceValidationResponse;
import com.ewallet.payment.dto.DebitRequest;
import com.ewallet.payment.dto.DebitResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "wallet-service", url = "${feign.wallet-service.url}")
public interface WalletServiceClient {
    
    @PostMapping("/wallets/validate-balance")
    BalanceValidationResponse validateBalance(@RequestBody BalanceValidationRequest request);
    
    @PostMapping("/wallets/{walletId}/debit")
    DebitResponse debitWallet(@PathVariable("walletId") Long walletId, @RequestBody DebitRequest request);
}

