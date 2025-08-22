package com.example.surveyapp.domain.payment.application.eventhandler;

import com.example.surveyapp.domain.payment.domain.model.entity.Payment;
import com.example.surveyapp.domain.payment.domain.repository.PaymentRepository;
import com.example.surveyapp.domain.point.domain.event.PointChargeFailedEvent;
import com.example.surveyapp.domain.point.domain.event.PointChargeSucceededEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventHandler {

    private final PaymentRepository paymentRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleChargeSuccessEvent(PointChargeSucceededEvent event){
        log.info("이벤트 받음");
        Payment payment = paymentRepository.findById(event.getPaymentId());

        payment.confirm();
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleChargeFailEvent(PointChargeFailedEvent event){
        Payment payment = paymentRepository.findById(event.getPaymentId());

        payment.fail();
    }
}
