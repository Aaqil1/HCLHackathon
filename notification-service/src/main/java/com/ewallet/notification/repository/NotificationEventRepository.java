package com.ewallet.notification.repository;

import com.ewallet.notification.entity.NotificationEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationEventRepository extends JpaRepository<NotificationEvent, Long> {
    List<NotificationEvent> findByPaymentId(Long paymentId);
    List<NotificationEvent> findByStatus(String status);
}

