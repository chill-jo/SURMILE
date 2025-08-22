package com.example.surveyapp.domain.payment.infrastructure;

import com.example.surveyapp.domain.payment.domain.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
}
