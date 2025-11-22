package com.ewallet.payment.repository;

import com.ewallet.payment.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    Optional<Merchant> findByIdAndStatus(Long id, String status);
}

