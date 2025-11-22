package com.ewallet.wallet.repository;

import com.ewallet.wallet.entity.SupportedCurrency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupportedCurrencyRepository extends JpaRepository<SupportedCurrency, String> {
    Optional<SupportedCurrency> findByCodeAndIsActiveTrue(String code);
}

