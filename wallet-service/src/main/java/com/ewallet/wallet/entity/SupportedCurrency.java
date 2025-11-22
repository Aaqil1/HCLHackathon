package com.ewallet.wallet.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "supported_currencies")
public class SupportedCurrency {
    @Id
    @Column(length = 3)
    private String code;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Getters and Setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}

