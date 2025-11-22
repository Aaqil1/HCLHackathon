package com.ewallet.wallet.repository;

import com.ewallet.wallet.entity.WalletAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface WalletAccountRepository extends JpaRepository<WalletAccount, Long> {
    Optional<WalletAccount> findByCustomerId(Long customerId);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM WalletAccount w WHERE w.id = :id")
    Optional<WalletAccount> findByIdWithLock(@Param("id") Long id);
}

