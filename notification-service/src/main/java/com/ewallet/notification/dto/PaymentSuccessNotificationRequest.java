package com.ewallet.notification.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class PaymentSuccessNotificationRequest {
    @NotNull(message = "paymentId is required")
    private Long paymentId;

    @NotNull(message = "merchantId is required")
    private Long merchantId;

    @NotNull(message = "amount is required")
    private BigDecimal amount;

    public PaymentSuccessNotificationRequest() {
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}

