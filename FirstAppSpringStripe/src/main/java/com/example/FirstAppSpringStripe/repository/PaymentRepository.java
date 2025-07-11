package com.example.FirstAppSpringStripe.repository;

import com.example.FirstAppSpringStripe.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByStripeSessionId(String stripeSessionId);
}
