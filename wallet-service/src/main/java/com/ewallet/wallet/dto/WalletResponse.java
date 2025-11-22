package com.ewallet.wallet.dto;

import java.math.BigDecimal;

public class WalletResponse {
    private Long customerId;
    private Long walletId;
    private String currency;
    private BigDecimal balance;
    private String status;

    public WalletResponse() {
    }

    public WalletResponse(Long customerId, Long walletId, String currency, BigDecimal balance, String status) {
        this.customerId = customerId;
        this.walletId = walletId;
        this.currency = currency;
        this.balance = balance;
        this.status = status;
    }

    // Getters and Setters
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

