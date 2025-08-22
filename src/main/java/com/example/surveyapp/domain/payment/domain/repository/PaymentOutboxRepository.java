package com.example.surveyapp.domain.payment.domain.repository;

import com.example.surveyapp.domain.payment.domain.model.entity.PaymentOutbox;
import com.example.surveyapp.domain.payment.domain.model.enums.PaymentOutboxEnum;

import java.util.List;

public interface PaymentOutboxRepository {
    List<PaymentOutbox> findByStatusAndPublished(PaymentOutboxEnum status, boolean published);

    PaymentOutbox save(PaymentOutbox paymentOutbox);
}
