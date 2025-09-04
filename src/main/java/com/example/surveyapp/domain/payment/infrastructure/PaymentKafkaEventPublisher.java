package com.example.surveyapp.domain.payment.infrastructure;

import com.example.surveyapp.domain.payment.application.PaymentEventPublisher;
import com.example.surveyapp.domain.payment.domain.event.PointChargeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentKafkaEventPublisher implements PaymentEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publishEvent(PointChargeEvent event) {
        kafkaTemplate.send("payment-charge-created", event);
    }
}
