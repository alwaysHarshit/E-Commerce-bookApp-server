package org.booknest.paymentservice.repo;

import org.booknest.paymentservice.entity.Payment;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment,Long> {

    Optional<Payment> findByOrderId(Long orderId);

    Optional<Payment> findByPaymentIntentId(String paymentIntentId);

    Page<Payment> findAll(Pageable pageable);
}
