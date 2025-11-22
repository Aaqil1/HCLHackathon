package com.ewallet.payment.dto;

import java.math.BigDecimal;

public class DebitResponse {
    private String status;
    private BigDecimal balanceAfter;

    public DebitResponse() {
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

