package com.example.surveyapp.domain.payment.infrastructure;

import com.example.surveyapp.domain.payment.domain.model.entity.Payment;
import com.example.surveyapp.domain.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    public Payment save(Payment payment){
        return paymentJpaRepository.save(payment);
    }

    public Payment findById(Long paymentId){
        return paymentJpaRepository.findById(paymentId)
                .orElseThrow();
    }
}
