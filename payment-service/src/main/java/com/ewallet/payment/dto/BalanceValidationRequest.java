package com.ewallet.payment.dto;

import java.math.BigDecimal;

public class BalanceValidationRequest {
    private Long walletId;
    private BigDecimal amount;
    private String currency;
    private Long paymentId;

    public BalanceValidationRequest() {
    }

    public BalanceValidationRequest(Long walletId, BigDecimal amount, String currency, Long paymentId) {
        this.walletId = walletId;
        this.amount = amount;
        this.currency = currency;
        this.paymentId = paymentId;
    }

    // Getters and Setters
    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }
}

