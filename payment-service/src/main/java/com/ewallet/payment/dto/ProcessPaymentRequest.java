package com.ewallet.payment.dto;

import jakarta.validation.constraints.NotNull;

public class ProcessPaymentRequest {
    @NotNull(message = "walletId is required")
    private Long walletId;

    @NotNull(message = "confirm is required")
    private Boolean confirm;

    public ProcessPaymentRequest() {
    }

    // Getters and Setters
    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public Boolean getConfirm() {
        return confirm;
    }

    public void setConfirm(Boolean confirm) {
        this.confirm = confirm;
    }
}

