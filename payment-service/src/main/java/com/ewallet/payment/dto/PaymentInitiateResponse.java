package com.ewallet.payment.dto;

public class PaymentInitiateResponse {
    private Long paymentId;
    private String status;

    public PaymentInitiateResponse() {
    }

    public PaymentInitiateResponse(Long paymentId, String status) {
        this.paymentId = paymentId;
        this.status = status;
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
}

