package com.ewallet.payment.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "settlements")
public class Settlement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    @Column(name = "merchant_id", nullable = false)
    private Long merchantId;

    @Column(name = "gross_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal grossAmount;

    @Column(name = "wallet_fee_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal walletFeeAmount;

    @Column(name = "net_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal netAmount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false, length = 30)
    private String status = "PENDING";

    @Column(name = "gateway_reference", length = 100)
    private String gatewayReference;

    @Column(name = "settlement_date")
    private LocalDateTime settlementDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public BigDecimal getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(BigDecimal grossAmount) {
        this.grossAmount = grossAmount;
    }

    public BigDecimal getWalletFeeAmount() {
        return walletFeeAmount;
    }

    public void setWalletFeeAmount(BigDecimal walletFeeAmount) {
        this.walletFeeAmount = walletFeeAmount;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGatewayReference() {
        return gatewayReference;
    }

    public void setGatewayReference(String gatewayReference) {
        this.gatewayReference = gatewayReference;
    }

    public LocalDateTime getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(LocalDateTime settlementDate) {
        this.settlementDate = settlementDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

