package com.ewallet.payment.repository;

import com.ewallet.payment.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByIdAndStatus(Long id, String status);
    Optional<Product> findByIdAndMerchantIdAndStatus(Long id, Long merchantId, String status);
}

