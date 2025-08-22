package com.example.surveyapp.domain.payment.infrastructure;

import com.example.surveyapp.domain.payment.domain.model.entity.PaymentOutbox;
import com.example.surveyapp.domain.payment.domain.model.enums.PaymentOutboxEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentOutboxJpaRepository extends JpaRepository<PaymentOutbox, Long> {
    List<PaymentOutbox> findByStatusAndPublished(PaymentOutboxEnum status, boolean published);
    PaymentOutbox save(PaymentOutbox paymentOutbox);

}
