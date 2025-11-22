package com.ewallet.wallet.dto;

public class BalanceValidationResponse {
    private String status;
    private Boolean hasSufficientBalance;
    private Boolean isCurrencySupported;

    public BalanceValidationResponse() {
    }

    public BalanceValidationResponse(String status, Boolean hasSufficientBalance, Boolean isCurrencySupported) {
        this.status = status;
        this.hasSufficientBalance = hasSufficientBalance;
        this.isCurrencySupported = isCurrencySupported;
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

