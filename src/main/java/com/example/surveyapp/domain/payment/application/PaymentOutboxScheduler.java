package com.example.surveyapp.domain.payment.application;

import com.example.surveyapp.domain.payment.domain.event.PointChargeEvent;
import com.example.surveyapp.domain.payment.domain.model.entity.PaymentOutbox;
import com.example.surveyapp.domain.payment.domain.model.enums.PaymentOutboxEnum;
import com.example.surveyapp.domain.payment.domain.repository.PaymentOutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentOutboxScheduler {

    private final ObjectMapper objectMapper;
    private final PaymentOutboxRepository paymentOutboxRepository;
    private final PaymentEventPublisher eventPublisher;

    private static final int MAX_RETRY = 5;

    @Scheduled(fixedDelay = 10_000)
    @Transactional
    public void publishOutboxEvents() throws Exception {
        List<PaymentOutbox> unpublished = paymentOutboxRepository.findByStatusAndPublished(PaymentOutboxEnum.READY, false);

        for (PaymentOutbox paymentOutbox : unpublished) {
            try {
                eventPublisher.publishEvent(objectMapper.readValue(paymentOutbox.getPayload(), PointChargeEvent.class));
                paymentOutbox.markPublished();

            }catch (Exception e) {

                paymentOutbox.markFailed(MAX_RETRY);
            }
            paymentOutboxRepository.save(paymentOutbox);
        }

    }
}
