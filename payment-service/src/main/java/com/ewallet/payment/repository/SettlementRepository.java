package com.ewallet.payment.repository;

import com.ewallet.payment.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {
    Optional<Settlement> findByPaymentId(Long paymentId);
}

