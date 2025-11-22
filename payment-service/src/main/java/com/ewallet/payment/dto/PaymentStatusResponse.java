package com.ewallet.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentStatusResponse {
    private Long paymentId;
    private String status;
    private BigDecimal amount;
    private String currency;
    private Long merchantId;
    private BigDecimal walletFeeAmount;
    private BigDecimal netAmountToMerchant;
    private String failureReason;
    private LocalDateTime initiatedAt;
    private LocalDateTime completedAt;

    public PaymentStatusResponse() {
    }

    // Getters and Setters
    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public BigDecimal getWalletFeeAmount() {
        return walletFeeAmount;
    }

    public void setWalletFeeAmount(BigDecimal walletFeeAmount) {
        this.walletFeeAmount = walletFeeAmount;
    }

    public BigDecimal getNetAmountToMerchant() {
        return netAmountToMerchant;
    }

    public void setNetAmountToMerchant(BigDecimal netAmountToMerchant) {
        this.netAmountToMerchant = netAmountToMerchant;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public LocalDateTime getInitiatedAt() {
        return initiatedAt;
    }

    public void setInitiatedAt(LocalDateTime initiatedAt) {
        this.initiatedAt = initiatedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}

