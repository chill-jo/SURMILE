package com.example.surveyapp.domain.payment.domain.repository;

import com.example.surveyapp.domain.payment.domain.model.entity.Payment;

public interface PaymentRepository {
    Payment save(Payment payment);
    Payment findById(Long paymentId);
}
