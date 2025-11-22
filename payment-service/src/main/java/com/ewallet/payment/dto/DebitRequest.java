package com.ewallet.payment.dto;

import java.math.BigDecimal;

public class DebitRequest {
    private Long paymentId;
    private BigDecimal amount;
    private String currency;

    public DebitRequest() {
    }

    public DebitRequest(Long paymentId, BigDecimal amount, String currency) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.currency = currency;
    }

    // Getters and Setters
    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
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
}

