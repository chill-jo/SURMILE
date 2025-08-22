package com.example.surveyapp.domain.payment.infrastructure;

import com.example.surveyapp.domain.payment.domain.model.entity.PaymentOutbox;
import com.example.surveyapp.domain.payment.domain.model.enums.PaymentOutboxEnum;
import com.example.surveyapp.domain.payment.domain.repository.PaymentOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PaymentOutboxRepositoryImpl implements PaymentOutboxRepository {

    private final PaymentOutboxJpaRepository paymentOutboxJpaRepository;

    @Override
    public List<PaymentOutbox> findByStatusAndPublished(PaymentOutboxEnum status, boolean published) {
        return paymentOutboxJpaRepository.findByStatusAndPublished(status,published);
    }

    @Override
    public PaymentOutbox save(PaymentOutbox paymentOutbox) {
        return paymentOutboxJpaRepository.save(paymentOutbox);
    }
}
