package com.ewallet.payment.dto;

import java.math.BigDecimal;

public class ProcessPaymentResponse {
    private Long paymentId;
    private String status;
    private BigDecimal walletFeeAmount;

    public ProcessPaymentResponse() {
    }

    public ProcessPaymentResponse(Long paymentId, String status, BigDecimal walletFeeAmount) {
        this.paymentId = paymentId;
        this.status = status;
        this.walletFeeAmount = walletFeeAmount;
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

    public BigDecimal getWalletFeeAmount() {
        return walletFeeAmount;
    }

    public void setWalletFeeAmount(BigDecimal walletFeeAmount) {
        this.walletFeeAmount = walletFeeAmount;
    }
}

