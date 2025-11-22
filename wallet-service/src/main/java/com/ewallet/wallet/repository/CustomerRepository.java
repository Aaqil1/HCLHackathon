package com.ewallet.wallet.repository;

import com.ewallet.wallet.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByIdAndStatus(Long id, String status);
}

