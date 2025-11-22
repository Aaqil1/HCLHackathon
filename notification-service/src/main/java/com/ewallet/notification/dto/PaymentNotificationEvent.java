package com.ewallet.notification.dto;

import java.math.BigDecimal;

public class PaymentNotificationEvent {
    private Long paymentId;
    private Long merchantId;
    private Long walletAccountId;
    private BigDecimal amount;
    private String currency;
    private String status;

    public PaymentNotificationEvent() {
    }

    // Getters and Setters
    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public Long getWalletAccountId() {
        return walletAccountId;
    }

    public void setWalletAccountId(Long walletAccountId) {
        this.walletAccountId = walletAccountId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

