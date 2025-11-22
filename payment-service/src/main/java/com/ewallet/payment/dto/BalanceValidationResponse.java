package com.ewallet.payment.dto;

public class BalanceValidationResponse {
    private String status;
    private Boolean hasSufficientBalance;
    private Boolean isCurrencySupported;

    public BalanceValidationResponse() {
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getHasSufficientBalance() {
        return hasSufficientBalance;
    }

    public void setHasSufficientBalance(Boolean hasSufficientBalance) {
        this.hasSufficientBalance = hasSufficientBalance;
    }

    public Boolean getIsCurrencySupported() {
        return isCurrencySupported;
    }

    public void setIsCurrencySupported(Boolean isCurrencySupported) {
        this.isCurrencySupported = isCurrencySupported;
    }
}

