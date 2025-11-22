package com.ewallet.wallet.dto;

import java.math.BigDecimal;

public class DebitResponse {
    private String status;
    private BigDecimal balanceAfter;

    public DebitResponse() {
    }

    public DebitResponse(String status, BigDecimal balanceAfter) {
        this.status = status;
        this.balanceAfter = balanceAfter;
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }
}

